package codes.cy.crmapi.infrastructure.connections

import codes.cy.crmapi.domain.common.DatabaseProvider
import java.sql.Connection
import java.sql.DriverManager
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

/**
 * Immutable JDBC connection configuration resolved for a specific database engine.
 */
data class JdbcConnectionConfig(
    val jdbcUrl: String,
    val username: String,
    val password: String,
    val databaseName: String
)

/**
 * Factory for creating JDBC [Connection] instances based on the specified [DatabaseProvider].
 */
interface IDbConnectionFactory {
    /**
     * Resolves the immutable [JdbcConnectionConfig] for the given [provider].
     */
    fun resolveConnectionConfig(provider: DatabaseProvider): JdbcConnectionConfig

    /**
     * Opens and returns a new JDBC [Connection] for the given [provider].
     */
    fun openConnection(provider: DatabaseProvider): Connection

    /**
     * Alias for [openConnection] matching C# `CreateConnection` naming.
     */
    fun createConnection(provider: DatabaseProvider): Connection = openConnection(provider)
}

/**
 * Implementation of [IDbConnectionFactory] resolving connection strings from Spring [Environment].
 */
@Component
class DbConnectionFactory(
    private val environment: Environment
) : IDbConnectionFactory {

    override fun resolveConnectionConfig(provider: DatabaseProvider): JdbcConnectionConfig =
        when (provider) {
            DatabaseProvider.Ms -> parseMsSqlConfig(
                resolveRawConnectionString(msSqlCandidateKeys, DEFAULT_MS_SQL_CONNECTION_STRING)
            )
            DatabaseProvider.Pg -> parsePgSqlConfig(
                resolveRawConnectionString(pgSqlCandidateKeys, DEFAULT_PG_SQL_CONNECTION_STRING)
            )
        }

    override fun openConnection(provider: DatabaseProvider): Connection {
        val config = resolveConnectionConfig(provider)
        return DriverManager.getConnection(config.jdbcUrl, config.username, config.password)
    }

    private fun resolveRawConnectionString(candidateKeys: List<String>, defaultConnectionString: String): String =
        candidateKeys
            .asSequence()
            .flatMap { key ->
                sequenceOf(
                    environment.getProperty("ConnectionStrings.$key"),
                    environment.getProperty("ConnectionStrings:$key"),
                    environment.getProperty("connection-strings.$key"),
                    environment.getProperty(key)
                )
            }
            .firstOrNull { value -> !value.isNullOrBlank() }
            ?: defaultConnectionString

    companion object {
        const val DEFAULT_MS_SQL_CONNECTION_STRING: String =
            "Server=localhost,1433;Database=CRMDatabaseMS;User Id=sa;Password=CrmDev2026!Secure#Pass;TrustServerCertificate=True;"

        const val DEFAULT_PG_SQL_CONNECTION_STRING: String =
            "Host=localhost;Port=5432;Database=CRMDatabase;Username=postgres;Password=postgres;"

        private const val DEFAULT_MS_HOST: String = "localhost"
        private const val DEFAULT_MS_PORT: String = "1433"
        private const val DEFAULT_MS_DB: String = "CRMDatabaseMS"
        private const val DEFAULT_MS_USER: String = "sa"
        private const val DEFAULT_MS_PASSWORD: String = "CrmDev2026!Secure#Pass"

        private const val DEFAULT_PG_HOST: String = "localhost"
        private const val DEFAULT_PG_PORT: String = "5432"
        private const val DEFAULT_PG_DB: String = "CRMDatabase"
        private const val DEFAULT_PG_USER: String = "postgres"
        private const val DEFAULT_PG_PASSWORD: String = "postgres"

        private val msSqlCandidateKeys: List<String> = listOf("CRMDatabaseMS", "ms", "MsSql")
        private val pgSqlCandidateKeys: List<String> = listOf("CRMDatabasePG", "pg", "PgSql")

        /**
         * Pure parser converting a raw SQL Server connection string (ADO.NET or JDBC) into [JdbcConnectionConfig].
         */
        fun parseMsSqlConfig(raw: String): JdbcConnectionConfig {
            if (raw.startsWith("jdbc:sqlserver://", ignoreCase = true)) {
                return JdbcConnectionConfig(raw, DEFAULT_MS_USER, DEFAULT_MS_PASSWORD, DEFAULT_MS_DB)
            }
            val pairs = parseKeyValuePairs(raw)
            val serverRaw = pairs["server"] ?: "$DEFAULT_MS_HOST,$DEFAULT_MS_PORT"
            val serverParts = serverRaw.split(',', ':')
            val host = serverParts.firstOrNull()?.takeIf { it.isNotBlank() } ?: DEFAULT_MS_HOST
            val port = serverParts.getOrNull(1)?.takeIf { it.isNotBlank() } ?: DEFAULT_MS_PORT
            val database = pairs["database"] ?: pairs["initial catalog"] ?: DEFAULT_MS_DB
            val user = pairs["user id"] ?: pairs["uid"] ?: pairs["user"] ?: DEFAULT_MS_USER
            val password = pairs["password"] ?: pairs["pwd"] ?: DEFAULT_MS_PASSWORD
            val trustCert = pairs["trustservercertificate"] ?: "true"

            val jdbcUrl = "jdbc:sqlserver://$host:$port;databaseName=$database;encrypt=true;trustServerCertificate=$trustCert"
            return JdbcConnectionConfig(jdbcUrl, user, password, database)
        }

        /**
         * Pure parser converting a raw PostgreSQL connection string (ADO.NET or JDBC) into [JdbcConnectionConfig].
         */
        fun parsePgSqlConfig(raw: String): JdbcConnectionConfig {
            if (raw.startsWith("jdbc:postgresql://", ignoreCase = true)) {
                return JdbcConnectionConfig(raw, DEFAULT_PG_USER, DEFAULT_PG_PASSWORD, DEFAULT_PG_DB)
            }
            val pairs = parseKeyValuePairs(raw)
            val host = pairs["host"] ?: pairs["server"] ?: DEFAULT_PG_HOST
            val port = pairs["port"] ?: DEFAULT_PG_PORT
            val database = pairs["database"] ?: DEFAULT_PG_DB
            val user = pairs["username"] ?: pairs["user id"] ?: pairs["user"] ?: DEFAULT_PG_USER
            val password = pairs["password"] ?: DEFAULT_PG_PASSWORD

            val jdbcUrl = "jdbc:postgresql://$host:$port/$database"
            return JdbcConnectionConfig(jdbcUrl, user, password, database)
        }

        private fun parseKeyValuePairs(raw: String): Map<String, String> =
            raw.split(';')
                .mapNotNull { segment ->
                    val eqIdx = segment.indexOf('=')
                    if (eqIdx <= 0) {
                        null
                    } else {
                        val key = segment.substring(0, eqIdx).trim().lowercase()
                        val value = segment.substring(eqIdx + 1).trim()
                        key to value
                    }
                }
                .toMap()
    }
}
