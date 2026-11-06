package codes.cy.crmapi.api.extensions

import codes.cy.crmapi.domain.common.QueryOptions
import org.springframework.http.ResponseEntity

private val RESERVED_QUERY_PARAMETER_NAMES = setOf(
    "page",
    "pagesize",
    "sortby",
    "sortdirection",
)

/**
 * Pure transformation converting an HTTP query parameter map and pagination parameters into an immutable [QueryOptions] data class.
 */
fun Map<String, String>.toQueryOptions(
    page: Int = QueryOptions.DEFAULT_PAGE,
    pageSize: Int = QueryOptions.DEFAULT_PAGE_SIZE,
    sortBy: String? = null,
    sortDirection: String = QueryOptions.DEFAULT_SORT_DIRECTION,
): QueryOptions {
    val filters = this.entries
        .filter { (key, _) -> !RESERVED_QUERY_PARAMETER_NAMES.contains(key.lowercase()) }
        .associate { (key, value) -> key to (value as String?) }

    return QueryOptions(page, pageSize, sortBy, sortDirection, filters)
}

/**
 * Pure transformation mapping a nullable entity into `404 Not Found` or `200 OK`.
 */
fun <T : Any> T?.toResponseEntity(): ResponseEntity<T> =
    if (this == null) ResponseEntity.notFound().build() else ResponseEntity.ok(this)

/**
 * Pure transformation wrapping a non-nullable result in `200 OK`.
 */
fun <T : Any> T.toOkResponseEntity(): ResponseEntity<T> =
    ResponseEntity.ok(this)
