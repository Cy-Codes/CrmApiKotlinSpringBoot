@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce Task entity.
 */
@Serializable
data class Task(
    val id: String = "",
    val whoId: String? = null,
    val whatId: String? = null,
    val whoCount: Int = 0,
    val whatCount: Int = 0,
    val subject: String = "",
    val activityDate: LocalDateTime? = null,
    val status: String = "",
    val priority: String = "",
    val isHighPriority: Boolean = false,
    val ownerId: String = "",
    val description: String? = null,
    val isDeleted: Boolean = false,
    val isClosed: Boolean = false,
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
