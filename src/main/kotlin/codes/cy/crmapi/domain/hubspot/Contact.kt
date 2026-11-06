@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.hubspot

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for HubSpot Contact entity.
 */
@Serializable
data class Contact(
    val contactId: Long = 0L,
    val email: String = "",
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val mobilePhone: String? = null,
    val jobTitle: String? = null,
    val associatedCompanyId: Long? = null,
    val ownerId: Long? = null,
    val lifecyclestage: String = "",
    val leadStatus: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val country: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val archived: Boolean = false
)
