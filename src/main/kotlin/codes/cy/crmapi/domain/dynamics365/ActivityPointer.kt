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
 * Domain model for Dynamics365 ActivityPointer entity.
 */
@Serializable
data class ActivityPointer(
    val activityId: UUID = UUID(0L, 0L),
    val activityTypeCode: String = "",
    val subject: String = "",
    val description: String? = null,
    val regardingobjectid_Account: UUID? = null,
    val regardingobjectid_Contact: UUID? = null,
    val regardingobjectid_Opportunity: UUID? = null,
    val regardingobjectid_Incident: UUID? = null,
    val ownerId: UUID = UUID(0L, 0L),
    val owningbusinessunit: UUID = UUID(0L, 0L),
    val scheduledStart: LocalDateTime? = null,
    val scheduledEnd: LocalDateTime? = null,
    val actualStart: LocalDateTime? = null,
    val actualEnd: LocalDateTime? = null,
    val actualDurationMinutes: Int? = null,
    val stateCode: Int = 0,
    val statusCode: Int = 0,
    val createdOn: LocalDateTime = LocalDateTime.MIN,
    val createdBy: UUID = UUID(0L, 0L),
    val modifiedOn: LocalDateTime = LocalDateTime.MIN,
    val modifiedBy: UUID = UUID(0L, 0L),
)
