package codes.cy.crmapi.application.interfaces

import java.util.UUID

/**
 * Three-character Salesforce key prefixes identifying the sObject type of an 18-character record id.
 */
enum class SalesforceIdPrefix(val keyPrefix: String) {
    Account("001"),
    Contact("003"),
    Opportunity("006"),
}

/**
 * Side-effect boundary generating server-owned primary keys. Injectable so tests can supply deterministic ids.
 */
interface IIdGenerator {
    /**
     * Generates an 18-character Salesforce id: [SalesforceIdPrefix.keyPrefix] + 15 random uppercase alphanumeric characters.
     */
    fun executeNextSalesforceId(prefix: SalesforceIdPrefix): String

    /**
     * Generates a positive HubSpot id: `epochMillis * 1000 + random(0..999)`.
     */
    fun executeNextHubSpotId(): Long

    /**
     * Generates a random Dynamics 365 [UUID].
     */
    fun executeNextUuid(): UUID
}
