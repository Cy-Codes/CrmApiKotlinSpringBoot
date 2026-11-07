package codes.cy.crmapi.infrastructure

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.infrastructure.connections.DbConnectionFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.mock.env.MockEnvironment

class DbConnectionFactoryTests {

    @Test
    fun `parseMsSqlConfig parses ADO NET style connection string correctly`() {
        val raw = "Server=db.example.com,14330;Database=CustomCrm;User Id=crm_user;Password=secret123;TrustServerCertificate=False;"
        val config = DbConnectionFactory.parseMsSqlConfig(raw)

        assertEquals("crm_user", config.username)
        assertEquals("secret123", config.password)
        assertEquals("CustomCrm", config.databaseName)
        assertTrue(config.jdbcUrl.contains("db.example.com:14330"))
        assertTrue(config.jdbcUrl.contains("databaseName=CustomCrm"))
        assertTrue(config.jdbcUrl.contains("trustServerCertificate=False"))
    }

    @Test
    fun `parseMsSqlConfig parses JDBC style connection string correctly`() {
        val raw = "jdbc:sqlserver://db.example.com:1433;databaseName=CustomCrm;user=custom_sa;password=custom_pass;"
        val config = DbConnectionFactory.parseMsSqlConfig(raw)

        assertEquals("custom_sa", config.username)
        assertEquals("custom_pass", config.password)
        assertEquals("CustomCrm", config.databaseName)
        assertEquals(raw, config.jdbcUrl)
    }

    @Test
    fun `parsePgSqlConfig parses ADO NET style connection string correctly`() {
        val raw = "Host=pg.example.com;Port=54320;Database=CrmPg;Username=pg_admin;Password=pg_pass;"
        val config = DbConnectionFactory.parsePgSqlConfig(raw)

        assertEquals("pg_admin", config.username)
        assertEquals("pg_pass", config.password)
        assertEquals("CrmPg", config.databaseName)
        assertEquals("jdbc:postgresql://pg.example.com:54320/CrmPg", config.jdbcUrl)
    }

    @Test
    fun `parsePgSqlConfig parses JDBC style connection string correctly`() {
        val raw = "jdbc:postgresql://pg_user:pg_secret@pg.example.com:5432/CrmPg"
        val config = DbConnectionFactory.parsePgSqlConfig(raw)

        assertEquals("pg_user", config.username)
        assertEquals("pg_secret", config.password)
        assertEquals("CrmPg", config.databaseName)
    }

    @Test
    fun `getDataSource returns configured HikariDataSource with pool size 10`() {
        val env = MockEnvironment()
            .withProperty("ConnectionStrings.CRMDatabaseMS", DbConnectionFactory.DEFAULT_MS_SQL_CONNECTION_STRING)
            .withProperty("ConnectionStrings.CRMDatabasePG", DbConnectionFactory.DEFAULT_PG_SQL_CONNECTION_STRING)

        val factory = DbConnectionFactory(env)
        try {
            val msDs = factory.getDataSource(DatabaseProvider.Ms)
            assertNotNull(msDs)
            assertEquals(10, msDs.maximumPoolSize)
            assertEquals(5000, msDs.connectionTimeout)
            assertEquals(600000, msDs.idleTimeout)
            assertEquals("HikariPool-Ms", msDs.poolName)
        } finally {
            factory.destroy()
        }
    }

    @Test
    fun `validateConfiguration throws IllegalStateException when connection strings are missing and fallback not allowed`() {
        val env = MockEnvironment()
        val factory = DbConnectionFactory(env)

        val exception = org.junit.jupiter.api.assertThrows<IllegalStateException> {
            factory.validateConfiguration(allowFallbackDefaults = false)
        }
        assertTrue(exception.message!!.contains("Missing required database connection string configuration"))
    }

    @Test
    fun `validateConfiguration succeeds when connection strings are explicitly configured`() {
        val env = MockEnvironment()
            .withProperty("ConnectionStrings.CRMDatabaseMS", "Server=prod-sql;Database=CRMDatabaseMS;User Id=sa;Password=secret;")
            .withProperty("ConnectionStrings.CRMDatabasePG", "Host=prod-pg;Port=5432;Database=CRMDatabase;Username=postgres;Password=secret;")

        val factory = DbConnectionFactory(env)
        // Should not throw
        factory.validateConfiguration(allowFallbackDefaults = false)
    }

    @Test
    fun `validateConfiguration succeeds when fallback is allowed`() {
        val env = MockEnvironment()
        val factory = DbConnectionFactory(env)

        // Should not throw
        factory.validateConfiguration(allowFallbackDefaults = true)
    }
}
