package codes.cy.crmapi.api.security

import java.util.Locale
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter

private val ROLE_SEPARATOR_REGEX = Regex("[\\s,]+")

/**
 * Pure extraction of role names from the `roles` claim, which may be a JSON array or a single (space/comma separated) string.
 */
fun Jwt.getRoleNames(): List<String> =
    when (val claim = claims[ROLES_CLAIM_NAME]) {
        is String -> claim.split(ROLE_SEPARATOR_REGEX)
        is Collection<*> -> claim.filterNotNull().map { it.toString() }
        else -> emptyList()
    }.filter { it.isNotBlank() }

/**
 * Pure mapping of [Jwt] `roles` into upper-cased `ROLE_*` authorities (case-insensitive role matching).
 */
fun Jwt.toRoleAuthorities(): List<GrantedAuthority> =
    getRoleNames().map { SimpleGrantedAuthority("${ROLE_AUTHORITY_PREFIX}${it.uppercase(Locale.ROOT)}") }

/**
 * Builds the [JwtAuthenticationConverter] mapping the `roles` claim to `ROLE_ADMIN` / `ROLE_USER` authorities.
 */
fun createRoleJwtAuthenticationConverter(): JwtAuthenticationConverter =
    JwtAuthenticationConverter().apply {
        setJwtGrantedAuthoritiesConverter(Converter<Jwt, Collection<GrantedAuthority>> { jwt -> jwt.toRoleAuthorities() })
    }
