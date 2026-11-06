package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.infrastructure.compilers.ISqlCompilerFactory
import codes.cy.crmapi.infrastructure.compilers.SqlQuery
import codes.cy.crmapi.infrastructure.connections.IDbConnectionFactory
import java.sql.Connection
import kotlin.reflect.KClass
import org.springframework.stereotype.Repository

/**
 * Generic repository utilizing multi-dialect [SqlQuery] compilation and lightweight [JdbcDataMapper] execution.
 */
@Repository
class BaseRepository(
    private val connectionFactory: IDbConnectionFactory,
    private val compilerFactory: ISqlCompilerFactory,
) : IBaseRepository {
    override fun <T : Any> getPaged(
        provider: DatabaseProvider,
        table: String,
        options: QueryOptions,
        klass: KClass<T>,
        defaultSortColumn: String,
        configureQuery: ((SqlQuery) -> SqlQuery)?,
    ): PagedResult<T> {
        val page = options.normalizedPage
        val pageSize = options.normalizedPageSize
        val compiler = compilerFactory.getCompiler(provider)

        // 1. Pure Query AST Construction: Base Filtering
        val filteredQuery = SqlQuery(table).applyFilters(options.filters)
        val baseQuery = configureQuery?.invoke(filteredQuery) ?: filteredQuery

        // 2. Pure Query AST Compilation: Count & Data Queries
        val compiledCount = compiler.compile(baseQuery.asCount())
        val compiledData = compiler.compile(
            baseQuery
                .applySorting(options.sortBy, options.sortDirection, defaultSortColumn)
                .applyPagination(page, pageSize),
        )

        // 3. Explicit Side-Effecting I/O Execution
        return executeWithOpenConnection(provider) { connection ->
            val totalCount = JdbcDataMapper.queryScalarInt(connection, compiledCount)
            val items = JdbcDataMapper.queryList(connection, compiledData, klass)
            PagedResult(items, totalCount, page, pageSize)
        }
    }

    override fun <T : Any> getById(
        provider: DatabaseProvider,
        table: String,
        idColumnName: String,
        idValue: Any,
        klass: KClass<T>,
    ): T? {
        require(idColumnName.isSafeSqlIdentifier()) {
            "Invalid identifier column name: $idColumnName"
        }

        val query = SqlQuery(table).where(idColumnName, idValue)
        val compiled = compilerFactory.getCompiler(provider).compile(query)

        return executeWithOpenConnection(provider) { connection ->
            JdbcDataMapper.queryFirstOrNull(connection, compiled, klass)
        }
    }

    override fun <T : Any> getList(
        provider: DatabaseProvider,
        table: String,
        klass: KClass<T>,
        configureQuery: ((SqlQuery) -> SqlQuery)?,
    ): List<T> {
        val baseQuery = SqlQuery(table)
        val query = configureQuery?.invoke(baseQuery) ?: baseQuery
        val compiled = compilerFactory.getCompiler(provider).compile(query)

        return executeWithOpenConnection(provider) { connection ->
            JdbcDataMapper.queryList(connection, compiled, klass)
        }
    }

    /**
     * Side-effecting I/O boundary: opens a provider-specific [Connection], executes the operation, and closes the connection.
     */
    private fun <TResult> executeWithOpenConnection(
        provider: DatabaseProvider,
        operation: (Connection) -> TResult,
    ): TResult =
        connectionFactory.createConnection(provider).use { connection ->
            operation(connection)
        }
}
