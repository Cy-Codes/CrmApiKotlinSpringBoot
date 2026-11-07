package codes.cy.crmapi.infrastructure.ids

import codes.cy.crmapi.application.interfaces.IIdGenerator
import codes.cy.crmapi.application.interfaces.SalesforceIdPrefix
import java.time.Clock
import java.util.UUID
import java.util.random.RandomGenerator
import org.springframework.stereotype.Component

/**
 * Production [IIdGenerator] driven by an injected [Clock] and [RandomGenerator] (both replaceable in tests).
 */
@Component
class RandomIdGenerator(
    private val clock: Clock,
    private val random: RandomGenerator,
) : IIdGenerator {
    override fun executeNextSalesforceId(prefix: SalesforceIdPrefix): String =
        prefix.keyPrefix + List(SALESFORCE_RANDOM_SUFFIX_LENGTH) { SALESFORCE_ID_ALPHABET[random.nextInt(SALESFORCE_ID_ALPHABET.length)] }
            .joinToString("")

    override fun executeNextHubSpotId(): Long =
        clock.millis() * HUBSPOT_MILLIS_MULTIPLIER + random.nextInt(HUBSPOT_MILLIS_MULTIPLIER.toInt())

    override fun executeNextUuid(): UUID = UUID.randomUUID()

    companion object {
        const val SALESFORCE_RANDOM_SUFFIX_LENGTH: Int = 15
        const val SALESFORCE_ID_ALPHABET: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        const val HUBSPOT_MILLIS_MULTIPLIER: Long = 1000L
    }
}
