package codes.cy.crmapi.infrastructure

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.common.toDatabaseProvider
import codes.cy.crmapi.domain.common.toProviderCode
import codes.cy.crmapi.infrastructure.compilers.ISqlCompilerFactory
import codes.cy.crmapi.infrastructure.compilers.SqlCommand
import codes.cy.crmapi.infrastructure.compilers.SqlCompilerFactory
import codes.cy.crmapi.infrastructure.compilers.SqlQuery
import codes.cy.crmapi.infrastructure.compilers.WhereClause
import codes.cy.crmapi.infrastructure.repositories.applyFilters
import codes.cy.crmapi.infrastructure.repositories.applyPagination
import codes.cy.crmapi.infrastructure.repositories.applySorting
import codes.cy.crmapi.infrastructure.repositories.parseFilterValue
import java.math.BigDecimal
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource

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

    @Test
    fun `queryFilteringExtensions applyPagination protects against integer overflow`() {
        val compiler = factory.getCompiler(DatabaseProvider.Ms)
        val query = SqlQuery("Salesforce.Account")
            .orderBy("Name")
            .applyPagination(Int.MAX_VALUE, 100)

        val compiled = compiler.compile(query)

        // MAX_PAGE is 100,000 so offset is (100,000 - 1) * 100 = 9,999,900
        assertTrue(compiled.sql.contains("OFFSET"))
        assertTrue(compiled.sql.contains("FETCH NEXT"))
        assertTrue(compiled.bindings.contains(9999900))
        assertTrue(compiled.bindings.contains(100))
    }

    @Test
    fun `sqlServerCompiler compiles insert with bracketed identifiers and bound parameters`() {
        val command = SqlCommand.Insert("Salesforce.Account", linkedMapOf("Id" to "001ABC", "Name" to "O'Brien; DROP TABLE x", "Phone" to null))

        val result = factory.getCompiler(DatabaseProvider.Ms).compile(command)

        assertEquals("INSERT INTO [Salesforce].[Account] ([Id], [Name], [Phone]) VALUES (@p0, @p1, @p2)", result.sql)
        assertEquals(listOf("001ABC", "O'Brien; DROP TABLE x", null), result.bindings)
        assertEquals("001ABC", result.namedBindings["@p0"])
    }

    @Test
    fun `postgresCompiler compiles insert with quoted identifiers and bound parameters`() {
        val command = SqlCommand.Insert("HubSpot.Company", linkedMapOf("companyId" to 1L, "name" to "Acme"))

        val result = factory.getCompiler(DatabaseProvider.Pg).compile(command)

        assertEquals("INSERT INTO \"HubSpot\".\"Company\" (\"companyId\", \"name\") VALUES (@p0, @p1)", result.sql)
        assertEquals(listOf<Any?>(1L, "Acme"), result.bindings)
    }

    @ParameterizedTest
    @EnumSource(DatabaseProvider::class)
    fun `compiler compiles update with set list followed by where binding`(provider: DatabaseProvider) {
        val command = SqlCommand.Update(
            "Salesforce.Account",
            linkedMapOf("Name" to "Acme", "Phone" to null),
            listOf(WhereClause.EqualsClause("Id", "001ABC")),
        )
        val expectedSql = when (provider) {
            DatabaseProvider.Ms -> "UPDATE [Salesforce].[Account] SET [Name] = @p0, [Phone] = @p1 WHERE [Id] = @p2"
            DatabaseProvider.Pg -> "UPDATE \"Salesforce\".\"Account\" SET \"Name\" = @p0, \"Phone\" = @p1 WHERE \"Id\" = @p2"
        }

        val result = factory.getCompiler(provider).compile(command)

        assertEquals(expectedSql, result.sql)
        assertEquals(listOf<Any?>("Acme", null, "001ABC"), result.bindings)
    }

    @Test
    fun `compiler compiles delete for both dialects`() {
        val command = SqlCommand.Delete("Dynamics365.Account", listOf(WhereClause.EqualsClause("accountid", "id-1")))

        val ms = factory.getCompiler(DatabaseProvider.Ms).compile(command)
        val pg = factory.getCompiler(DatabaseProvider.Pg).compile(command)

        assertEquals("DELETE FROM [Dynamics365].[Account] WHERE [accountid] = @p0", ms.sql)
        assertEquals("DELETE FROM \"Dynamics365\".\"Account\" WHERE \"accountid\" = @p0", pg.sql)
        assertEquals(listOf<Any?>("id-1"), pg.bindings)
    }

    @ParameterizedTest
    @EnumSource(DatabaseProvider::class)
    fun `compiler rejects unsafe identifiers in commands`(provider: DatabaseProvider) {
        val compiler = factory.getCompiler(provider)

        assertThrows(IllegalArgumentException::class.java) {
            compiler.compile(SqlCommand.Insert("Salesforce.Account", mapOf("Name\"; DROP TABLE x;--" to "v")))
        }
        assertThrows(IllegalArgumentException::class.java) {
            compiler.compile(SqlCommand.Delete("Salesforce.Account; DROP", listOf(WhereClause.EqualsClause("Id", "1"))))
        }
        assertThrows(IllegalArgumentException::class.java) {
            compiler.compile(SqlCommand.Update("Salesforce.Account", mapOf("Name" to "v"), listOf(WhereClause.EqualsClause("Id OR 1=1", "1"))))
        }
    }

    @ParameterizedTest
    @EnumSource(DatabaseProvider::class)
    fun `compiler rejects update and delete without predicate and empty insert`(provider: DatabaseProvider) {
        val compiler = factory.getCompiler(provider)

        assertThrows(IllegalArgumentException::class.java) { compiler.compile(SqlCommand.Delete("Salesforce.Account", emptyList())) }
        assertThrows(IllegalArgumentException::class.java) {
            compiler.compile(SqlCommand.Update("Salesforce.Account", mapOf("Name" to "v"), emptyList()))
        }
        assertThrows(IllegalArgumentException::class.java) { compiler.compile(SqlCommand.Insert("Salesforce.Account", emptyMap())) }
    }
}
