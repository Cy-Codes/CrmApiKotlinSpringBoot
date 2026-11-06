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
 * Domain model for Dynamics365 OpportunityProduct entity.
 */
@Serializable
data class OpportunityProduct(
    val opportunityProductId: UUID = UUID(0L, 0L),
    val opportunityId: UUID = UUID(0L, 0L),
    val productId: UUID? = null,
    val productDescription: String? = null,
    val pricePerUnit: BigDecimal = BigDecimal.ZERO,
    val quantity: BigDecimal = BigDecimal.ZERO,
    val extendedAmount: BigDecimal? = null,
    val manualDiscountAmount: BigDecimal = BigDecimal.ZERO,
    val createdOn: LocalDateTime = LocalDateTime.MIN,
    val createdBy: UUID = UUID(0L, 0L),
    val modifiedOn: LocalDateTime = LocalDateTime.MIN,
    val modifiedBy: UUID = UUID(0L, 0L),
)
