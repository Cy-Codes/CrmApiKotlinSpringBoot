package codes.cy.crmapi.api.security

import javax.crypto.spec.SecretKeySpec
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.web.SecurityFilterChain

/**
 * Spring Security configuration establishing perimeter JWT & OAuth2 Resource Server authentication.
 *
 * Enforces stateless token validation when `crm.security.auth-enabled` is configured to `true`,
 * while permitting anonymous access for health checks and unauthenticated local development profiles.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    @param:Value("\${crm.security.auth-enabled:false}") private val authEnabled: Boolean,
    @param:Value("\${crm.security.jwt.secret:LocalStackCRMReferenceArchitectureJwtSigningSecretKey2026!}") private val jwtSecret: String,
) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests { auth ->
                auth.requestMatchers(
                    "/health/**",
                    "/actuator/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/error",
                ).permitAll()

                if (authEnabled) {
                    auth.requestMatchers("/api/**", "/graphql").authenticated()
                    auth.anyRequest().authenticated()
                } else {
                    auth.anyRequest().permitAll()
                }
            }

        if (authEnabled) {
            http.oauth2ResourceServer { oauth2 ->
                oauth2.jwt { }
            }
        }

        return http.build()
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        val secretKey = SecretKeySpec(jwtSecret.toByteArray(Charsets.UTF_8), "HmacSHA256")
        return NimbusJwtDecoder.withSecretKey(secretKey).build()
    }
}
