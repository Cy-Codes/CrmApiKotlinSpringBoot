@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce Case entity.
 */
@Serializable
data class Case(
    val id: String = "",
    val isDeleted: Boolean = false,
    val caseNumber: String = "",
    val contactId: String? = null,
    val accountId: String? = null,
    val parentId: String? = null,
    val suppliedName: String? = null,
    val suppliedEmail: String? = null,
    val suppliedPhone: String? = null,
    val suppliedCompany: String? = null,
    val type: String? = null,
    val status: String = "",
    val reason: String? = null,
    val origin: String? = null,
    val subject: String? = null,
    val priority: String = "",
    val description: String? = null,
    val isClosed: Boolean = false,
    val closedDate: LocalDateTime? = null,
    val isEscalated: Boolean = false,
    val ownerId: String = "",
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
