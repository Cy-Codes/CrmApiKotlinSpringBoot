package codes.cy.crmapi.infrastructure

import codes.cy.crmapi.domain.dynamics365.ContactWriteRequest as DynamicsContactWriteRequest
import codes.cy.crmapi.domain.dynamics365.OpportunityWriteRequest as DynamicsOpportunityWriteRequest
import codes.cy.crmapi.domain.hubspot.CompanyWriteRequest
import codes.cy.crmapi.domain.hubspot.ContactWriteRequest as HubSpotContactWriteRequest
import codes.cy.crmapi.domain.hubspot.DealWriteRequest
import codes.cy.crmapi.domain.salesforce.AccountWriteRequest as SalesforceAccountWriteRequest
import codes.cy.crmapi.domain.salesforce.ContactWriteRequest as SalesforceContactWriteRequest
import codes.cy.crmapi.domain.salesforce.OpportunityWriteRequest as SalesforceOpportunityWriteRequest
import codes.cy.crmapi.infrastructure.repositories.getDynamicsContactFullName
import codes.cy.crmapi.infrastructure.repositories.isConstraintViolation
import codes.cy.crmapi.infrastructure.repositories.toInsertColumns
import codes.cy.crmapi.infrastructure.repositories.toUpdateColumns
import java.math.BigDecimal
import java.sql.SQLException
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class WriteColumnMappingsTests {
    private val now: OffsetDateTime = OffsetDateTime.of(2026, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC)
    private val ownerUuid: UUID = UUID.fromString("22222222-2222-2222-2222-222222222222")
    private val unitUuid: UUID = UUID.fromString("33333333-3333-3333-3333-333333333333")
    private val newUuid: UUID = UUID.fromString("44444444-4444-4444-4444-444444444444")

    @Test
    fun `salesforce account insert sets id, audit columns from owner and omits null defaulted columns`() {
        val columns = SalesforceAccountWriteRequest(name = "Acme", ownerId = "005OWNER").toInsertColumns("001NEW", now)

        assertEquals("001NEW", columns["Id"])
        assertEquals("Acme", columns["Name"])
        assertEquals("005OWNER", columns["OwnerId"])
        assertEquals("005OWNER", columns["CreatedById"])
        assertEquals("005OWNER", columns["LastModifiedById"])
        assertEquals(now, columns["CreatedDate"])
        assertEquals(now, columns["LastModifiedDate"])
        assertFalse(columns.containsKey("IsDeleted"))
        assertTrue(columns.containsKey("Phone") && columns["Phone"] == null)
    }

    @Test
    fun `salesforce account update only touches last modified audit columns`() {
        val columns = SalesforceAccountWriteRequest(name = "Acme", ownerId = "005OWNER", isDeleted = true).toUpdateColumns(now)

        assertEquals(true, columns["IsDeleted"])
        assertEquals(now, columns["LastModifiedDate"])
        assertEquals("005OWNER", columns["LastModifiedById"])
        listOf("Id", "CreatedDate", "CreatedById").forEach { assertFalse(columns.containsKey(it), it) }
    }

    @Test
    fun `salesforce contact never writes generated Name column`() {
        val columns = SalesforceContactWriteRequest(lastName = "Doe", ownerId = "005OWNER").toInsertColumns("003NEW", now)

        assertFalse(columns.containsKey("Name"))
        assertEquals("Doe", columns["LastName"])
    }

    @Test
    fun `salesforce opportunity never writes ExpectedRevenue and converts close date to a date`() {
        val request = SalesforceOpportunityWriteRequest(
            name = "Deal",
            stageName = "Prospecting",
            closeDate = LocalDateTime.of(2026, 12, 31, 10, 30),
            ownerId = "005OWNER",
            amount = BigDecimal("10.50"),
        )

        val columns = request.toInsertColumns("006NEW", now)

        assertFalse(columns.containsKey("ExpectedRevenue"))
        assertEquals(LocalDate.of(2026, 12, 31), columns["CloseDate"])
        assertEquals(BigDecimal("10.50"), columns["Amount"])
        listOf("IsClosed", "IsWon", "IsPrivate", "ForecastCategoryName").forEach { assertFalse(columns.containsKey(it), it) }
    }

    @Test
    fun `salesforce opportunity writes optional flags when supplied`() {
        val columns = SalesforceOpportunityWriteRequest(
            name = "Deal", stageName = "Closed Won", closeDate = LocalDateTime.of(2026, 1, 1, 0, 0), ownerId = "005OWNER",
            isClosed = true, isWon = true, forecastCategoryName = "Closed",
        ).toUpdateColumns(now)

        assertEquals(true, columns["IsClosed"])
        assertEquals(true, columns["IsWon"])
        assertEquals("Closed", columns["ForecastCategoryName"])
    }

    @Test
    fun `hubspot insert uses entity id column, equal created and updated timestamps, and omits defaulted nulls`() {
        val company = CompanyWriteRequest(name = "Acme").toInsertColumns(42L, now)
        val contact = HubSpotContactWriteRequest(email = "a@b.c").toInsertColumns(43L, now)
        val deal = DealWriteRequest(dealName = "D", pipelineId = "p", dealStageId = "s").toInsertColumns(44L, now)

        assertEquals(42L, company["companyId"])
        assertEquals(43L, contact["contactId"])
        assertEquals(44L, deal["dealId"])
        listOf(company, contact, deal).forEach {
            assertEquals(now, it["createdAt"])
            assertEquals(now, it["updatedAt"])
            assertFalse(it.containsKey("archived"))
            assertFalse(it.containsKey("lifecyclestage"))
        }
    }

    @Test
    fun `hubspot update only changes updatedAt and deal close date is bound as UTC offset timestamp`() {
        val columns = DealWriteRequest(
            dealName = "D", pipelineId = "p", dealStageId = "s", closedate = LocalDateTime.of(2026, 5, 6, 7, 8),
        ).toUpdateColumns(now)

        assertEquals(now, columns["updatedAt"])
        assertFalse(columns.containsKey("createdAt"))
        assertFalse(columns.containsKey("dealId"))
        assertEquals(OffsetDateTime.of(2026, 5, 6, 7, 8, 0, 0, ZoneOffset.UTC), columns["closedate"])
    }

    @Test
    fun `dynamics contact computes fullname and uses owner for created and modified by`() {
        val request = DynamicsContactWriteRequest(
            lastName = "Doe", ownerId = ownerUuid, owningBusinessUnit = unitUuid, firstName = "Jane",
        )

        val insert = request.toInsertColumns(newUuid, now)
        val update = request.toUpdateColumns(now)

        assertEquals("Jane Doe", insert["fullname"])
        assertEquals(newUuid, insert["contactid"])
        assertEquals(ownerUuid, insert["createdby"])
        assertEquals(ownerUuid, insert["modifiedby"])
        assertEquals(now, insert["createdon"])
        assertEquals(now, insert["modifiedon"])
        assertEquals(unitUuid, insert["owningbusinessunit"])
        assertFalse(insert.containsKey("statecode"))
        assertEquals(ownerUuid, update["modifiedby"])
        assertEquals(now, update["modifiedon"])
        listOf("createdon", "createdby", "contactid").forEach { assertFalse(update.containsKey(it), it) }
    }

    @ParameterizedTest
    @CsvSource(
        "Jane, Doe, Jane Doe",
        "' ', Doe, Doe",
    )
    fun `dynamics contact full name is trimmed`(first: String, last: String, expected: String) {
        assertEquals(expected, getDynamicsContactFullName(first.trim().ifEmpty { null }, last))
    }

    @Test
    fun `dynamics opportunity converts close dates to dates and writes state codes when supplied`() {
        val columns = DynamicsOpportunityWriteRequest(
            name = "O", ownerId = ownerUuid, owningBusinessUnit = unitUuid,
            estimatedCloseDate = LocalDateTime.of(2026, 3, 4, 5, 6), stateCode = 1,
        ).toInsertColumns(newUuid, now)

        assertEquals(LocalDate.of(2026, 3, 4), columns["estimatedclosedate"])
        assertEquals(null, columns["actualclosedate"])
        assertEquals(1, columns["statecode"])
        assertFalse(columns.containsKey("statuscode"))
        assertEquals(newUuid, columns["opportunityid"])
    }

    @Test
    fun `constraint violations are recognised on postgres and sql server`() {
        assertTrue(SQLException("fk", "23503").isConstraintViolation())
        assertTrue(SQLException("unique", "23505").isConstraintViolation())
        assertTrue(SQLException("fk", "23000", 547).isConstraintViolation())
        assertTrue(SQLException("pk", "23000", 2627).isConstraintViolation())
        assertTrue(SQLException("idx", "23000", 2601).isConstraintViolation())
        assertTrue(RuntimeException("wrapped", SQLException("fk", "23503")).isConstraintViolation())
        assertFalse(SQLException("timeout", "08006").isConstraintViolation())
        assertFalse(SQLException("other", "22001", 8152).isConstraintViolation())
    }
}
