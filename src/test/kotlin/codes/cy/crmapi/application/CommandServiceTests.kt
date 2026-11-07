package codes.cy.crmapi.application

import codes.cy.crmapi.application.interfaces.IIdGenerator
import codes.cy.crmapi.application.interfaces.SalesforceIdPrefix
import codes.cy.crmapi.application.services.HubSpotCommandService
import codes.cy.crmapi.application.services.SalesforceCommandService
import codes.cy.crmapi.application.services.getUtcNow
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.hubspot.Company
import codes.cy.crmapi.domain.hubspot.CompanyWriteRequest
import codes.cy.crmapi.domain.salesforce.Account
import codes.cy.crmapi.domain.salesforce.AccountWriteRequest
import codes.cy.crmapi.infrastructure.repositories.IHubSpotRepository
import codes.cy.crmapi.infrastructure.repositories.ISalesforceRepository
import java.lang.reflect.Proxy
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CommandServiceTests {
    private val clock: Clock = Clock.fixed(Instant.parse("2026-01-02T03:04:05Z"), ZoneId.of("America/New_York"))

    private class FixedIds : IIdGenerator {
        override fun executeNextSalesforceId(prefix: SalesforceIdPrefix): String = "${prefix.keyPrefix}FIXEDIDFIXEDID1"
        override fun executeNextHubSpotId(): Long = HUBSPOT_ID
        override fun executeNextUuid(): UUID = UUID(0L, 1L)
    }

    private inline fun <reified T : Any> recordingProxy(calls: MutableList<List<Any?>>, crossinline result: (String) -> Any?): T =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args ->
            calls += listOf(method.name) + (args?.toList() ?: emptyList())
            result(method.name)
        } as T

    @Test
    fun `clock getUtcNow always yields a UTC offset`() {
        assertEquals(OffsetDateTime.of(2026, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC), clock.getUtcNow())
    }

    @Test
    fun `salesforce create passes generated prefixed id and UTC now to the repository`() {
        val calls = mutableListOf<List<Any?>>()
        val repository = recordingProxy<ISalesforceRepository>(calls) { Account(id = "x") }
        val request = AccountWriteRequest(name = "Acme", ownerId = "005OWNER")

        SalesforceCommandService(repository, FixedIds(), clock).executeCreateAccount(DatabaseProvider.Pg, request)

        assertEquals(
            listOf("executeCreateAccount", DatabaseProvider.Pg, "001FIXEDIDFIXEDID1", request, clock.getUtcNow()),
            calls.single(),
        )
    }

    @Test
    fun `hubspot create uses generated long id and update and delete pass the path id through`() {
        val calls = mutableListOf<List<Any?>>()
        val repository = recordingProxy<IHubSpotRepository>(calls) { name -> if (name.startsWith("executeDelete")) true else Company() }
        val service = HubSpotCommandService(repository, FixedIds(), clock)
        val request = CompanyWriteRequest(name = "Acme")

        service.executeCreateCompany(DatabaseProvider.Ms, request)
        service.executeUpdateCompany(DatabaseProvider.Ms, UPDATED_ID, request)
        service.executeDeleteCompany(DatabaseProvider.Ms, UPDATED_ID)

        assertEquals(listOf("executeCreateCompany", DatabaseProvider.Ms, HUBSPOT_ID, request, clock.getUtcNow()), calls[0])
        assertEquals(listOf("executeUpdateCompany", DatabaseProvider.Ms, UPDATED_ID, request, clock.getUtcNow()), calls[1])
        assertEquals(listOf("executeDeleteCompany", DatabaseProvider.Ms, UPDATED_ID), calls[2])
    }

    companion object {
        private const val HUBSPOT_ID = 1_700_000_000_000_123L
        private const val UPDATED_ID = 77L
    }
}
