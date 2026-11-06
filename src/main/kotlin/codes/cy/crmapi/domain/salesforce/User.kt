@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce User entity.
 */
@Serializable
data class User(
    val id: String = "",
    val username: String = "",
    val lastName: String = "",
    val firstName: String? = null,
    val name: String? = null,
    val email: String = "",
    val isActive: Boolean = false,
    val userRoleId: String? = null,
    val profileId: String? = null,
    val title: String? = null,
    val department: String? = null,
    val phone: String? = null,
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN
)
