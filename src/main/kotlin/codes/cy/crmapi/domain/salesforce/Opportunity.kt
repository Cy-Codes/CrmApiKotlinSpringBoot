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
 * Domain model for Salesforce Opportunity entity.
 */
@Serializable
data class Opportunity(
    val id: String = "",
    val isDeleted: Boolean = false,
    val accountId: String? = null,
    val isPrivate: Boolean = false,
    val name: String = "",
    val description: String? = null,
    val stageName: String = "",
    val amount: BigDecimal? = null,
    val probability: BigDecimal? = null,
    val expectedRevenue: BigDecimal? = null,
    val closeDate: LocalDateTime = LocalDateTime.MIN,
    val type: String? = null,
    val leadSource: String? = null,
    val isClosed: Boolean = false,
    val isWon: Boolean = false,
    val forecastCategoryName: String = "",
    val campaignId: String? = null,
    val pricebook2Id: String? = null,
    val ownerId: String = "",
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
