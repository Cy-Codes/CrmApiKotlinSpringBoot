package codes.cy.crmapi.api

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * Controller behavior of the create / replace / delete endpoints for every provider, using mocked command services
 * and an `admin` token (authorization rules are covered by [JwtAuthenticationTests]).
 */
@SpringBootTest
@Import(TestMockConfiguration::class)
class WriteEndpointTests {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    private fun MockHttpServletRequestBuilder.asAdmin(body: String? = null): MockHttpServletRequestBuilder =
        header(HttpHeaders.AUTHORIZATION, "Bearer ${JwtAuthenticationTests.generateJwtToken("admin")}")
            .let { builder -> if (body == null) builder else builder.contentType(MediaType.APPLICATION_JSON).content(body) }

    // ------------------------------------------------------------ Salesforce

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `salesforce create returns 201 with Location and re-read entity`(provider: String) {
        mockMvc.perform(post("/api/$provider/v1/salesforce/accounts").asAdmin(SF_ACCOUNT_BODY))
            .andExpect(status().isCreated)
            .andExpect(header().string(HttpHeaders.LOCATION, org.hamcrest.Matchers.endsWith("/api/$provider/v1/salesforce/accounts/${TestMockConfiguration.CREATED_SALESFORCE_ACCOUNT_ID}")))
            .andExpect(jsonPath("$.id").value(TestMockConfiguration.CREATED_SALESFORCE_ACCOUNT_ID))
            .andExpect(jsonPath("$.name").value("Acme"))
    }

    @Test
    fun `salesforce create without required ownerId returns 400 problem details`() {
        mockMvc.perform(post("/api/ms/v1/salesforce/accounts").asAdmin("""{"name":"Acme"}"""))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.status").value(400))
    }

