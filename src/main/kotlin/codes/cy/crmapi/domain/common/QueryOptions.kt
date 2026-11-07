package codes.cy.crmapi.domain.common

/**
 * Immutable query options for pagination, sorting, and field-level filtering.
 */
data class QueryOptions(
    val page: Int = DEFAULT_PAGE,
    val pageSize: Int = DEFAULT_PAGE_SIZE,
    val sortBy: String? = null,
    val sortDirection: String = DEFAULT_SORT_DIRECTION,
    val filters: Map<String, String?>? = null
) {
    /**
     * Pure calculation returning a valid 1-based page number clamped to [DEFAULT_PAGE, MAX_PAGE].
     */
    val normalizedPage: Int
        get() = if (page < DEFAULT_PAGE) DEFAULT_PAGE else if (page > MAX_PAGE) MAX_PAGE else page

    /**
     * Pure calculation clamping [pageSize] within [DEFAULT_PAGE, MAX_PAGE_SIZE].
     */
    val normalizedPageSize: Int
        get() = if (pageSize < DEFAULT_PAGE || pageSize > MAX_PAGE_SIZE) DEFAULT_PAGE_SIZE else pageSize

    companion object {
        const val DEFAULT_PAGE: Int = 1
        const val DEFAULT_PAGE_SIZE: Int = 20
        const val MAX_PAGE: Int = 100_000
        const val MAX_PAGE_SIZE: Int = 100
        const val DEFAULT_SORT_DIRECTION: String = "asc"
        const val DESCENDING_SORT_DIRECTION: String = "desc"
        const val DEFAULT_ID_COLUMN: String = "Id"

        /**
         * Shared immutable default query options instance.
         */
        val Default: QueryOptions = QueryOptions()
    }
}
