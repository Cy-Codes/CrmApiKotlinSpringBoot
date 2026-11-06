@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.hubspot

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for HubSpot Owner entity.
 */
@Serializable
data class Owner(
    val ownerId: Long = 0L,
    val email: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val userId: Long? = null,
    val isActive: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val archived: Boolean = false
)
