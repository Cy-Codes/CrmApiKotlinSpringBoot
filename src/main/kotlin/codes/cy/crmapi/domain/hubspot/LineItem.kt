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
 * Domain model for HubSpot LineItem entity.
 */
@Serializable
data class LineItem(
    val lineItemId: Long = 0L,
    val dealId: Long = 0L,
    val productId: Long? = null,
    val name: String = "",
    val quantity: BigDecimal = BigDecimal.ZERO,
    val price: BigDecimal = BigDecimal.ZERO,
    val amount: BigDecimal? = null,
    val discount: BigDecimal = BigDecimal.ZERO,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val archived: Boolean = false
)
