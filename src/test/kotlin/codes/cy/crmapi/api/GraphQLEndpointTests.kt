package codes.cy.crmapi.api

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@Import(TestMockConfiguration::class)
class GraphQLEndpointTests {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `postGraphQL salesforceAccountsQuery returns Ok data`() {
        val payload = """
            {
                "query": "query { salesforceAccounts(provider: MS, page: 1, pageSize: 5) { id name industry } }"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.salesforceAccounts[0].name").value("Acme Corp"))
            .andExpect(jsonPath("$.data.salesforceAccounts[0].id").value(TestMockConfiguration.SAMPLE_SALESFORCE_ACCOUNT_ID))
    }

    @Test
    fun `postGraphQL salesforceAccountById returns expected record`() {
        val payload = """
            {
                "query": "query(${'$'}id: ID!) { salesforceAccountById(id: ${'$'}id, provider: MS) { id name industry } }",
                "variables": { "id": "${TestMockConfiguration.SAMPLE_SALESFORCE_ACCOUNT_ID}" }
            }
        """.trimIndent()

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.salesforceAccountById.name").value("Acme Corp"))
            .andExpect(jsonPath("$.data.salesforceAccountById.id").value(TestMockConfiguration.SAMPLE_SALESFORCE_ACCOUNT_ID))
    }

    @Test
    fun `postGraphQL hubSpotCompaniesQuery returns Ok data`() {
        val payload = """
            {
                "query": "query { hubSpotCompanies(provider: MS, page: 1, pageSize: 5) { companyId name domain } }"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.hubSpotCompanies[0].name").value("HubSpot Co"))
            .andExpect(jsonPath("$.data.hubSpotCompanies[0].companyId").value(TestMockConfiguration.SAMPLE_HUBSPOT_COMPANY_ID.toString()))
    }

    @Test
    fun `postGraphQL dynamics365AccountsQuery returns Ok data`() {
        val payload = """
            {
                "query": "query { dynamics365Accounts(provider: MS, page: 1, pageSize: 5) { accountId name } }"
            }
        """.trimIndent()

        mockMvc.perform(
            post("/graphql")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.dynamics365Accounts[0].name").value("D365 Account"))
            .andExpect(jsonPath("$.data.dynamics365Accounts[0].accountId").value(TestMockConfiguration.SAMPLE_DYNAMICS_ACCOUNT_ID.toString()))
    }
}
