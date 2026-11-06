@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.dynamics365

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Dynamics365 Contact entity.
 */
@Serializable
data class Contact(
    val contactId: UUID = UUID(0L, 0L),
    val parentCustomerId: UUID? = null,
    val firstName: String? = null,
    val lastName: String = "",
    val fullName: String = "",
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
    val ownerId: UUID = UUID(0L, 0L),
    val owningbusinessunit: UUID = UUID(0L, 0L),
    val stateCode: Int = 0,
    val statusCode: Int = 0,
    val createdOn: LocalDateTime = LocalDateTime.MIN,
    val createdBy: UUID = UUID(0L, 0L),
    val modifiedOn: LocalDateTime = LocalDateTime.MIN,
    val modifiedBy: UUID = UUID(0L, 0L),
)
