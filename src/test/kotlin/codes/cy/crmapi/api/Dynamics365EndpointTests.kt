package codes.cy.crmapi.api

import org.junit.jupiter.api.BeforeEach
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
class Dynamics365EndpointTests {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc
    private val testGuid = TestMockConfiguration.SAMPLE_DYNAMICS_ACCOUNT_ID.toString()

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `getAccounts valid provider returns Ok`(provider: String) {
        mockMvc.perform(get("/api/$provider/v1/dynamics365/accounts?page=1&pageSize=20"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].accountId").value(testGuid))
    }

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `getAccountById existing guid returns Ok`(provider: String) {
        mockMvc.perform(get("/api/$provider/v1/dynamics365/accounts/$testGuid"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.accountId").value(testGuid))
            .andExpect(jsonPath("$.name").value("D365 Account"))
    }
}
