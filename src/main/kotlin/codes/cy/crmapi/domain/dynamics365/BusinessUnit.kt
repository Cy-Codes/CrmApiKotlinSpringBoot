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
 * Domain model for Dynamics365 BusinessUnit entity.
 */
@Serializable
data class BusinessUnit(
    val businessUnitId: UUID = UUID(0L, 0L),
    val name: String = "",
    val description: String? = null,
    val parentBusinessUnitId: UUID? = null,
    val isDisabled: Boolean = false,
    val createdOn: LocalDateTime = LocalDateTime.MIN,
    val modifiedOn: LocalDateTime = LocalDateTime.MIN,
)
