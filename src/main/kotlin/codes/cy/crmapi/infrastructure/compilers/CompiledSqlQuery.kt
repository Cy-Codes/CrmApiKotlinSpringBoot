package codes.cy.crmapi.infrastructure.compilers

/**
 * Immutable compiled SQL query result containing dialect-specific SQL and parameterized bindings.
 */
data class CompiledSqlQuery(
    val sql: String,
    val bindings: List<Any?>,
    val namedBindings: Map<String, Any?>
)
