@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.dynamics365

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Dynamics365 Opportunity entity.
 */
@Serializable
data class Opportunity(
    val opportunityId: UUID = UUID(0L, 0L),
    val name: String = "",
    val parentAccountId: UUID? = null,
    val parentcontactid: UUID? = null,
    val priceLevelId: UUID? = null,
    val originatingLeadId: UUID? = null,
    val estimatedValue: BigDecimal? = null,
    val actualValue: BigDecimal? = null,
    val estimatedCloseDate: LocalDateTime? = null,
    val actualCloseDate: LocalDateTime? = null,
    val closeProbability: Int? = null,
    val stepname: String? = null,
    val salesstage: Int? = null,
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
