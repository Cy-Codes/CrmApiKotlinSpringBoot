package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.infrastructure.compilers.CompiledSqlQuery
import java.math.BigDecimal
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

/**
 * Lightweight, reflection-based JDBC Data Mapper for immutable Kotlin data classes.
 * Eliminates ORM/JPA tracking overhead while keeping Domain models 100% persistence-ignorant.
 */
object JdbcDataMapper {
    private val NAMED_PARAM_REGEX = Regex("@p\\d+")

    /**
     * Pure transformation converting `@p0`, `@p1` placeholders into positional `?` JDBC placeholders.
     */
    fun toJdbcSql(sql: String): String =
        NAMED_PARAM_REGEX.replace(sql, "?")

    /**
     * Side-effecting JDBC helper: prepares a parameterized [PreparedStatement] from a [CompiledSqlQuery].
     */
    fun prepareStatement(connection: Connection, compiledQuery: CompiledSqlQuery): PreparedStatement {
        val jdbcSql = toJdbcSql(compiledQuery.sql)
        val statement = connection.prepareStatement(jdbcSql)
        compiledQuery.bindings.forEachIndexed { index, value ->
            bindParameter(statement, index + 1, value)
        }
        return statement
    }

    /**
     * Side-effecting JDBC helper: executes a scalar integer query (such as `COUNT(*)`).
     */
    fun queryScalarInt(connection: Connection, compiledQuery: CompiledSqlQuery): Int =
        prepareStatement(connection, compiledQuery).use { statement ->
            statement.executeQuery().use { rs ->
                if (rs.next()) rs.getInt(1) else 0
            }
        }

    /**
     * Side-effecting JDBC helper: executes a query and maps all rows to an immutable [List] of [T].
     */
    fun <T : Any> queryList(
        connection: Connection,
        compiledQuery: CompiledSqlQuery,
        klass: KClass<T>,
    ): List<T> =
        prepareStatement(connection, compiledQuery).use { statement ->
            statement.executeQuery().use { rs ->
                val columnLookup = buildColumnLookup(rs)
                generateSequence { if (rs.next()) mapRow(rs, columnLookup, klass) else null }.toList()
            }
        }

    /**
     * Side-effecting JDBC helper: executes a query and maps the first row to [T] or `null`.
     */
    fun <T : Any> queryFirstOrNull(
        connection: Connection,
        compiledQuery: CompiledSqlQuery,
        klass: KClass<T>,
    ): T? =
        prepareStatement(connection, compiledQuery).use { statement ->
            statement.executeQuery().use { rs ->
                if (!rs.next()) {
                    null
                } else {
                    val columnLookup = buildColumnLookup(rs)
                    mapRow(rs, columnLookup, klass)
                }
            }
        }

    private fun bindParameter(statement: PreparedStatement, parameterIndex: Int, value: Any?) {
        when (value) {
            null -> statement.setObject(parameterIndex, null)
            is String -> statement.setString(parameterIndex, value)
            is Boolean -> statement.setBoolean(parameterIndex, value)
            is Int -> statement.setInt(parameterIndex, value)
            is Long -> statement.setLong(parameterIndex, value)
            is BigDecimal -> statement.setBigDecimal(parameterIndex, value)
            is LocalDateTime -> statement.setTimestamp(parameterIndex, Timestamp.valueOf(value))
            is UUID -> statement.setObject(parameterIndex, value)
            else -> statement.setObject(parameterIndex, value)
        }
    }

    private fun buildColumnLookup(rs: ResultSet): Map<String, Int> {
        val metaData = rs.metaData
        return (1..metaData.columnCount).associate { columnIndex ->
            metaData.getColumnLabel(columnIndex).lowercase() to columnIndex
        }
    }

    private fun <T : Any> mapRow(
        rs: ResultSet,
        columnLookup: Map<String, Int>,
        klass: KClass<T>,
    ): T {
        val constructor = requireNotNull(klass.primaryConstructor) {
            "Class ${klass.qualifiedName} must have a primary constructor."
        }

        val args = constructor.parameters
            .mapNotNull { param ->
                val paramName = param.name?.lowercase()
                val columnIndex = paramName?.let { columnLookup[it] }
                if (columnIndex == null) {
                    if (param.isOptional) null else param to null
                } else {
                    param to extractColumnValue(rs, columnIndex, param)
                }
            }
            .toMap()

        return constructor.callBy(args)
    }

    private fun extractColumnValue(
        rs: ResultSet,
        columnIndex: Int,
        param: KParameter,
    ): Any? {
        val rawValue = rs.getObject(columnIndex) ?: return null
        return when (param.type.classifier) {
            String::class -> rawValue.toString()
            Boolean::class -> when (rawValue) {
                is Boolean -> rawValue
                is Number -> rawValue.toInt() != 0
                else -> rawValue.toString().toBoolean()
            }
            Int::class -> (rawValue as? Number)?.toInt() ?: rawValue.toString().toInt()
            Long::class -> (rawValue as? Number)?.toLong() ?: rawValue.toString().toLong()
            BigDecimal::class -> when (rawValue) {
                is BigDecimal -> rawValue
                is Number -> BigDecimal.valueOf(rawValue.toDouble())
                else -> BigDecimal(rawValue.toString())
            }
            LocalDateTime::class -> when (rawValue) {
                is LocalDateTime -> rawValue
                is Timestamp -> rawValue.toLocalDateTime()
                else -> LocalDateTime.parse(rawValue.toString())
            }
            UUID::class -> when (rawValue) {
                is UUID -> rawValue
                else -> UUID.fromString(rawValue.toString())
            }
            else -> rawValue
        }
    }
}
