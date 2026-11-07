@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class)

package codes.cy.crmapi.domain.dynamics365

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LenientLocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import codes.cy.crmapi.domain.common.WriteRequest
import codes.cy.crmapi.domain.common.requireNotBlankFields
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.json.JsonNames

/**
 * Writable columns of a Dynamics 365 Account (create / full replace).
 * Server-owned: `accountid`, `createdon/by`, `modifiedon/by`. `statecode` / `statuscode` are optional (database default applies).
 * `owningBusinessUnit` also accepts the response-side spelling `owningbusinessunit`.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class AccountWriteRequest(
    val name: String,
    val ownerId: UUID,
    @JsonNames("owningbusinessunit") val owningBusinessUnit: UUID,
    val accountNumber: String? = null,
    val parentAccountId: UUID? = null,
    val telephone1: String? = null,
    val emailAddress1: String? = null,
    val websiteurl: String? = null,
    val address1_Line1: String? = null,
    val address1_City: String? = null,
    val address1_StateOrProvince: String? = null,
    val address1_PostalCode: String? = null,
    val address1_Country: String? = null,
    val revenue: BigDecimal? = null,
    val numberOfEmployees: Int? = null,
    val industrycode: Int? = null,
    val description: String? = null,
    val stateCode: Int? = null,
    val statusCode: Int? = null,
) : WriteRequest {
    override fun validate() = requireNotBlankFields("name" to name)
}

/**
 * Writable columns of a Dynamics 365 Contact (create / full replace). `fullname` is server-computed as `"$firstName $lastName"`.
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ContactWriteRequest(
    val lastName: String,
    val ownerId: UUID,
    @JsonNames("owningbusinessunit") val owningBusinessUnit: UUID,
    val parentCustomerId: UUID? = null,
    val firstName: String? = null,
    val jobTitle: String? = null,
    val emailAddress1: String? = null,
    val telephone1: String? = null,
    val mobilephone: String? = null,
    val address1_Line1: String? = null,
    val address1_City: String? = null,
    val address1_StateOrProvince: String? = null,
    val address1_PostalCode: String? = null,
    val address1_Country: String? = null,
    val leadsourcecode: Int? = null,
    val description: String? = null,
    val stateCode: Int? = null,
    val statusCode: Int? = null,
) : WriteRequest {
    override fun validate() = requireNotBlankFields("lastName" to lastName)
}

/**
 * Writable columns of a Dynamics 365 Opportunity (create / full replace).
 */
@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class OpportunityWriteRequest(
    val name: String,
    val ownerId: UUID,
    @JsonNames("owningbusinessunit") val owningBusinessUnit: UUID,
    val parentAccountId: UUID? = null,
    val parentcontactid: UUID? = null,
    val priceLevelId: UUID? = null,
    val originatingLeadId: UUID? = null,
    val estimatedValue: BigDecimal? = null,
    val actualValue: BigDecimal? = null,
    @Serializable(with = LenientLocalDateTimeSerializer::class) val estimatedCloseDate: LocalDateTime? = null,
    @Serializable(with = LenientLocalDateTimeSerializer::class) val actualCloseDate: LocalDateTime? = null,
    val closeProbability: Int? = null,
    val stepname: String? = null,
    val salesstage: Int? = null,
    val description: String? = null,
    val stateCode: Int? = null,
    val statusCode: Int? = null,
) : WriteRequest {
    override fun validate() = requireNotBlankFields("name" to name)
}
