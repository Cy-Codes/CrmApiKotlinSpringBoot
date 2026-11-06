package codes.cy.crmapi.domain.common

import kotlin.math.ceil
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Immutable envelope for paginated query results.
 */
@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class PagedResult<T>(
    val items: List<T>,
    val totalCount: Int,
    val page: Int,
    val pageSize: Int,
    @EncodeDefault
    val totalPages: Int = if (pageSize > 0) ceil(totalCount.toDouble() / pageSize.toDouble()).toInt() else 0,
    @EncodeDefault
    val hasPreviousPage: Boolean = page > QueryOptions.DEFAULT_PAGE,
    @EncodeDefault
    val hasNextPage: Boolean = page < totalPages,
)
