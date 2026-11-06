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
 * Domain model for Dynamics365 SystemUser entity.
 */
@Serializable
data class SystemUser(
    val systemUserId: UUID = UUID(0L, 0L),
    val businessUnitId: UUID = UUID(0L, 0L),
    val domainName: String = "",
    val fullName: String = "",
    val firstName: String? = null,
    val lastName: String = "",
    val internalEmailAddress: String? = null,
    val jobTitle: String? = null,
    val isDisabled: Boolean = false,
    val createdOn: LocalDateTime = LocalDateTime.MIN,
    val modifiedOn: LocalDateTime = LocalDateTime.MIN,
)
