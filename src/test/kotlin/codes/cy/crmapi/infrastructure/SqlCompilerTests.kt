package codes.cy.crmapi.infrastructure

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.common.toDatabaseProvider
import codes.cy.crmapi.domain.common.toProviderCode
import codes.cy.crmapi.infrastructure.compilers.ISqlCompilerFactory
import codes.cy.crmapi.infrastructure.compilers.SqlCompilerFactory
import codes.cy.crmapi.infrastructure.compilers.SqlQuery
import codes.cy.crmapi.infrastructure.repositories.applyFilters
import codes.cy.crmapi.infrastructure.repositories.applyPagination
import codes.cy.crmapi.infrastructure.repositories.applySorting
import codes.cy.crmapi.infrastructure.repositories.parseFilterValue
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class SqlCompilerTests {
    private val factory: ISqlCompilerFactory = SqlCompilerFactory()

    @Test
    fun `sqlServerCompiler generates bracketed identifiers and offset fetch`() {
        val compiler = factory.getCompiler(DatabaseProvider.Ms)
        val query = SqlQuery("Salesforce.Account")
            .where("Industry", "Technology")
            .orderBy("Name")
            .skip(20)
            .take(10)

        val result = compiler.compile(query)

        assertTrue(result.sql.contains("[Salesforce].[Account]"))
        assertTrue(result.sql.contains("[Industry] ="))
        assertTrue(result.sql.contains("ORDER BY [Name]"))
        assertTrue(result.sql.contains("OFFSET"))
        assertTrue(result.sql.contains("FETCH NEXT"))
    }

    @Test
    fun `postgresCompiler generates double quoted identifiers and limit offset`() {
        val compiler = factory.getCompiler(DatabaseProvider.Pg)
        val query = SqlQuery("Salesforce.Account")
            .where("Industry", "Technology")
            .orderBy("Name")
            .skip(20)
            .take(10)

        val result = compiler.compile(query)

        assertTrue(result.sql.contains("\"Salesforce\".\"Account\""))
        assertTrue(result.sql.contains("\"Industry\" ="))
        assertTrue(result.sql.contains("ORDER BY \"Name\""))
        assertTrue(result.sql.contains("LIMIT"))
        assertTrue(result.sql.contains("OFFSET"))
    }

    @Test
    fun `countQuery compiles accurately across providers`() {
        val msCompiler = factory.getCompiler(DatabaseProvider.Ms)
        val pgCompiler = factory.getCompiler(DatabaseProvider.Pg)

        val countQuery = SqlQuery("HubSpot.Company")
            .where("domain", "acme.com")
            .asCount()

        val msResult = msCompiler.compile(countQuery)
        val pgResult = pgCompiler.compile(countQuery)

        assertTrue(msResult.sql.uppercase().contains("SELECT COUNT(*)"))
        assertTrue(msResult.sql.contains("[HubSpot].[Company]"))

        assertTrue(pgResult.sql.uppercase().contains("SELECT COUNT(*)"))
        assertTrue(pgResult.sql.contains("\"HubSpot\".\"Company\""))
    }

    @Test
    fun `queryFilteringExtensions applyFilters ignores unsafe identifiers and parses invariant types`() {
        val filters = mapOf(
            "Industry" to "Technology",
            "AnnualRevenue" to "125000.50",
            "IsDeleted" to "false",
            "Name; DROP TABLE Account;--" to "Malicious",
        )

        val compiler = factory.getCompiler(DatabaseProvider.Ms)
        val query = SqlQuery("Salesforce.Account")
            .applyFilters(filters)
            .applySorting("Name", QueryOptions.DESCENDING_SORT_DIRECTION)
            .applyPagination(2, 10)

        val compiled = compiler.compile(query)

        assertTrue(compiled.sql.contains("[Industry] ="))
        assertTrue(compiled.sql.contains("[AnnualRevenue] ="))
        assertTrue(compiled.sql.contains("[IsDeleted] ="))
        assertFalse(compiled.sql.contains("DROP TABLE"))
        assertTrue(compiled.sql.contains("ORDER BY [Name] DESC"))
        assertTrue(compiled.bindings.contains(BigDecimal("125000.50")))
        assertEquals(false, "false".parseFilterValue())
    }

    @ParameterizedTest
    @CsvSource(
        "ms, Ms",
        "MS, Ms",
        "pg, Pg",
        "PG, Pg",
    )
    fun `databaseProviderExtensions toDatabaseProvider parses valid providers`(
        rawProvider: String,
        expected: DatabaseProvider,
    ) {
        assertEquals(expected, rawProvider.toDatabaseProvider())
        assertEquals(rawProvider.lowercase(), expected.toProviderCode())
    }
}
