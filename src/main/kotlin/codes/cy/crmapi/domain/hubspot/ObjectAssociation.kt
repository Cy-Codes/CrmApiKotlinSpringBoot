@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.hubspot

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for HubSpot ObjectAssociation entity.
 */
@Serializable
data class ObjectAssociation(
    val associationId: Long = 0L,
    val fromObjectType: String = "",
    val fromObjectId: Long = 0L,
    val toObjectType: String = "",
    val toObjectId: Long = 0L,
    val associationTypeId: Int = 0,
    val createdAt: LocalDateTime = LocalDateTime.MIN
)
