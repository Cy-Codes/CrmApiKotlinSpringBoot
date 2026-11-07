package codes.cy.crmapi.api

import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.MACSigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.util.Date
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
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

/**
 * Role-based authorization model (always enforced): anonymous GET, `admin`/`user` for POST/PUT, `admin` only for DELETE.
 */
@SpringBootTest
@Import(TestMockConfiguration::class)
class JwtAuthenticationTests {

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

    private fun createAccount(token: String?): ResultActions =
        mockMvc.perform(
            post(ACCOUNTS_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ACCOUNT_BODY)
                .let { builder -> token?.let { builder.header(HttpHeaders.AUTHORIZATION, "Bearer $it") } ?: builder },
        )

    private fun replaceAccount(token: String?): ResultActions =
        mockMvc.perform(
            put(ACCOUNT_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(ACCOUNT_BODY)
                .let { builder -> token?.let { builder.header(HttpHeaders.AUTHORIZATION, "Bearer $it") } ?: builder },
        )

    private fun deleteAccount(token: String?): ResultActions =
        mockMvc.perform(
            delete(ACCOUNT_ROUTE)
                .let { builder -> token?.let { builder.header(HttpHeaders.AUTHORIZATION, "Bearer $it") } ?: builder },
        )

    @Test
    fun `anonymous GET returns 200 OK`() {
        mockMvc.perform(get(ACCOUNTS_ROUTE))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.items[0].id").value(TestMockConfiguration.SAMPLE_SALESFORCE_ACCOUNT_ID))
    }

    @Test
    fun `anonymous graphql POST is not blocked by the write rules`() {
        mockMvc.perform(
            post("/graphql").contentType(MediaType.APPLICATION_JSON).content("""{"query":"{ __typename }"}"""),
        )
            .andExpect(status().is2xxSuccessful)
    }

    @Test
    fun `health endpoint stays anonymous`() {
        mockMvc.perform(get("/health"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value("Healthy"))
    }

    @Test
    fun `anonymous POST PUT DELETE return 401 problem details`() {
        listOf(createAccount(null), replaceAccount(null), deleteAccount(null)).forEach { result ->
            result
                .andExpect(status().isUnauthorized)
                .andExpect(header().exists(HttpHeaders.WWW_AUTHENTICATE))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
        }
    }

    @Test
    fun `invalid token on POST returns 401`() {
        createAccount("not.a.jwt").andExpect(status().isUnauthorized)
    }

    @Test
    fun `token signed with another secret returns 401`() {
        createAccount(generateJwtToken("admin", secret = "AnotherSecretKeyThatIsAtLeast32BytesLong!!"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `expired token returns 401`() {
        createAccount(generateJwtToken("admin", expiresInMillis = -EXPIRED_OFFSET_MILLIS)).andExpect(status().isUnauthorized)
    }

    @Test
    fun `role user can POST and PUT`() {
        val token = generateJwtToken("user")

        createAccount(token).andExpect(status().isCreated)
        replaceAccount(token).andExpect(status().isOk)
    }

    @Test
    fun `role user cannot DELETE and receives 403 problem details`() {
        deleteAccount(generateJwtToken("user"))
            .andExpect(status().isForbidden)
            .andExpect(jsonPath("$.status").value(403))
            .andExpect(jsonPath("$.title").value("Forbidden"))
    }

    @Test
    fun `role admin can POST PUT and DELETE`() {
        val token = generateJwtToken("admin")

        createAccount(token).andExpect(status().isCreated)
        replaceAccount(token).andExpect(status().isOk)
        deleteAccount(token).andExpect(status().isNoContent)
    }

    @Test
    fun `token without roles gets 403 on every write`() {
        val token = generateJwtToken()

        createAccount(token).andExpect(status().isForbidden)
        replaceAccount(token).andExpect(status().isForbidden)
        deleteAccount(token).andExpect(status().isForbidden)
    }

    @Test
    fun `unrelated role gets 403`() {
        createAccount(generateJwtToken("CrmReader")).andExpect(status().isForbidden)
    }

    @ParameterizedTest
    @ValueSource(strings = ["ADMIN", "Admin", "aDmIn"])
    fun `roles are matched case-insensitively`(role: String) {
        deleteAccount(generateJwtToken(role)).andExpect(status().isNoContent)
    }

    @Test
    fun `roles claim may be a single string`() {
        deleteAccount(generateJwtToken(rolesClaim = "admin")).andExpect(status().isNoContent)
    }

    @Test
    fun `roles claim may be an array with several roles`() {
        deleteAccount(generateJwtToken("user", "admin")).andExpect(status().isNoContent)
    }

    @Test
    fun `valid token with role GET still returns 200`() {
        mockMvc.perform(get(ACCOUNTS_ROUTE).header(HttpHeaders.AUTHORIZATION, "Bearer ${generateJwtToken("user")}"))
            .andExpect(status().isOk)
    }

    companion object {
        private const val TEST_SECRET = "LocalStackCRMReferenceArchitectureJwtSigningSecretKey2026!"
        private const val TEST_ISSUER = "https://auth.localstack.local"
        private const val TEST_AUDIENCE = "crm-api"
        private const val ROLES_CLAIM = "roles"
        private const val TOKEN_LIFETIME_MILLIS = 3_600_000L
        private const val EXPIRED_OFFSET_MILLIS = 3_600_000L
        private const val ACCOUNTS_ROUTE = "/api/ms/v1/salesforce/accounts"
        private const val ACCOUNT_ROUTE = "$ACCOUNTS_ROUTE/001000000000001AAA"
        private const val ACCOUNT_BODY = """{"name":"Acme","ownerId":"005000000000001AAA"}"""

        /**
         * Mints an HS256 token. [roles] become the `roles` array claim (omitted when empty) unless [rolesClaim] supplies a raw value.
         */
        fun generateJwtToken(
            vararg roles: String,
            rolesClaim: Any? = null,
            secret: String = TEST_SECRET,
            expiresInMillis: Long = TOKEN_LIFETIME_MILLIS,
        ): String {
            val claimsSet = JWTClaimsSet.Builder()
                .subject("user-123")
                .issuer(TEST_ISSUER)
                .audience(TEST_AUDIENCE)
                .expirationTime(Date(System.currentTimeMillis() + expiresInMillis))
                .let { builder ->
                    when {
                        rolesClaim != null -> builder.claim(ROLES_CLAIM, rolesClaim)
                        roles.isNotEmpty() -> builder.claim(ROLES_CLAIM, roles.toList())
                        else -> builder
                    }
                }
                .build()

            val signedJWT = SignedJWT(JWSHeader(JWSAlgorithm.HS256), claimsSet)
            signedJWT.sign(MACSigner(secret.toByteArray(Charsets.UTF_8)))
            return signedJWT.serialize()
        }
    }
}
