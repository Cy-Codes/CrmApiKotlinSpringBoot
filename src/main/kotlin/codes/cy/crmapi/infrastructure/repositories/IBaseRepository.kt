package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.infrastructure.compilers.SqlQuery
import kotlin.reflect.KClass

/**
 * Generic read-only repository contract utilizing multi-dialect SQL compilation and JDBC mapping.
 */
interface IBaseRepository {
    fun <T : Any> getPaged(
        provider: DatabaseProvider,
        table: String,
        options: QueryOptions,
        klass: KClass<T>,
        defaultSortColumn: String = QueryOptions.DEFAULT_ID_COLUMN,
        configureQuery: ((SqlQuery) -> SqlQuery)? = null,
    ): PagedResult<T>

    fun <T : Any> getById(
        provider: DatabaseProvider,
        table: String,
        idColumnName: String,
        idValue: Any,
        klass: KClass<T>,
    ): T?

    fun <T : Any> getList(
        provider: DatabaseProvider,
        table: String,
        klass: KClass<T>,
        configureQuery: ((SqlQuery) -> SqlQuery)? = null,
    ): List<T>

    /**
     * Command: inserts a row and returns it freshly re-read by id, within a single transaction.
     * Constraint violations surface as [codes.cy.crmapi.domain.common.DataConflictException].
     */
    fun <T : Any> executeCreate(
        provider: DatabaseProvider,
        table: String,
        idColumnName: String,
        idValue: Any,
        values: Map<String, Any?>,
        klass: KClass<T>,
    ): T

    /**
     * Command: updates the given columns of the row with the given id and returns it freshly re-read, within a single transaction.
     * Returns `null` when no row has that id.
     */
    fun <T : Any> executeUpdate(
        provider: DatabaseProvider,
        table: String,
        idColumnName: String,
        idValue: Any,
        values: Map<String, Any?>,
        klass: KClass<T>,
    ): T?

    /**
     * Command: hard-deletes the row with the given id. Returns `false` when no row has that id.
     */
    fun executeDelete(
        provider: DatabaseProvider,
        table: String,
        idColumnName: String,
        idValue: Any,
    ): Boolean
}
