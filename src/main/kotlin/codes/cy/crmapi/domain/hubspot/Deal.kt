@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.hubspot

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for HubSpot Deal entity.
 */
@Serializable
data class Deal(
    val dealId: Long = 0L,
    val dealName: String = "",
    val pipelineId: String = "",
    val dealStageId: String = "",
    val amount: BigDecimal? = null,
    val closedate: LocalDateTime? = null,
    val dealType: String? = null,
    val ownerId: Long? = null,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val archived: Boolean = false
)
