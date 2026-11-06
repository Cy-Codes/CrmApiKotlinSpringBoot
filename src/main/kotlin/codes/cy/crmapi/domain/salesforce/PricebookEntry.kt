@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce PricebookEntry entity.
 */
@Serializable
data class PricebookEntry(
    val id: String = "",
    val pricebook2Id: String = "",
    val product2Id: String = "",
    val unitPrice: BigDecimal = BigDecimal.ZERO,
    val isActive: Boolean = false,
    val useStandardPrice: Boolean = false,
    val isDeleted: Boolean = false,
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
