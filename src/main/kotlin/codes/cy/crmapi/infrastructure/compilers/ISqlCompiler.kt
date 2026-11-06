package codes.cy.crmapi.infrastructure.compilers

/**
 * Contract for compiling a dialect-agnostic [SqlQuery] AST into a parameterized [CompiledSqlQuery].
 */
interface ISqlCompiler {
    /**
     * Pure compilation of a [SqlQuery] into dialect-specific SQL and parameter bindings.
     */
    fun compile(query: SqlQuery): CompiledSqlQuery
}
