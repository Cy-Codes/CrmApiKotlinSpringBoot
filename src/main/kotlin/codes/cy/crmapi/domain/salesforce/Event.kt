@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce Event entity.
 */
@Serializable
data class Event(
    val id: String = "",
    val whoId: String? = null,
    val whatId: String? = null,
    val whoCount: Int = 0,
    val whatCount: Int = 0,
    val subject: String = "",
    val location: String? = null,
    val isAllDayEvent: Boolean = false,
    val activityDateTime: LocalDateTime? = null,
    val activityDate: LocalDateTime? = null,
    val durationInMinutes: Int? = null,
    val startDateTime: LocalDateTime = LocalDateTime.MIN,
    val endDateTime: LocalDateTime = LocalDateTime.MIN,
    val description: String? = null,
    val ownerId: String = "",
    val isDeleted: Boolean = false,
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
