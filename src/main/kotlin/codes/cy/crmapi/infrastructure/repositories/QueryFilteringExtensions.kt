package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.infrastructure.compilers.SqlQuery
import java.math.BigDecimal
import java.util.UUID

private const val SQL_WILDCARD_CHAR = '%'
private val SAFE_IDENTIFIER_REGEX = Regex("^[a-zA-Z0-9_.]+$")
private val UUID_REGEX = Regex("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")

/**
 * Pure check validating whether a column or table identifier is safe against SQL injection.
 */
fun String?.isSafeSqlIdentifier(): Boolean =
    !this.isNullOrBlank() && SAFE_IDENTIFIER_REGEX.matches(this)

/**
 * Pure locale-invariant parser converting a raw query filter string into its strongly-typed scalar value.
 */
fun String.parseFilterValue(): Any =
    when {
        this.equals("true", ignoreCase = true) -> true
        this.equals("false", ignoreCase = true) -> false
        this.toIntOrNull() != null -> this.toInt()
        this.toLongOrNull() != null -> this.toLong()
        this.toBigDecimalOrNull() != null -> BigDecimal(this)
        UUID_REGEX.matches(this) -> UUID.fromString(this)
        else -> this
    }

/**
 * Applies valid field-level filters to a [SqlQuery] using a declarative functional fold.
 */
fun SqlQuery.applyFilters(filters: Map<String, String?>?): SqlQuery =
    if (filters.isNullOrEmpty()) {
        this
    } else {
        filters.entries
            .filter { (key, value) -> key.isSafeSqlIdentifier() && !value.isNullOrBlank() }
            .fold(this) { currentQuery, (key, value) -> currentQuery.applySingleFilter(key, value!!) }
    }

/**
 * Applies a single validated key/value predicate to a [SqlQuery].
 */
fun SqlQuery.applySingleFilter(column: String, rawValue: String): SqlQuery =
    if (rawValue.contains(SQL_WILDCARD_CHAR)) {
        this.whereLike(column, rawValue)
    } else {
        this.where(column, rawValue.parseFilterValue())
    }

/**
 * Applies validated sorting order to a [SqlQuery].
 */
fun SqlQuery.applySorting(
    sortBy: String?,
    sortDirection: String,
    defaultSortColumn: String = QueryOptions.DEFAULT_ID_COLUMN,
): SqlQuery {
    val sortColumn = if (sortBy.isSafeSqlIdentifier()) sortBy!! else defaultSortColumn
    val isDescending = sortDirection.equals(QueryOptions.DESCENDING_SORT_DIRECTION, ignoreCase = true)
    return if (isDescending) this.orderByDesc(sortColumn) else this.orderBy(sortColumn)
}

/**
 * Applies 1-based page offset and limit to a [SqlQuery].
 */
fun SqlQuery.applyPagination(page: Int, pageSize: Int): SqlQuery =
    this.skip((page - 1) * pageSize).take(pageSize)
