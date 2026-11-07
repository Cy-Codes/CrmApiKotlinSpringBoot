@file:UseSerializers(BigDecimalSerializer::class)

package codes.cy.crmapi.domain.hubspot

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LenientLocalDateTimeSerializer
import codes.cy.crmapi.domain.common.WriteRequest
import codes.cy.crmapi.domain.common.requireNotBlankFields
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Writable columns of a HubSpot Company (create / full replace).
 * Server-owned: `companyId`, `createdAt`, `updatedAt`. `lifecyclestage` / `archived` are optional (database default applies).
 */
@Serializable
data class CompanyWriteRequest(
    val name: String,
    val domain: String? = null,
    val industry: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val country: String? = null,
    val annualRevenue: BigDecimal? = null,
    val numberOfEmployees: Int? = null,
    val description: String? = null,
    val ownerId: Long? = null,
    val lifecyclestage: String? = null,
    val archived: Boolean? = null,
) : WriteRequest {
    override fun validate() = requireNotBlankFields("name" to name)
}

/**
 * Writable columns of a HubSpot Contact (create / full replace). `email` is unique.
 */
@Serializable
data class ContactWriteRequest(
    val email: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val mobilePhone: String? = null,
    val jobTitle: String? = null,
    val associatedCompanyId: Long? = null,
    val ownerId: Long? = null,
    val leadStatus: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val country: String? = null,
    val lifecyclestage: String? = null,
    val archived: Boolean? = null,
) : WriteRequest {
    override fun validate() = requireNotBlankFields("email" to email)
}

/**
 * Writable columns of a HubSpot Deal (create / full replace).
 */
@Serializable
data class DealWriteRequest(
    val dealName: String,
    val pipelineId: String,
    val dealStageId: String,
    val amount: BigDecimal? = null,
    @Serializable(with = LenientLocalDateTimeSerializer::class) val closedate: LocalDateTime? = null,
    val dealType: String? = null,
    val ownerId: Long? = null,
    val archived: Boolean? = null,
) : WriteRequest {
    override fun validate() =
        requireNotBlankFields("dealName" to dealName, "pipelineId" to pipelineId, "dealStageId" to dealStageId)
}
