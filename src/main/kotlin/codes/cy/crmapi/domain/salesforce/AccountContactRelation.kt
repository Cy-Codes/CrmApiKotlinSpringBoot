@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce AccountContactRelation entity.
 */
@Serializable
data class AccountContactRelation(
    val id: String = "",
    val accountId: String = "",
    val contactId: String = "",
    val roles: String? = null,
    val isDirect: Boolean = false,
    val isActive: Boolean = false,
    val startDate: LocalDateTime? = null,
    val endDate: LocalDateTime? = null,
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN
)
