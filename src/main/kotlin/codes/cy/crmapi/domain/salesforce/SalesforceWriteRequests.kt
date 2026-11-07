@file:UseSerializers(BigDecimalSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LenientLocalDateTimeSerializer
import codes.cy.crmapi.domain.common.WriteRequest
import codes.cy.crmapi.domain.common.requireNotBlankFields
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Writable columns of a Salesforce Account (create / full replace).
 * Server-owned: `id`, audit columns. `isDeleted` is optional (database default applies when omitted).
 */
@Serializable
data class AccountWriteRequest(
    val name: String,
    val ownerId: String,
    val parentId: String? = null,
    val type: String? = null,
    val billingStreet: String? = null,
    val billingCity: String? = null,
    val billingState: String? = null,
    val billingPostalCode: String? = null,
    val billingCountry: String? = null,
    val shippingStreet: String? = null,
    val shippingCity: String? = null,
    val shippingState: String? = null,
    val shippingPostalCode: String? = null,
    val shippingCountry: String? = null,
    val phone: String? = null,
    val fax: String? = null,
    val website: String? = null,
    val industry: String? = null,
    val annualRevenue: BigDecimal? = null,
    val numberOfEmployees: Int? = null,
    val description: String? = null,
    val isDeleted: Boolean? = null,
) : WriteRequest {
    override fun validate() = requireNotBlankFields("name" to name, "ownerId" to ownerId)
}

/**
 * Writable columns of a Salesforce Contact (create / full replace). `Name` is a generated column and is never written.
 */
@Serializable
data class ContactWriteRequest(
    val lastName: String,
    val ownerId: String,
    val accountId: String? = null,
    val firstName: String? = null,
    val salutation: String? = null,
    val reportsToId: String? = null,
    val title: String? = null,
    val department: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val mobilePhone: String? = null,
    val mailingStreet: String? = null,
    val mailingCity: String? = null,
    val mailingState: String? = null,
    val mailingPostalCode: String? = null,
    val mailingCountry: String? = null,
    val leadSource: String? = null,
    val description: String? = null,
    val isDeleted: Boolean? = null,
) : WriteRequest {
    override fun validate() = requireNotBlankFields("lastName" to lastName, "ownerId" to ownerId)
}

/**
 * Writable columns of a Salesforce Opportunity (create / full replace). `ExpectedRevenue` is a generated column and is never written.
 */
@Serializable
data class OpportunityWriteRequest(
    val name: String,
    val stageName: String,
    @Serializable(with = LenientLocalDateTimeSerializer::class) val closeDate: LocalDateTime,
    val ownerId: String,
    val accountId: String? = null,
    val description: String? = null,
    val amount: BigDecimal? = null,
    val probability: BigDecimal? = null,
    val type: String? = null,
    val leadSource: String? = null,
    val campaignId: String? = null,
    val pricebook2Id: String? = null,
    val isDeleted: Boolean? = null,
    val isPrivate: Boolean? = null,
    val isClosed: Boolean? = null,
    val isWon: Boolean? = null,
    val forecastCategoryName: String? = null,
) : WriteRequest {
    override fun validate() =
        requireNotBlankFields("name" to name, "stageName" to stageName, "ownerId" to ownerId)
}
