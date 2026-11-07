package codes.cy.crmapi.infrastructure.compilers

/**
 * Immutable Command Object AST for composing dialect-independent, fully parameterized data-modification statements.
 *
 * Column order follows the insertion order of the supplied [Map]s; every value is bound as a parameter, never inlined.
 */
sealed interface SqlCommand {
    val table: String

    /**
     * `INSERT INTO table (columns...) VALUES (@p0, ...)`.
     */
    data class Insert(
        override val table: String,
        val values: Map<String, Any?>,
    ) : SqlCommand

    /**
     * `UPDATE table SET column = @p0, ... WHERE key = @pN`.
     */
    data class Update(
        override val table: String,
        val values: Map<String, Any?>,
        val whereClauses: List<WhereClause.EqualsClause>,
    ) : SqlCommand

    /**
     * `DELETE FROM table WHERE key = @p0`.
     */
    data class Delete(
        override val table: String,
        val whereClauses: List<WhereClause.EqualsClause>,
    ) : SqlCommand
}
