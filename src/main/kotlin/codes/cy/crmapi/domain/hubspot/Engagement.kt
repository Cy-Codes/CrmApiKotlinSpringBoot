@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.hubspot

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for HubSpot Engagement entity.
 */
@Serializable
data class Engagement(
    val engagementId: Long = 0L,
    val engagementType: String = "",
    val ownerId: Long? = null,
    val timestamp: LocalDateTime = LocalDateTime.MIN,
    val body: String? = null,
    val status: String? = null,
    val activityDurationMilliseconds: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val archived: Boolean = false
)
