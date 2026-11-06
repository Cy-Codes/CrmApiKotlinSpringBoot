package codes.cy.crmapi.domain.common

/**
 * Canonical route code for Microsoft SQL Server.
 */
const val MS_PROVIDER_CODE: String = "ms"

/**
 * Canonical route code for PostgreSQL.
 */
const val PG_PROVIDER_CODE: String = "pg"

/**
 * Pure transformation parsing a route provider string ('ms' or 'pg') into a [DatabaseProvider].
 */
fun String?.toDatabaseProvider(): DatabaseProvider =
    when (this?.lowercase()) {
        MS_PROVIDER_CODE -> DatabaseProvider.Ms
        PG_PROVIDER_CODE -> DatabaseProvider.Pg
        else -> throw IllegalArgumentException(
            "Invalid provider '$this'. Must be '$MS_PROVIDER_CODE' or '$PG_PROVIDER_CODE'."
        )
    }

/**
 * Pure transformation formatting a [DatabaseProvider] into its canonical route code.
 */
fun DatabaseProvider.toProviderCode(): String =
    when (this) {
        DatabaseProvider.Ms -> MS_PROVIDER_CODE
        DatabaseProvider.Pg -> PG_PROVIDER_CODE
    }
