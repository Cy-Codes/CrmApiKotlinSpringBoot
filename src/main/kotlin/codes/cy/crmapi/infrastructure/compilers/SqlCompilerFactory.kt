package codes.cy.crmapi.infrastructure.compilers

import codes.cy.crmapi.domain.common.DatabaseProvider
import org.springframework.stereotype.Component

/**
 * Factory interface providing the appropriate [ISqlCompiler] for a given [DatabaseProvider].
 */
interface ISqlCompilerFactory {
    /**
     * Returns the singleton [ISqlCompiler] for the target database provider.
     */
    fun getCompiler(provider: DatabaseProvider): ISqlCompiler
}

/**
 * Abstract compiler base with shared functional AST traversal logic.
 */
abstract class BaseSqlCompiler : ISqlCompiler {
    protected abstract fun quoteIdentifierSegment(segment: String): String

    protected abstract fun formatPagination(
        sqlWithoutPagination: String,
        hasOrderBy: Boolean,
        offset: Int?,
        limit: Int?,
        startParamIndex: Int
    ): Pair<String, List<Any?>>

    /**
     * Quotes a single or dot-qualified identifier (e.g., `Salesforce.Account` -> `[Salesforce].[Account]` or `"Salesforce"."Account"`).
     */
    protected fun quoteIdentifier(identifier: String): String =
        identifier
            .split(IDENTIFIER_SEPARATOR)
            .joinToString(IDENTIFIER_SEPARATOR.toString()) { segment -> quoteIdentifierSegment(segment) }

    override fun compile(query: SqlQuery): CompiledSqlQuery =
        compileInternal(query, initialParamIndex = 0)

    private fun compileInternal(query: SqlQuery, initialParamIndex: Int): CompiledSqlQuery {
        val selectProjection = when {
            query.isCountQuery -> "COUNT(*) AS ${quoteIdentifierSegment(COUNT_ALIAS)}"
            query.selectedColumns.isEmpty() -> ALL_COLUMNS_WILDCARD
            else -> query.selectedColumns.joinToString(", ") { col -> quoteIdentifier(col) }
        }

        val fromClause = "SELECT $selectProjection FROM ${quoteIdentifier(query.table)}"

        val (whereFragments, whereBindings) = query.whereClauses.fold(
            Pair(emptyList<String>(), emptyList<Any?>())
        ) { (fragments, bindings), clause ->
            val currentIndex = initialParamIndex + bindings.size
            when (clause) {
                is WhereClause.EqualsClause -> {
                    val paramName = "@p$currentIndex"
                    Pair(
                        fragments + "${quoteIdentifier(clause.column)} = $paramName",
                        bindings + clause.value
                    )
                }
                is WhereClause.LikeClause -> {
                    val paramName = "@p$currentIndex"
                    Pair(
                        fragments + "${quoteIdentifier(clause.column)} LIKE $paramName",
                        bindings + clause.pattern
                    )
                }
                is WhereClause.InSubQueryClause -> {
                    val compiledSub = compileInternal(clause.subQuery, currentIndex)
                    Pair(
                        fragments + "${quoteIdentifier(clause.column)} IN (${compiledSub.sql})",
                        bindings + compiledSub.bindings
                    )
                }
            }
        }

        val whereSql = if (whereFragments.isEmpty()) {
            ""
        } else {
            " WHERE ${whereFragments.joinToString(" AND ")}"
        }

        val orderSql = if (query.orderClauses.isEmpty()) {
            ""
        } else {
            val orders = query.orderClauses.joinToString(", ") { order ->
                val direction = if (order.descending) " DESC" else ""
                "${quoteIdentifier(order.column)}$direction"
            }
            " ORDER BY $orders"
        }

        val baseSql = "$fromClause$whereSql$orderSql"
        val (finalSql, paginationBindings) = formatPagination(
            sqlWithoutPagination = baseSql,
            hasOrderBy = query.orderClauses.isNotEmpty(),
            offset = query.offsetValue,
            limit = query.limitValue,
            startParamIndex = initialParamIndex + whereBindings.size
        )

        val allBindings = whereBindings + paginationBindings
        val namedBindings = allBindings
            .mapIndexed { idx, value -> "@p${initialParamIndex + idx}" to value }
            .toMap()

        return CompiledSqlQuery(
            sql = finalSql,
            bindings = allBindings,
            namedBindings = namedBindings
        )
    }

