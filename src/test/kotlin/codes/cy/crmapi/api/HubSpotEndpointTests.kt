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
class HubSpotEndpointTests {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `getCompanies valid provider returns Ok`(provider: String) {
        mockMvc.perform(get("/api/$provider/v1/hubspot/companies?page=1&pageSize=20"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].companyId").value(101))
    }

    @ParameterizedTest
    @ValueSource(strings = ["ms", "pg"])
    fun `getCompanyById existing id returns Ok`(provider: String) {
        mockMvc.perform(get("/api/$provider/v1/hubspot/companies/101"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.companyId").value(101))
            .andExpect(jsonPath("$.name").value("HubSpot Co"))
    }
}
