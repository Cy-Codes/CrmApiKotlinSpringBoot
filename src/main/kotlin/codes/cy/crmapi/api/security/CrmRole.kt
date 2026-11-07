package codes.cy.crmapi.api.security

import java.util.Locale

/**
 * The single source of truth for API roles carried in the JWT [ROLES_CLAIM_NAME] claim (array or single string, case-insensitive).
 */
enum class CrmRole(val claimValue: String) {
    Admin("admin"),
    User("user"),
    ;

    /** Spring Security authority granted for this role (e.g. `ROLE_ADMIN`). */
    val authority: String get() = "$ROLE_AUTHORITY_PREFIX${claimValue.uppercase(Locale.ROOT)}"
}

/** JWT claim carrying the caller's roles. */
const val ROLES_CLAIM_NAME: String = "roles"

/** Prefix Spring Security uses for role authorities. */
const val ROLE_AUTHORITY_PREFIX: String = "ROLE_"
