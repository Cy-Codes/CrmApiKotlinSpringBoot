@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce Product2 entity.
 */
@Serializable
data class Product2(
    val id: String = "",
    val name: String = "",
    val productCode: String? = null,
    val description: String? = null,
    val isActive: Boolean = false,
    val family: String? = null,
    val isDeleted: Boolean = false,
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
