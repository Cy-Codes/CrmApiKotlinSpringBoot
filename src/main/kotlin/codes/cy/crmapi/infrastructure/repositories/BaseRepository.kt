package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.common.DataConflictException
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.infrastructure.compilers.ISqlCompilerFactory
import codes.cy.crmapi.infrastructure.compilers.SqlCommand
import codes.cy.crmapi.infrastructure.compilers.SqlQuery
import codes.cy.crmapi.infrastructure.compilers.WhereClause
import codes.cy.crmapi.infrastructure.connections.IDbConnectionFactory
import java.sql.Connection
import java.sql.SQLException
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
            val prevAutoCommit = connection.autoCommit
            connection.autoCommit = false
            try {
                val totalCount = JdbcDataMapper.queryScalarInt(connection, compiledCount)
                val items = JdbcDataMapper.queryList(connection, compiledData, klass)
                connection.commit()
                PagedResult(items, totalCount, page, pageSize)
            } finally {
                connection.autoCommit = prevAutoCommit
            }
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
        val query = (configureQuery?.invoke(baseQuery) ?: baseQuery).limit(MAX_CHILD_RESULTS)
        val compiled = compilerFactory.getCompiler(provider).compile(query)

        return executeWithOpenConnection(provider) { connection ->
            JdbcDataMapper.queryList(connection, compiled, klass)
        }
    }

    override fun <T : Any> executeCreate(
        provider: DatabaseProvider,
        table: String,
        idColumnName: String,
        idValue: Any,
        values: Map<String, Any?>,
        klass: KClass<T>,
    ): T {
        requireSafeIdColumn(idColumnName)
        val compiler = compilerFactory.getCompiler(provider)
        val compiledInsert = compiler.compile(SqlCommand.Insert(table, values))
        val compiledReRead = compiler.compile(SqlQuery(table).where(idColumnName, idValue))

        return executeInTransaction(provider) { connection ->
            JdbcDataMapper.executeUpdate(connection, compiledInsert)
            checkNotNull(JdbcDataMapper.queryFirstOrNull(connection, compiledReRead, klass)) {
                "Row $idValue was not found in $table immediately after insert."
            }
        }
    }

    override fun <T : Any> executeUpdate(
        provider: DatabaseProvider,
        table: String,
        idColumnName: String,
        idValue: Any,
        values: Map<String, Any?>,
        klass: KClass<T>,
    ): T? {
        requireSafeIdColumn(idColumnName)
        val compiler = compilerFactory.getCompiler(provider)
        val compiledUpdate = compiler.compile(
            SqlCommand.Update(table, values, listOf(WhereClause.EqualsClause(idColumnName, idValue)))
        )
        val compiledReRead = compiler.compile(SqlQuery(table).where(idColumnName, idValue))

        return executeInTransaction(provider) { connection ->
            if (JdbcDataMapper.executeUpdate(connection, compiledUpdate) == 0) {
                null
            } else {
                JdbcDataMapper.queryFirstOrNull(connection, compiledReRead, klass)
            }
        }
    }

    override fun executeDelete(
        provider: DatabaseProvider,
        table: String,
        idColumnName: String,
        idValue: Any,
    ): Boolean {
        requireSafeIdColumn(idColumnName)
        val compiledDelete = compilerFactory.getCompiler(provider).compile(
            SqlCommand.Delete(table, listOf(WhereClause.EqualsClause(idColumnName, idValue)))
        )

        return executeInTransaction(provider) { connection ->
            JdbcDataMapper.executeUpdate(connection, compiledDelete) > 0
        }
    }

    private fun requireSafeIdColumn(idColumnName: String) =
        require(idColumnName.isSafeSqlIdentifier()) { "Invalid identifier column name: $idColumnName" }

    /**
     * Side-effecting I/O boundary for mutations: runs the operation in one transaction (commit on success, rollback on failure)
     * without automatic retries, and translates constraint violations into [DataConflictException].
     */
    private fun <TResult> executeInTransaction(
        provider: DatabaseProvider,
        operation: (Connection) -> TResult,
    ): TResult =
        try {
            connectionFactory.createConnection(provider).use { connection ->
                val prevAutoCommit = connection.autoCommit
                connection.autoCommit = false
                try {
                    operation(connection).also { connection.commit() }
                } catch (ex: Exception) {
                    runCatching { connection.rollback() }
                    throw ex
                } finally {
                    connection.autoCommit = prevAutoCommit
                }
            }
        } catch (ex: SQLException) {
            if (ex.isConstraintViolation()) throw DataConflictException(cause = ex) else throw ex
        }

    /**
     * Side-effecting I/O boundary: opens a provider-specific [Connection], executes the operation with transient fault retries, and closes the connection.
     */
    private fun <TResult> executeWithOpenConnection(
        provider: DatabaseProvider,
        operation: (Connection) -> TResult,
    ): TResult {
        var attempt = 1
        while (true) {
            try {
                return connectionFactory.createConnection(provider).use { connection ->
                    operation(connection)
                }
            } catch (ex: Exception) {
                if (attempt < MAX_RETRY_ATTEMPTS && isTransientException(ex)) {
                    val delayMs = INITIAL_RETRY_DELAY_MS * (1L shl (attempt - 1))
                    try {
                        Thread.sleep(delayMs)
                    } catch (_: InterruptedException) {
                        Thread.currentThread().interrupt()
                        throw ex
                    }
                    attempt++
                } else {
                    throw ex
                }
            }
        }
    }

    private fun isTransientException(ex: Throwable): Boolean =
        when (ex) {
            is java.sql.SQLTransientException -> true
            is java.sql.SQLException -> isTransientSqlState(ex.sqlState)
            is java.net.SocketTimeoutException, is java.net.SocketException -> true
            else -> ex.cause?.let { isTransientException(it) } ?: false
        }

    private fun isTransientSqlState(sqlState: String?): Boolean {
        if (sqlState == null) return false
        return sqlState.startsWith(PG_SQLSTATE_CONNECTION_PREFIX) ||
            sqlState == PG_SQLSTATE_SERIALIZATION_FAILURE ||
            sqlState == PG_SQLSTATE_ADMIN_SHUTDOWN
    }

    companion object {
        const val MAX_CHILD_RESULTS: Int = 1000
        private const val MAX_RETRY_ATTEMPTS: Int = 3
        private const val INITIAL_RETRY_DELAY_MS: Long = 50L
        private const val PG_SQLSTATE_CONNECTION_PREFIX: String = "08"
        private const val PG_SQLSTATE_SERIALIZATION_FAILURE: String = "40001"
        private const val PG_SQLSTATE_ADMIN_SHUTDOWN: String = "57P01"
    }
}
