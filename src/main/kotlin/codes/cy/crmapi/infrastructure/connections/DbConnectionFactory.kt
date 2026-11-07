package codes.cy.crmapi.infrastructure.connections

import codes.cy.crmapi.domain.common.DatabaseProvider
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import jakarta.annotation.PostConstruct
import java.sql.Connection
import java.util.concurrent.ConcurrentHashMap
import org.springframework.beans.factory.DisposableBean
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
 * Factory for creating pooled JDBC [Connection] instances based on the specified [DatabaseProvider].
 */
interface IDbConnectionFactory {
    /**
     * Resolves the immutable [JdbcConnectionConfig] for the given [provider].
     */
    fun resolveConnectionConfig(provider: DatabaseProvider): JdbcConnectionConfig

    /**
     * Opens and returns a JDBC [Connection] from the connection pool for the given [provider].
     */
    fun openConnection(provider: DatabaseProvider): Connection

    /**
     * Alias for [openConnection] matching C# `CreateConnection` naming.
     */
    fun createConnection(provider: DatabaseProvider): Connection = openConnection(provider)

    /**
     * Validates that required database connection strings are present in configuration.
     * Throws [IllegalStateException] if required connection strings are missing.
     */
    fun validateConfiguration(allowFallbackDefaults: Boolean = false)
}

/**
 * Implementation of [IDbConnectionFactory] managing pooled [HikariDataSource] instances.
 */
