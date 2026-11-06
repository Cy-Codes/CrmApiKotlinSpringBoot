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
 * Domain model for Dynamics365 Product entity.
 */
@Serializable
data class Product(
    val productId: UUID = UUID(0L, 0L),
    val productNumber: String = "",
    val name: String = "",
    val description: String? = null,
    val price: BigDecimal? = null,
    val standardCost: BigDecimal? = null,
    val currentCost: BigDecimal? = null,
    val quantityOnHand: BigDecimal = BigDecimal.ZERO,
    val stateCode: Int = 0,
    val statusCode: Int = 0,
    val createdOn: LocalDateTime = LocalDateTime.MIN,
    val createdBy: UUID = UUID(0L, 0L),
    val modifiedOn: LocalDateTime = LocalDateTime.MIN,
    val modifiedBy: UUID = UUID(0L, 0L),
)
