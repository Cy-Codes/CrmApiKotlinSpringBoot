@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.hubspot

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for HubSpot Ticket entity.
 */
@Serializable
data class Ticket(
    val ticketId: Long = 0L,
    val subject: String = "",
    val content: String? = null,
    val pipelineId: String = "",
    val ticketStatusStageId: String = "",
    val priority: String = "",
    val ownerId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val archived: Boolean = false
)
