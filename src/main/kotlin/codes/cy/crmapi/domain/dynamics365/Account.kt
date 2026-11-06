@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)

package codes.cy.crmapi.domain.dynamics365

import codes.cy.crmapi.domain.common.BigDecimalSerializer
import codes.cy.crmapi.domain.common.LocalDateTimeSerializer
import codes.cy.crmapi.domain.common.UuidSerializer
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

/**
 * Domain model for Dynamics365 Account entity.
 */
@Serializable
data class Account(
    val accountId: UUID = UUID(0L, 0L),
    val name: String = "",
    val accountNumber: String? = null,
    val parentAccountId: UUID? = null,
    val telephone1: String? = null,
    val emailAddress1: String? = null,
    val websiteurl: String? = null,
    val address1_Line1: String? = null,
    val address1_City: String? = null,
    val address1_StateOrProvince: String? = null,
    val address1_PostalCode: String? = null,
    val address1_Country: String? = null,
    val revenue: BigDecimal? = null,
    val numberOfEmployees: Int? = null,
    val industrycode: Int? = null,
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