    companion object {
        private const val IDENTIFIER_SEPARATOR: Char = '.'
        private const val ALL_COLUMNS_WILDCARD: String = "*"
        private const val COUNT_ALIAS: String = "count"
    }
}

/**
 * T-SQL compiler producing bracketed identifiers (`[Schema].[Table]`) and `OFFSET ... FETCH NEXT` pagination.
 */
class SqlServerCompiler : BaseSqlCompiler() {
    override fun quoteIdentifierSegment(segment: String): String = "[$segment]"

    override fun formatPagination(
        sqlWithoutPagination: String,
        hasOrderBy: Boolean,
        offset: Int?,
        limit: Int?,
        startParamIndex: Int
    ): Pair<String, List<Any?>> {
        if (offset == null && limit == null) {
            return Pair(sqlWithoutPagination, emptyList())
        }
        val effectiveOffset = offset ?: 0
        val orderPrefix = if (hasOrderBy) "" else " ORDER BY (SELECT 0)"
        val offsetParam = "@p$startParamIndex"
        return if (limit != null) {
            val fetchParam = "@p${startParamIndex + 1}"
            Pair(
                "$sqlWithoutPagination$orderPrefix OFFSET $offsetParam ROWS FETCH NEXT $fetchParam ROWS ONLY",
                listOf(effectiveOffset, limit)
            )
        } else {
            Pair(
                "$sqlWithoutPagination$orderPrefix OFFSET $offsetParam ROWS",
                listOf(effectiveOffset)
            )
        }
    }
}

/**
 * PostgreSQL compiler producing ANSI double-quoted identifiers (`"Schema"."Table"`) and `LIMIT ... OFFSET` pagination.
 */
class PostgresCompiler : BaseSqlCompiler() {
    override fun quoteIdentifierSegment(segment: String): String = "\"$segment\""

    override fun formatPagination(
        sqlWithoutPagination: String,
        hasOrderBy: Boolean,
        offset: Int?,
        limit: Int?,
        startParamIndex: Int
    ): Pair<String, List<Any?>> =
        when {
            limit != null && offset != null -> {
                val limitParam = "@p$startParamIndex"
                val offsetParam = "@p${startParamIndex + 1}"
                Pair(
                    "$sqlWithoutPagination LIMIT $limitParam OFFSET $offsetParam",
                    listOf(limit, offset)
                )
            }
            limit != null -> {
                val limitParam = "@p$startParamIndex"
                Pair("$sqlWithoutPagination LIMIT $limitParam", listOf(limit))
            }
            offset != null -> {
                val offsetParam = "@p$startParamIndex"
                Pair("$sqlWithoutPagination OFFSET $offsetParam", listOf(offset))
            }
            else -> Pair(sqlWithoutPagination, emptyList())
        }
}

/**
 * Singleton compiler factory providing thread-safe instances of [SqlServerCompiler] and [PostgresCompiler].
 */
@Component
class SqlCompilerFactory : ISqlCompilerFactory {
    override fun getCompiler(provider: DatabaseProvider): ISqlCompiler =
        when (provider) {
            DatabaseProvider.Ms -> sqlServerCompilerInstance
            DatabaseProvider.Pg -> postgresCompilerInstance
        }

    companion object {
        private val sqlServerCompilerInstance: ISqlCompiler = SqlServerCompiler()
        private val postgresCompilerInstance: ISqlCompiler = PostgresCompiler()
    }
}
