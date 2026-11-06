@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.salesforce

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Salesforce Account entity.
 */
@Serializable
data class Account(
    val id: String = "",
    val isDeleted: Boolean = false,
    val parentId: String? = null,
    val name: String = "",
    val type: String? = null,
    val billingStreet: String? = null,
    val billingCity: String? = null,
    val billingState: String? = null,
    val billingPostalCode: String? = null,
    val billingCountry: String? = null,
    val shippingStreet: String? = null,
    val shippingCity: String? = null,
    val shippingState: String? = null,
    val shippingPostalCode: String? = null,
    val shippingCountry: String? = null,
    val phone: String? = null,
    val fax: String? = null,
    val website: String? = null,
    val industry: String? = null,
    val annualRevenue: BigDecimal? = null,
    val numberOfEmployees: Int? = null,
    val description: String? = null,
    val ownerId: String = "",
    val createdDate: LocalDateTime = LocalDateTime.MIN,
    val createdById: String = "",
    val lastModifiedDate: LocalDateTime = LocalDateTime.MIN,
    val lastModifiedById: String = ""
)
