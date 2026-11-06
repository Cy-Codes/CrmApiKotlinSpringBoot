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
 * Domain model for Salesforce Campaign entity.
 */
@Serializable
data class Campaign(
    val id: String = "",
    val isDeleted: Boolean = false,
    val name: String = "",
    val parentId: String? = null,
    val type: String? = null,
    val status: String? = null,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val expectedRevenue: BigDecimal? = null,
    val budgetedCost: BigDecimal? = null,
    val actualCost: BigDecimal? = null,
    val expectedResponse: BigDecimal? = null,
    val numberSent: BigDecimal? = null,
    val isActive: Boolean = false,
    val description: String? = null,
    val numberOfLeads: Int = 0,
    val numberOfConvertedLeads: Int = 0,
    val numberOfContacts: Int = 0,
    val numberOfResponses: Int = 0,
    val numberOfOpportunities: Int = 0,
    val numberOfWonOpportunities: Int = 0,
    val amountAllOpportunities: BigDecimal = BigDecimal.ZERO,
    val amountWonOpportunities: BigDecimal = BigDecimal.ZERO,
    val ownerId: String = "",
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
