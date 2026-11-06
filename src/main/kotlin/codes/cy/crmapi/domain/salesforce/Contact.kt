@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce Contact entity.
 */
@Serializable
data class Contact(
    val id: String = "",
    val isDeleted: Boolean = false,
    val accountId: String? = null,
    val lastName: String = "",
    val firstName: String? = null,
    val salutation: String? = null,
    val name: String? = null,
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
    val ownerId: String = "",
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
