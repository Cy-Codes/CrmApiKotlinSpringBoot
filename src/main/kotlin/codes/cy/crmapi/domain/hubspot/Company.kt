@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.hubspot

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for HubSpot Company entity.
 */
@Serializable
data class Company(
    val companyId: Long = 0L,
    val name: String = "",
    val domain: String? = null,
    val industry: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null,
    val state: String? = null,
    val zip: String? = null,
    val country: String? = null,
    val annualRevenue: BigDecimal? = null,
    val numberOfEmployees: Int? = null,
    val description: String? = null,
    val ownerId: Long? = null,
    val lifecyclestage: String = "",
    val createdAt: LocalDateTime = LocalDateTime.MIN,
    val updatedAt: LocalDateTime = LocalDateTime.MIN,
    val archived: Boolean = false
)
