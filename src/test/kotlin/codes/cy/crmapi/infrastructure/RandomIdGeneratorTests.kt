package codes.cy.crmapi.infrastructure

import codes.cy.crmapi.application.interfaces.SalesforceIdPrefix
import codes.cy.crmapi.infrastructure.ids.RandomIdGenerator
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.random.RandomGenerator
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource

class RandomIdGeneratorTests {
    private val fixedClock: Clock = Clock.fixed(Instant.ofEpochMilli(FIXED_EPOCH_MILLIS), ZoneOffset.UTC)

    @ParameterizedTest
    @EnumSource(SalesforceIdPrefix::class)
    fun `salesforce id is 18 chars with key prefix and 15 uppercase alphanumerics`(prefix: SalesforceIdPrefix) {
        val id = RandomIdGenerator(fixedClock, java.security.SecureRandom()).executeNextSalesforceId(prefix)

        assertEquals(ID_LENGTH, id.length)
        assertTrue(id.startsWith(prefix.keyPrefix))
        assertTrue(Regex("^[A-Z0-9]{18}$").matches(id), id)
    }

    @Test
    fun `salesforce key prefixes follow the contract`() {
        assertEquals("001", SalesforceIdPrefix.Account.keyPrefix)
        assertEquals("003", SalesforceIdPrefix.Contact.keyPrefix)
        assertEquals("006", SalesforceIdPrefix.Opportunity.keyPrefix)
    }

    @Test
    fun `salesforce suffix is derived from the injected random generator`() {
        val alwaysFirst = ConstantRandom(0)

        val id = RandomIdGenerator(fixedClock, alwaysFirst).executeNextSalesforceId(SalesforceIdPrefix.Account)

        assertEquals("001AAAAAAAAAAAAAAA", id)
    }

    @Test
    fun `hubspot id is epoch millis times 1000 plus random 0 to 999`() {
        assertEquals(FIXED_EPOCH_MILLIS * 1000 + 0, RandomIdGenerator(fixedClock, ConstantRandom(0)).executeNextHubSpotId())
        assertEquals(FIXED_EPOCH_MILLIS * 1000 + 999, RandomIdGenerator(fixedClock, ConstantRandom(999)).executeNextHubSpotId())
        assertTrue(RandomIdGenerator(fixedClock, ConstantRandom(5)).executeNextHubSpotId() > 0)
    }

    @Test
    fun `uuid ids are unique`() {
        val generator = RandomIdGenerator(fixedClock, java.security.SecureRandom())

        assertNotEquals(generator.executeNextUuid(), generator.executeNextUuid())
    }

    /** Deterministic [RandomGenerator] whose `nextInt(bound)` returns `min(value, bound - 1)`. */
    private class ConstantRandom(private val value: Int) : RandomGenerator {
        override fun nextLong(): Long = value.toLong()
        override fun nextInt(bound: Int): Int = minOf(value, bound - 1)
    }

    companion object {
        private const val FIXED_EPOCH_MILLIS = 1_700_000_000_000L
        private const val ID_LENGTH = 18
    }
}
