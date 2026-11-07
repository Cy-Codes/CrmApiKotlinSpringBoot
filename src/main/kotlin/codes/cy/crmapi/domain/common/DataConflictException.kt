package codes.cy.crmapi.domain.common

/**
 * Raised when a write violates a relational integrity rule (foreign key, unique or primary key constraint).
 * Translated to `409 Conflict` at the API boundary; carries no database-internal detail.
 */
class DataConflictException(
    message: String = DEFAULT_MESSAGE,
    cause: Throwable? = null,
) : RuntimeException(message, cause) {
    companion object {
        const val DEFAULT_MESSAGE: String = "The operation conflicts with existing data (foreign key or unique constraint violation)."
    }
}
