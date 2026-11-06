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
 * Domain model for Salesforce Lead entity.
 */
@Serializable
data class Lead(
    val id: String = "",
    val isDeleted: Boolean = false,
    val lastName: String = "",
    val firstName: String? = null,
    val salutation: String? = null,
    val name: String? = null,
    val title: String? = null,
    val company: String = "",
    val street: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val country: String? = null,
    val phone: String? = null,
    val mobilePhone: String? = null,
    val email: String? = null,
    val website: String? = null,
    val leadSource: String? = null,
    val status: String = "",
    val industry: String? = null,
    val annualRevenue: BigDecimal? = null,
    val numberOfEmployees: Int? = null,
    val description: String? = null,
    val isConverted: Boolean = false,
    val convertedDate: LocalDateTime? = null,
    val convertedAccountId: String? = null,
    val convertedContactId: String? = null,
    val convertedOpportunityId: String? = null,
    val isUnreadByOwner: Boolean = false,
    val ownerId: String = "",
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
