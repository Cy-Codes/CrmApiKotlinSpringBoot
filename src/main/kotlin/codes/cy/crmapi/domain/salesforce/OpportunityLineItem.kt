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
 * Domain model for Salesforce OpportunityLineItem entity.
 */
@Serializable
data class OpportunityLineItem(
    val id: String = "",
    val opportunityId: String = "",
    val pricebookEntryId: String = "",
    val quantity: BigDecimal = BigDecimal.ZERO,
    val unitPrice: BigDecimal = BigDecimal.ZERO,
    val totalPrice: BigDecimal? = null,
    val listPrice: BigDecimal? = null,
    val description: String? = null,
    val isDeleted: Boolean = false,
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
