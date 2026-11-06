package codes.cy.crmapi.api

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@Import(TestMockConfiguration::class)
class SalesforceEndpointTests {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `getAccounts valid provider returns Ok with PagedResult`(provider: String) {
        mockMvc.perform(get("/api/$provider/v1/salesforce/accounts?page=1&pageSize=20"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.totalCount").value(1))
            .andExpect(jsonPath("$.items[0].id").value("001000000000001AAA"))
    }

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `getAccountById existing id returns Ok with Account`(provider: String) {
        mockMvc.perform(get("/api/$provider/v1/salesforce/accounts/001000000000001AAA"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value("001000000000001AAA"))
            .andExpect(jsonPath("$.name").value("Acme Corp"))
    }

    @Test
    fun `getAccounts invalid provider returns NotFound`() {
        mockMvc.perform(get("/api/invalid/v1/salesforce/accounts"))
            .andExpect(status().isNotFound)
    }

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `getContactsByAccountId returns Ok list`(provider: String) {
        mockMvc.perform(get("/api/$provider/v1/salesforce/accounts/001000000000001AAA/contacts"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].lastName").value("Doe"))
    }
}