    @Test
    fun `salesforce create with blank name returns 400`() {
        mockMvc.perform(post("/api/ms/v1/salesforce/accounts").asAdmin("""{"name":" ","ownerId":"005OWNER"}"""))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `salesforce create with malformed json returns 400`() {
        mockMvc.perform(post("/api/pg/v1/salesforce/accounts").asAdmin("{not json"))
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `salesforce create constraint violation returns 409 problem details`() {
        mockMvc.perform(
            post("/api/pg/v1/salesforce/accounts")
                .asAdmin("""{"name":"${TestMockConfiguration.CONFLICT_ENTITY_NAME}","ownerId":"005OWNER"}"""),
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.title").value("Conflict"))
    }

    @Test
    fun `salesforce update returns 200 with updated entity`() {
        mockMvc.perform(
            put("/api/ms/v1/salesforce/accounts/${TestMockConfiguration.SAMPLE_SALESFORCE_ACCOUNT_ID}").asAdmin(SF_ACCOUNT_BODY),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(TestMockConfiguration.SAMPLE_SALESFORCE_ACCOUNT_ID))
    }

    @Test
    fun `salesforce update of missing id returns 404 problem details`() {
        mockMvc.perform(put("/api/ms/v1/salesforce/accounts/001MISSING000000AAA").asAdmin(SF_ACCOUNT_BODY))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.title").value("Not Found"))
    }

    @Test
    fun `salesforce delete returns 204 and 404 when missing`() {
        mockMvc.perform(delete("/api/pg/v1/salesforce/accounts/${TestMockConfiguration.SAMPLE_SALESFORCE_ACCOUNT_ID}").asAdmin())
            .andExpect(status().isNoContent)
        mockMvc.perform(delete("/api/pg/v1/salesforce/accounts/001MISSING000000AAA").asAdmin())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.status").value(404))
    }

    // --------------------------------------------------------------- HubSpot

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `hubspot create returns 201 with Location`(provider: String) {
        mockMvc.perform(post("/api/$provider/v1/hubspot/companies").asAdmin("""{"name":"HubSpot Co"}"""))
            .andExpect(status().isCreated)
            .andExpect(header().string(HttpHeaders.LOCATION, org.hamcrest.Matchers.endsWith("/api/$provider/v1/hubspot/companies/${TestMockConfiguration.CREATED_HUBSPOT_COMPANY_ID}")))
            .andExpect(jsonPath("$.companyId").value(TestMockConfiguration.CREATED_HUBSPOT_COMPANY_ID))
    }

    @Test
    fun `hubspot update delete and conflict behave per contract`() {
        mockMvc.perform(put("/api/ms/v1/hubspot/companies/${TestMockConfiguration.SAMPLE_HUBSPOT_COMPANY_ID}").asAdmin("""{"name":"Renamed"}"""))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.name").value("Renamed"))
        mockMvc.perform(put("/api/ms/v1/hubspot/companies/1").asAdmin("""{"name":"Renamed"}"""))
            .andExpect(status().isNotFound)
        mockMvc.perform(delete("/api/ms/v1/hubspot/companies/${TestMockConfiguration.SAMPLE_HUBSPOT_COMPANY_ID}").asAdmin())
            .andExpect(status().isNoContent)
        mockMvc.perform(delete("/api/ms/v1/hubspot/companies/1").asAdmin())
            .andExpect(status().isNotFound)
        mockMvc.perform(post("/api/pg/v1/hubspot/companies").asAdmin("""{"name":"${TestMockConfiguration.CONFLICT_ENTITY_NAME}"}"""))
            .andExpect(status().isConflict)
    }

    @Test
    fun `hubspot non numeric id returns 400`() {
        mockMvc.perform(delete("/api/ms/v1/hubspot/companies/abc").asAdmin())
            .andExpect(status().isBadRequest)
    }

    // ----------------------------------------------------------- Dynamics365

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `dynamics create returns 201 with Location and accepts both business unit spellings`(provider: String) {
        listOf("owningBusinessUnit", "owningbusinessunit").forEach { property ->
            mockMvc.perform(post("/api/$provider/v1/dynamics365/accounts").asAdmin(d365AccountBody(property)))
                .andExpect(status().isCreated)
                .andExpect(header().string(HttpHeaders.LOCATION, org.hamcrest.Matchers.endsWith("/api/$provider/v1/dynamics365/accounts/${TestMockConfiguration.CREATED_DYNAMICS_ACCOUNT_ID}")))
                .andExpect(jsonPath("$.accountId").value(TestMockConfiguration.CREATED_DYNAMICS_ACCOUNT_ID.toString()))
        }
    }

    @Test
    fun `dynamics create without owning business unit returns 400`() {
        mockMvc.perform(
            post("/api/ms/v1/dynamics365/accounts").asAdmin("""{"name":"D365","ownerId":"${TestMockConfiguration.SAMPLE_DYNAMICS_ACCOUNT_ID}"}"""),
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `dynamics update delete and conflict behave per contract`() {
        val id = TestMockConfiguration.SAMPLE_DYNAMICS_ACCOUNT_ID
        mockMvc.perform(put("/api/pg/v1/dynamics365/accounts/$id").asAdmin(d365AccountBody("owningBusinessUnit")))
            .andExpect(status().isOk)
        mockMvc.perform(put("/api/pg/v1/dynamics365/accounts/00000000-0000-0000-0000-000000000000").asAdmin(d365AccountBody("owningBusinessUnit")))
            .andExpect(status().isNotFound)
        mockMvc.perform(delete("/api/pg/v1/dynamics365/accounts/$id").asAdmin())
            .andExpect(status().isNoContent)
        mockMvc.perform(delete("/api/pg/v1/dynamics365/accounts/00000000-0000-0000-0000-000000000000").asAdmin())
            .andExpect(status().isNotFound)
        mockMvc.perform(
            post("/api/pg/v1/dynamics365/accounts")
                .asAdmin(d365AccountBody("owningBusinessUnit", TestMockConfiguration.CONFLICT_ENTITY_NAME)),
        )
            .andExpect(status().isConflict)
    }

    private fun d365AccountBody(businessUnitProperty: String, name: String = "D365 New"): String =
        """{"name":"$name","ownerId":"${TestMockConfiguration.SAMPLE_DYNAMICS_ACCOUNT_ID}","$businessUnitProperty":"${TestMockConfiguration.SAMPLE_DYNAMICS_ACCOUNT_ID}"}"""

    companion object {
        private const val SF_ACCOUNT_BODY = """{"name":"Acme","ownerId":"005000000000001AAA","industry":"Technology"}"""
    }
}
