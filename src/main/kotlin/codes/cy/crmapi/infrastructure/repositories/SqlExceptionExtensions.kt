package codes.cy.crmapi.infrastructure.repositories

import java.sql.SQLException

private const val PG_SQLSTATE_FOREIGN_KEY_VIOLATION: String = "23503"
private const val PG_SQLSTATE_UNIQUE_VIOLATION: String = "23505"
private const val MS_ERROR_FOREIGN_KEY_VIOLATION: Int = 547
private const val MS_ERROR_UNIQUE_CONSTRAINT_VIOLATION: Int = 2627
private const val MS_ERROR_UNIQUE_INDEX_VIOLATION: Int = 2601

private val CONSTRAINT_SQLSTATES = setOf(PG_SQLSTATE_FOREIGN_KEY_VIOLATION, PG_SQLSTATE_UNIQUE_VIOLATION)
private val CONSTRAINT_MS_ERROR_CODES = setOf(
    MS_ERROR_FOREIGN_KEY_VIOLATION,
    MS_ERROR_UNIQUE_CONSTRAINT_VIOLATION,
    MS_ERROR_UNIQUE_INDEX_VIOLATION,
)

/**
 * Pure check whether this [SQLException] (or any exception in its cause chain) is a foreign key or unique/primary key violation
 * on PostgreSQL (SQLSTATE 23503/23505) or SQL Server (error 547/2627/2601).
 */
fun Throwable.isConstraintViolation(): Boolean =
    generateSequence(this) { it.cause }
        .filterIsInstance<SQLException>()
        .any { it.sqlState in CONSTRAINT_SQLSTATES || it.errorCode in CONSTRAINT_MS_ERROR_CODES }
