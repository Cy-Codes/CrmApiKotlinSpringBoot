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
 * Domain model for Dynamics365 Incident entity.
 */
@Serializable
data class Incident(
    val incidentId: UUID = UUID(0L, 0L),
    val ticketNumber: String = "",
    val title: String = "",
    val customerid_Account: UUID? = null,
    val customerid_Contact: UUID? = null,
    val priorityCode: Int = 0,
    val severityCode: Int? = null,
    val caseTypeCode: Int? = null,
    val casesubject: String? = null,
    val description: String? = null,
    val ownerId: UUID = UUID(0L, 0L),
    val owningbusinessunit: UUID = UUID(0L, 0L),
    val stateCode: Int = 0,
    val statusCode: Int = 0,
    val createdOn: LocalDateTime = LocalDateTime.MIN,
    val createdBy: UUID = UUID(0L, 0L),
    val modifiedOn: LocalDateTime = LocalDateTime.MIN,
    val modifiedBy: UUID = UUID(0L, 0L),
)
