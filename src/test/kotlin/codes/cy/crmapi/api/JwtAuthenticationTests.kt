package codes.cy.crmapi.api

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.util.Date
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(properties = ["crm.security.auth-enabled=true"])
@Import(TestMockConfiguration::class)
class JwtAuthenticationTests {

    @Autowired
    private lateinit var context: WebApplicationContext

    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
        mockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply<org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder>(springSecurity())
            .build()
    }

    companion object {
        private const val TEST_SECRET = "LocalStackCRMReferenceArchitectureJwtSigningSecretKey2026!"
        private const val TEST_ISSUER = "https://auth.localstack.local"
        private const val TEST_AUDIENCE = "crm-api"

        fun generateValidJwtToken(): String {
            val claimsSet = JWTClaimsSet.Builder()
                .subject("user-123")
                .issuer(TEST_ISSUER)
                .audience(TEST_AUDIENCE)
                .claim("role", "CrmReader")
                .claim("scope", "crm:read")
                .expirationTime(Date(System.currentTimeMillis() + 3600_000))
                .build()

            val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)
            val signer = MACSigner(TEST_SECRET.toByteArray(Charsets.UTF_8))
            signedJWT.sign(signer)
            return signedJWT.serialize()
        }
    }

    @Test
    fun `request without bearer token when auth enabled returns 401 Unauthorized`() {
        mockMvc.perform(get("/api/ms/v1/salesforce/accounts"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `request with valid bearer token when auth enabled returns 200 OK`() {
        val token = generateValidJwtToken()

        mockMvc.perform(
            get("/api/ms/v1/salesforce/accounts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].id").value(TestMockConfiguration.SAMPLE_SALESFORCE_ACCOUNT_ID))
    }

    @Test
    fun `health endpoint permits unauthenticated access even when auth is enabled`() {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("Healthy"))
    }
}