@Component
class DbConnectionFactory(
    private val environment: Environment
) : IDbConnectionFactory, DisposableBean {

    private val dataSources = ConcurrentHashMap<DatabaseProvider, HikariDataSource>()

    @PostConstruct
    fun initValidation() {
        val activeProfiles = environment.activeProfiles
        val isLocalOrTest = activeProfiles.isEmpty() || activeProfiles.any { it in listOf("local", "dev", "test") }
        validateConfiguration(allowFallbackDefaults = isLocalOrTest)
    }

    override fun validateConfiguration(allowFallbackDefaults: Boolean) {
        if (allowFallbackDefaults) return
        val msRaw = findRawConnectionString(msSqlCandidateKeys)
        val pgRaw = findRawConnectionString(pgSqlCandidateKeys)
        if (msRaw == null || pgRaw == null) {
            val missing = buildList {
                if (msRaw == null) add("SQL Server (CRMDatabaseMS)")
                if (pgRaw == null) add("PostgreSQL (CRMDatabasePG)")
            }
            throw IllegalStateException(
                "Missing required database connection string configuration for: ${missing.joinToString(", ")}. " +
                "Connection strings must be configured explicitly when outside local/test fallback mode."
            )
        }
    }

    override fun resolveConnectionConfig(provider: DatabaseProvider): JdbcConnectionConfig =
        when (provider) {
            DatabaseProvider.Ms -> parseMsSqlConfig(
                resolveRawConnectionString(msSqlCandidateKeys, DEFAULT_MS_SQL_CONNECTION_STRING)
            )
            DatabaseProvider.Pg -> parsePgSqlConfig(
                resolveRawConnectionString(pgSqlCandidateKeys, DEFAULT_PG_SQL_CONNECTION_STRING)
            )
        }

    fun getDataSource(provider: DatabaseProvider): HikariDataSource =
        dataSources.computeIfAbsent(provider) { p ->
            val config = resolveConnectionConfig(p)
            val hikariConfig = HikariConfig().apply {
                jdbcUrl = config.jdbcUrl
                username = config.username
                password = config.password
                maximumPoolSize = DEFAULT_MAX_POOL_SIZE
                connectionTimeout = DEFAULT_CONNECTION_TIMEOUT_MS
                idleTimeout = DEFAULT_IDLE_TIMEOUT_MS
                initializationFailTimeout = DEFAULT_INIT_FAIL_TIMEOUT_MS
                poolName = "HikariPool-${p.name}"
            }
            HikariDataSource(hikariConfig)
        }

    override fun openConnection(provider: DatabaseProvider): Connection =
        getDataSource(provider).connection

    override fun destroy() {
        dataSources.values.forEach { dataSource ->
            runCatching { dataSource.close() }
        }
        dataSources.clear()
    }

    private fun findRawConnectionString(candidateKeys: List<String>): String? =
        candidateKeys
            .asSequence()
            .flatMap { key ->
                sequenceOf(
                    environment.getProperty("ConnectionStrings.$key"),
                    environment.getProperty("ConnectionStrings:$key"),
                    environment.getProperty("connection-strings.$key"),
                    environment.getProperty("connectionstrings.$key"),
                    environment.getProperty(key)
                )
            }
            .firstOrNull { value -> !value.isNullOrBlank() }

    private fun resolveRawConnectionString(candidateKeys: List<String>, defaultConnectionString: String): String =
        findRawConnectionString(candidateKeys) ?: defaultConnectionString

    companion object {
        const val DEFAULT_MAX_POOL_SIZE: Int = 10
        const val DEFAULT_CONNECTION_TIMEOUT_MS: Long = 5000L
        const val DEFAULT_IDLE_TIMEOUT_MS: Long = 600000L
        const val DEFAULT_INIT_FAIL_TIMEOUT_MS: Long = -1L

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
            val pairs = parseKeyValuePairs(raw)
            val user = pairs["user id"] ?: pairs["uid"] ?: pairs["user"] ?: pairs["username"] ?: DEFAULT_MS_USER
            val password = pairs["password"] ?: pairs["pwd"] ?: DEFAULT_MS_PASSWORD
            val database = pairs["database"] ?: pairs["initial catalog"] ?: pairs["databasename"] ?: DEFAULT_MS_DB

            if (raw.startsWith("jdbc:sqlserver://", ignoreCase = true)) {
                return JdbcConnectionConfig(raw, user, password, database)
            }

            val serverRaw = pairs["server"] ?: "$DEFAULT_MS_HOST,$DEFAULT_MS_PORT"
            val serverParts = serverRaw.split(',', ':')
            val host = serverParts.firstOrNull()?.takeIf { it.isNotBlank() } ?: DEFAULT_MS_HOST
            val port = serverParts.getOrNull(1)?.takeIf { it.isNotBlank() } ?: DEFAULT_MS_PORT
            val trustCert = pairs["trustservercertificate"] ?: "true"

            val jdbcUrl = "jdbc:sqlserver://$host:$port;databaseName=$database;encrypt=true;trustServerCertificate=$trustCert"
            return JdbcConnectionConfig(jdbcUrl, user, password, database)
        }

        /**
         * Pure parser converting a raw PostgreSQL connection string (ADO.NET or JDBC) into [JdbcConnectionConfig].
         */
        fun parsePgSqlConfig(raw: String): JdbcConnectionConfig {
            if (raw.startsWith("jdbc:postgresql://", ignoreCase = true)) {
                val withoutPrefix = raw.removePrefix("jdbc:postgresql://")
                val queryParams = if (withoutPrefix.contains('?')) {
                    withoutPrefix.substringAfter('?')
                        .split('&')
                        .mapNotNull { segment ->
                            val eqIdx = segment.indexOf('=')
                            if (eqIdx > 0) segment.substring(0, eqIdx).trim().lowercase() to segment.substring(eqIdx + 1).trim() else null
                        }.toMap()
                } else emptyMap()

                val pathPart = withoutPrefix.substringBefore('?')
                val userInfo = if (pathPart.contains('@')) pathPart.substringBefore('@') else null
                val userFromUri = userInfo?.substringBefore(':')
                val passFromUri = userInfo?.substringAfter(':', "")?.takeIf { it.isNotBlank() }
                val dbFromUri = pathPart.substringAfterLast('/').takeIf { it.isNotBlank() }

                val user = queryParams["user"] ?: queryParams["username"] ?: userFromUri ?: DEFAULT_PG_USER
                val password = queryParams["password"] ?: passFromUri ?: DEFAULT_PG_PASSWORD
                val database = queryParams["database"] ?: dbFromUri ?: DEFAULT_PG_DB

                return JdbcConnectionConfig(raw, user, password, database)
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
