package codes.cy.crmapi.infrastructure.compilers

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.infrastructure.repositories.isSafeSqlIdentifier
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

    override fun compile(command: SqlCommand): CompiledSqlQuery {
        require(command.table.isSafeSqlIdentifier()) { "Invalid identifier table name: ${command.table}" }
        val (sql, bindings) = when (command) {
            is SqlCommand.Insert -> compileInsert(command)
            is SqlCommand.Update -> compileUpdate(command)
            is SqlCommand.Delete -> compileDelete(command)
        }
        return CompiledSqlQuery(
            sql = sql,
            bindings = bindings,
            namedBindings = bindings.mapIndexed { idx, value -> "@p$idx" to value }.toMap()
        )
    }

    private fun compileInsert(command: SqlCommand.Insert): Pair<String, List<Any?>> {
        require(command.values.isNotEmpty()) { "INSERT requires at least one column value." }
        val columns = command.values.keys.map { column -> quoteSafeColumn(column) }
        val placeholders = command.values.keys.indices.map { idx -> "@p$idx" }
        return Pair(
            "INSERT INTO ${quoteIdentifier(command.table)} (${columns.joinToString(", ")}) VALUES (${placeholders.joinToString(", ")})",
            command.values.values.toList()
        )
    }

    private fun compileUpdate(command: SqlCommand.Update): Pair<String, List<Any?>> {
        require(command.values.isNotEmpty()) { "UPDATE requires at least one column value." }
        require(command.whereClauses.isNotEmpty()) { "UPDATE requires a WHERE predicate." }
        val assignments = command.values.keys
            .mapIndexed { idx, column -> "${quoteSafeColumn(column)} = @p$idx" }
        val (whereSql, whereBindings) = compileEqualsPredicates(command.whereClauses, command.values.size)
        return Pair(
            "UPDATE ${quoteIdentifier(command.table)} SET ${assignments.joinToString(", ")}$whereSql",
            command.values.values.toList() + whereBindings
        )
    }

    private fun compileDelete(command: SqlCommand.Delete): Pair<String, List<Any?>> {
        require(command.whereClauses.isNotEmpty()) { "DELETE requires a WHERE predicate." }
        val (whereSql, whereBindings) = compileEqualsPredicates(command.whereClauses, 0)
        return Pair("DELETE FROM ${quoteIdentifier(command.table)}$whereSql", whereBindings)
    }

    private fun compileEqualsPredicates(
        clauses: List<WhereClause.EqualsClause>,
        startParamIndex: Int,
    ): Pair<String, List<Any?>> {
        val fragments = clauses.mapIndexed { idx, clause ->
            "${quoteSafeColumn(clause.column)} = @p${startParamIndex + idx}"
        }
        return Pair(" WHERE ${fragments.joinToString(" AND ")}", clauses.map { it.value })
    }

    private fun quoteSafeColumn(column: String): String {
        require(column.isSafeSqlIdentifier()) { "Invalid identifier column name: $column" }
        return quoteIdentifier(column)
    }

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
