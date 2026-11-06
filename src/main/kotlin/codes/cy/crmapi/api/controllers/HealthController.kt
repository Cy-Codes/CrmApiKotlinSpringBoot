package codes.cy.crmapi.api.controllers

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.MS_PROVIDER_CODE
import codes.cy.crmapi.domain.common.PG_PROVIDER_CODE
import codes.cy.crmapi.domain.common.toProviderCode
import codes.cy.crmapi.infrastructure.connections.IDbConnectionFactory
import java.time.Instant
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Serializable
data class HealthStatusResponse(
    val status: String,
    val timestamp: String,
)

@Serializable
data class DatabaseHealthResponse(
    val provider: String,
    val status: String,
    val server: String? = null,
    val error: String? = null,
)

@RestController
@RequestMapping("/health")
class HealthController(
    private val connectionFactory: IDbConnectionFactory,
) {
    companion object {
        private const val HEALTHY_STATUS = "Healthy"
        private const val CONNECTED_STATUS = "Connected"
        private const val UNHEALTHY_STATUS = "Unhealthy"
        private val logger = LoggerFactory.getLogger(HealthController::class.java)
    }

    @GetMapping
    fun get(): ResponseEntity<HealthStatusResponse> =
        ResponseEntity.ok(
            HealthStatusResponse(
                status = HEALTHY_STATUS,
                timestamp = Instant.now().toString(),
            ),
        )

    @GetMapping("/$MS_PROVIDER_CODE")
    fun checkMsSql(): ResponseEntity<DatabaseHealthResponse> =
        executeDatabaseHealthCheck(DatabaseProvider.Ms, "SQL Server")

    @GetMapping("/$PG_PROVIDER_CODE")
    fun checkPgSql(): ResponseEntity<DatabaseHealthResponse> =
        executeDatabaseHealthCheck(DatabaseProvider.Pg, "PostgreSQL")

    /**
     * Side-effecting I/O helper executing a live database connectivity probe.
     */
    private fun executeDatabaseHealthCheck(
        provider: DatabaseProvider,
        engineDisplayName: String,
    ): ResponseEntity<DatabaseHealthResponse> {
        val providerCode = provider.toProviderCode()
        return try {
            connectionFactory.createConnection(provider).use { conn ->
                ResponseEntity.ok(
                    DatabaseHealthResponse(
                        provider = providerCode,
                        status = CONNECTED_STATUS,
                        server = conn.catalog,
                    ),
                )
            }
        } catch (ex: Exception) {
            logger.warn("{} health check failed: {}", engineDisplayName, ex.message, ex)
            ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(
                    DatabaseHealthResponse(
                        provider = providerCode,
                        status = UNHEALTHY_STATUS,
                        error = ex.message,
                    ),
                )
        }
    }
}
