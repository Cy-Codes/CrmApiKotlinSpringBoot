@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.dynamics365

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Dynamics365 PriceLevel entity.
 */
@Serializable
data class PriceLevel(
    val priceLevelId: UUID = UUID(0L, 0L),
    val name: String = "",
    val description: String? = null,
    val beginDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val stateCode: Int = 0,
    val statusCode: Int = 0,
    val createdOn: LocalDateTime = LocalDateTime.MIN,
    val createdBy: UUID = UUID(0L, 0L),
    val modifiedOn: LocalDateTime = LocalDateTime.MIN,
    val modifiedBy: UUID = UUID(0L, 0L),
)
