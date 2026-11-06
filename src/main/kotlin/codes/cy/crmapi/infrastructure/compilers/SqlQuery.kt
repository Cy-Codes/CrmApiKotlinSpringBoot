package codes.cy.crmapi.infrastructure.compilers

/**
 * Immutable representation of a WHERE clause predicate in a [SqlQuery].
 */
sealed interface WhereClause {
    data class EqualsClause(val column: String, val value: Any?) : WhereClause
    data class LikeClause(val column: String, val pattern: String) : WhereClause
    data class InSubQueryClause(val column: String, val subQuery: SqlQuery) : WhereClause
}

/**
 * Immutable representation of an ORDER BY clause in a [SqlQuery].
 */
data class OrderByClause(
    val column: String,
    val descending: Boolean = false
)

/**
 * Immutable Query Object AST for composing dialect-independent relational queries.
 */
data class SqlQuery(
    val table: String,
    val selectedColumns: List<String> = emptyList(),
    val whereClauses: List<WhereClause> = emptyList(),
    val orderClauses: List<OrderByClause> = emptyList(),
    val offsetValue: Int? = null,
    val limitValue: Int? = null,
    val isCountQuery: Boolean = false
) {
    /**
     * Returns a new [SqlQuery] projecting the specified columns.
     */
    fun select(vararg columns: String): SqlQuery =
        copy(selectedColumns = columns.toList(), isCountQuery = false)

    /**
     * Returns a new [SqlQuery] with an equality predicate appended.
     */
    fun where(column: String, value: Any?): SqlQuery =
        copy(whereClauses = whereClauses + WhereClause.EqualsClause(column, value))

    /**
     * Returns a new [SqlQuery] with a LIKE predicate appended.
     */
    fun whereLike(column: String, pattern: String): SqlQuery =
        copy(whereClauses = whereClauses + WhereClause.LikeClause(column, pattern))

    /**
     * Returns a new [SqlQuery] with an IN (subquery) predicate appended.
     */
    fun whereIn(column: String, subQuery: SqlQuery): SqlQuery =
        copy(whereClauses = whereClauses + WhereClause.InSubQueryClause(column, subQuery))

    /**
     * Returns a new [SqlQuery] with an ascending ORDER BY clause appended.
     */
    fun orderBy(column: String): SqlQuery =
        copy(orderClauses = orderClauses + OrderByClause(column, descending = false))

    /**
     * Returns a new [SqlQuery] with a descending ORDER BY clause appended.
     */
    fun orderByDesc(column: String): SqlQuery =
        copy(orderClauses = orderClauses + OrderByClause(column, descending = true))

    /**
     * Returns a new [SqlQuery] with the specified row offset.
     */
    fun skip(offset: Int): SqlQuery =
        copy(offsetValue = offset)

    /**
     * Returns a new [SqlQuery] with the specified row limit.
     */
    fun take(limit: Int): SqlQuery =
        copy(limitValue = limit)

    /**
     * Returns a new [SqlQuery] configured as a `SELECT COUNT(*)` aggregation.
     */
    fun asCount(): SqlQuery =
        copy(
            isCountQuery = true,
            orderClauses = emptyList(),
            offsetValue = null,
            limitValue = null
        )

    /**
     * Returns an immutable copy of this [SqlQuery].
     */
    fun clone(): SqlQuery = copy()
}
