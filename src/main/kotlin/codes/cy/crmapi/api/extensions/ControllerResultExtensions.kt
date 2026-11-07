package codes.cy.crmapi.api.extensions

import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.common.WriteRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

private const val LOCATION_ID_SEGMENT = "/{id}"

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

/**
 * Pure validation gate for request bodies: returns the request unchanged or throws [IllegalArgumentException] (`400 Bad Request`).
 */
fun <R : WriteRequest> R.validated(): R = also { it.validate() }

/**
 * Builds `201 Created` with a `Location` header of `<current request URI>/{id}` (the GET-by-id route) and this entity as body.
 */
fun <T : Any> T.toCreatedResponseEntity(id: Any): ResponseEntity<T> =
    ResponseEntity
        .created(ServletUriComponentsBuilder.fromCurrentRequestUri().path(LOCATION_ID_SEGMENT).buildAndExpand(id).toUri())
        .body(this)

/**
 * Maps a nullable entity into `200 OK`, or throws [NoSuchElementException] (rendered as `404` Problem Details) when absent.
 */
fun <T : Any> T?.toOkResponseEntityOrNotFound(resource: String, id: Any): ResponseEntity<T> =
    ResponseEntity.ok(this ?: throw NoSuchElementException(getNotFoundMessage(resource, id)))

/**
 * Maps a delete outcome into `204 No Content`, or throws [NoSuchElementException] (rendered as `404` Problem Details) when nothing was deleted.
 */
fun Boolean.toNoContentResponseEntityOrNotFound(resource: String, id: Any): ResponseEntity<Void> =
    if (this) ResponseEntity.noContent().build() else throw NoSuchElementException(getNotFoundMessage(resource, id))

private fun getNotFoundMessage(resource: String, id: Any): String = "$resource '$id' was not found."
