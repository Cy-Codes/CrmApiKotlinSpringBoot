package codes.cy.crmapi.api.graphql

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.common.toDatabaseProvider
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import kotlin.reflect.full.memberProperties
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@Serializable
data class GraphqlRequest(
    val query: String = "",
    val variables: JsonObject? = null,
)

/**
 * Lightweight GraphQL HTTP endpoint executing [Query] operations and projecting requested selection sets
 * via JetBrains `kotlinx.serialization` [JsonObject].
 */
@RestController
class GraphqlController(
    private val queryResolver: Query,
) {
    companion object {
        private val OPERATION_REGEX =
            Regex("""(salesforceAccounts|salesforceAccountById|salesforceContacts|salesforceContactById|hubSpotCompanies|hubSpotCompanyById|hubSpotContacts|dynamics365Accounts|dynamics365AccountById|dynamics365Contacts)\s*(?:\(([^)]*)\))?\s*\{([^}]*)\}""")
        private val ARGUMENT_REGEX = Regex("""(\w+)\s*:\s*("[^"]*"|[^\s,]+)""")
        private val FIELD_TOKEN_REGEX = Regex("""[a-zA-Z_][a-zA-Z0-9_]*""")
    }

    @PostMapping("/graphql", consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun execute(@RequestBody request: GraphqlRequest): ResponseEntity<JsonObject> {
        val matches = OPERATION_REGEX.findAll(request.query).toList()
        if (matches.isEmpty()) {
            return ResponseEntity.badRequest().body(
                buildJsonObject {
                    put(
                        "errors",
                        JsonArray(
                            listOf(
                                buildJsonObject {
                                    put("message", "Invalid or unsupported GraphQL query.")
                                },
                            ),
                        ),
                    )
                },
            )
        }

        val dataMap = matches.associate { match ->
            val operationName = match.groupValues[1]
            val rawArgs = match.groupValues[2]
            val rawSelection = match.groupValues[3]
            val args = parseArguments(rawArgs)
            val selectedFields = parseSelectionFields(rawSelection)
            val result = invokeOperation(operationName, args)
            operationName to projectResult(result, selectedFields)
        }

        return ResponseEntity.ok(
            buildJsonObject {
                put("data", JsonObject(dataMap))
            },
        )
    }

    private fun parseArguments(rawArgs: String): Map<String, String> =
        ARGUMENT_REGEX.findAll(rawArgs).associate { match ->
            val key = match.groupValues[1]
            val value = match.groupValues[2].trim('"')
            key to value
        }

    private fun parseSelectionFields(rawSelection: String): List<String> =
        FIELD_TOKEN_REGEX.findAll(rawSelection).map { it.value }.toList()

    private fun invokeOperation(operationName: String, args: Map<String, String>): Any? {
        val provider = args["provider"]?.toDatabaseProvider() ?: DatabaseProvider.Ms
        val page = args["page"]?.toIntOrNull() ?: QueryOptions.DEFAULT_PAGE
        val pageSize = args["pageSize"]?.toIntOrNull() ?: QueryOptions.DEFAULT_PAGE_SIZE

        return when (operationName) {
            "salesforceAccounts" -> queryResolver.getSalesforceAccounts(provider, page, pageSize)
            "salesforceAccountById" -> queryResolver.getSalesforceAccountById(args["id"].orEmpty(), provider)
            "salesforceContacts" -> queryResolver.getSalesforceContacts(provider, page, pageSize)
            "salesforceContactById" -> queryResolver.getSalesforceContactById(args["id"].orEmpty(), provider)
            "hubSpotCompanies" -> queryResolver.getHubSpotCompanies(provider, page, pageSize)
            "hubSpotCompanyById" -> queryResolver.getHubSpotCompanyById(args["id"]?.toLongOrNull() ?: 0L, provider)
            "hubSpotContacts" -> queryResolver.getHubSpotContacts(provider, page, pageSize)
            "dynamics365Accounts" -> queryResolver.getDynamics365Accounts(provider, page, pageSize)
            "dynamics365AccountById" -> queryResolver.getDynamics365AccountById(UUID.fromString(args["id"].orEmpty()), provider)
            "dynamics365Contacts" -> queryResolver.getDynamics365Contacts(provider, page, pageSize)
            else -> null
        }
    }

    private fun projectResult(result: Any?, selectedFields: List<String>): JsonElement =
        when (result) {
            null -> JsonNull
            is Iterable<*> -> JsonArray(result.map { item -> projectEntity(item, selectedFields) })
            else -> projectEntity(result, selectedFields)
        }

    private fun projectEntity(entity: Any?, selectedFields: List<String>): JsonElement {
        if (entity == null) return JsonNull
        val propertiesByLowerName = entity::class.memberProperties.associateBy { it.name.lowercase() }
        val fieldsMap = if (selectedFields.isEmpty()) {
            propertiesByLowerName.values.associate { prop -> prop.name to toJsonElement(prop.getter.call(entity)) }
        } else {
            selectedFields.associateWith { fieldName ->
                toJsonElement(propertiesByLowerName[fieldName.lowercase()]?.getter?.call(entity))
            }
        }
        return JsonObject(fieldsMap)
    }

    private fun toJsonElement(value: Any?): JsonElement =
        when (value) {
            null -> JsonNull
            is Boolean -> JsonPrimitive(value)
            is Number -> JsonPrimitive(value)
            is LocalDateTime -> JsonPrimitive(value.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            else -> JsonPrimitive(value.toString())
        }
}
