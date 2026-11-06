package codes.cy.crmapi.api.controllers

import kotlinx.serialization.Serializable
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Serializable
data class OpenApiInfo(
    val title: String,
    val version: String,
    val description: String,
)

@Serializable
data class OpenApiOperation(
    val summary: String,
)

@Serializable
data class OpenApiPathItem(
    val get: OpenApiOperation? = null,
    val post: OpenApiOperation? = null,
)

@Serializable
data class OpenApiSpec(
    val openapi: String,
    val info: OpenApiInfo,
    val paths: Map<String, OpenApiPathItem>,
)

/**
 * Serves OpenAPI 3.0 specification and Swagger UI at `/swagger` and `/swagger/v1/swagger.json`
 * for parity with `CrmApiCSharp`.
 */
@RestController
class SwaggerController {
    companion object {
        private const val API_VERSION = "v1"
        private const val API_TITLE = "CrmApiKotlinSpringBoot - Multi-Provider Relational CRM Web API"
        private const val SWAGGER_JSON_PATH = "/swagger/v1/swagger.json"
    }

    @GetMapping(SWAGGER_JSON_PATH, produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getOpenApiSpec(): ResponseEntity<OpenApiSpec> =
        ResponseEntity.ok(
            OpenApiSpec(
                openapi = "3.0.1",
                info = OpenApiInfo(
                    title = API_TITLE,
                    version = API_VERSION,
                    description = "High-performance Kotlin + Spring Boot Web API serving Salesforce, HubSpot, and Dynamics 365 relational data from Microsoft SQL Server (ms) and PostgreSQL (pg).",
                ),
                paths = mapOf(
                    "/health" to OpenApiPathItem(get = OpenApiOperation("General health check")),
                    "/health/ms" to OpenApiPathItem(get = OpenApiOperation("SQL Server connectivity probe")),
                    "/health/pg" to OpenApiPathItem(get = OpenApiOperation("PostgreSQL connectivity probe")),
                    "/api/{provider}/v1/salesforce/accounts" to OpenApiPathItem(get = OpenApiOperation("List Salesforce Accounts")),
                    "/api/{provider}/v1/hubspot/companies" to OpenApiPathItem(get = OpenApiOperation("List HubSpot Companies")),
                    "/api/{provider}/v1/dynamics365/accounts" to OpenApiPathItem(get = OpenApiOperation("List Dynamics 365 Accounts")),
                    "/graphql" to OpenApiPathItem(post = OpenApiOperation("Execute GraphQL queries")),
                ),
            ),
        )

    @GetMapping("/swagger", "/swagger/index.html", produces = [MediaType.TEXT_HTML_VALUE])
    fun getSwaggerUi(): ResponseEntity<String> =
        ResponseEntity.ok(
            """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>$API_TITLE</title>
            </head>
            <body>
                <h1>$API_TITLE</h1>
                <p>OpenAPI Specification: <a href="$SWAGGER_JSON_PATH">$SWAGGER_JSON_PATH</a></p>
            </body>
            </html>
            """.trimIndent(),
        )
}
