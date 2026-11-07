package codes.cy.crmapi.api.security

import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain

/**
 * Spring Security configuration establishing perimeter JWT & OAuth2 Resource Server authentication. Always enforced.
 *
 * Authorization model (roles come from the JWT `roles` claim, see [CrmRole]):
 * - `GET` / `HEAD` / `OPTIONS`, `/health`, `/graphql`, Swagger and actuator remain anonymous.
 * - `POST` / `PUT` on `/api` routes require role `admin` or `user`.
 * - `DELETE` on `/api` routes requires role `admin`.
 * - Missing / invalid token -> `401`, valid token without a sufficient role -> `403` (both RFC 7807 Problem Details).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    @param:Value("\${crm.security.jwt.secret:LocalStackCRMReferenceArchitectureJwtSigningSecretKey2026!}") private val jwtSecret: String,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(*PUBLIC_PATH_PATTERNS.toTypedArray()).permitAll()
                auth.requestMatchers(HttpMethod.GET, ANY_PATH_PATTERN).permitAll()
                auth.requestMatchers(HttpMethod.HEAD, ANY_PATH_PATTERN).permitAll()
                auth.requestMatchers(HttpMethod.OPTIONS, ANY_PATH_PATTERN).permitAll()
                auth.requestMatchers(HttpMethod.POST, API_PATH_PATTERN).hasAnyAuthority(CrmRole.Admin.authority, CrmRole.User.authority)
                auth.requestMatchers(HttpMethod.PUT, API_PATH_PATTERN).hasAnyAuthority(CrmRole.Admin.authority, CrmRole.User.authority)
                auth.requestMatchers(HttpMethod.DELETE, API_PATH_PATTERN).hasAuthority(CrmRole.Admin.authority)
                auth.anyRequest().denyAll()
            }
            .oauth2ResourceServer { oauth2 ->
                oauth2
                    .authenticationEntryPoint(ProblemDetailsAuthenticationEntryPoint())
                    .accessDeniedHandler(ProblemDetailsAccessDeniedHandler())
                    .jwt { jwt -> jwt.jwtAuthenticationConverter(createRoleJwtAuthenticationConverter()) }
            }

        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val secretKey = SecretKeySpec(jwtSecret.toByteArray(Charsets.UTF_8), "HmacSHA256")
        return NimbusJwtDecoder.withSecretKey(secretKey).build()
    }

    companion object {
        private const val ANY_PATH_PATTERN: String = "/**"
        private const val API_PATH_PATTERN: String = "/api/**"
        private val PUBLIC_PATH_PATTERNS = listOf(
            "/health/**",
            "/actuator/**",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/graphql",
            "/error",
        )
    }
}
