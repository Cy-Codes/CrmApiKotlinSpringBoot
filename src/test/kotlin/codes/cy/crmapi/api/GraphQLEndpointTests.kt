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
    }
}
