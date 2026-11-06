package codes.cy.crmapi.api

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
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
class ExceptionHandlingAndFilterTests {
    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build()
    }

    @Test
    fun `getAccounts with filters passes filters to QueryOptions`() {
        mockMvc.perform(get("/api/ms/v1/salesforce/accounts?Industry=Technology"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items.length()").value(1))
            .andExpect(jsonPath("$.items[0].name").value("Tech Corp"))
            .andExpect(jsonPath("$.items[0].isDeleted").value(false))
            .andExpect(jsonPath("$.totalPages").value(1))
            .andExpect(jsonPath("$.hasPreviousPage").value(false))
            .andExpect(jsonPath("$.hasNextPage").value(false))
    }

    @Test
    fun `healthEndpoint returns Healthy status`() {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("Healthy"))
    }
}
