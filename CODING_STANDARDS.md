# CrmApiKotlinSpringBoot Coding Standards & Best Practices

This document is the complete, self-contained specification of both the **Universal Coding Standards** and the **Kotlin 2.x, Spring Boot, Lightweight JDBC Data Mapper, Multi-Dialect SQL Compiler, GraphQL, JUnit 5, and Bruno API Testing Standards** for the **CrmApiKotlinSpringBoot** repository.

---

# Part I: Universal Coding Standards

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Universal Standards Pillars                     │
├───────────────────┬───────────────────┬────────────────────────────────┤
│  Functional First │ Immutable Always  │ Explicit Side Effects          │
│  Maps over Loops  │ val & data class  │ Command/Query Segregation     │
├───────────────────┴──────┬────────────┴────────────────────────────────┤
│  Helper & Extension Fns  │ Zero Magic Numbers & Anti-Pattern Tolerance │
└──────────────────────────┴─────────────────────────────────────────────┘
```

## 1. Core Universal Principles

1. **Functional Logic Over Imperative Loops**:
   - Choose declarative transformations (`map`, `filter`, `fold`, `associate`, `flatMap`, `generateSequence`) over state-mutating imperative loops (`for`, `while`, `do..while`).
   - Do not allocate a `mutableListOf()` or `mutableMapOf()`, loop over input, and imperatively append elements (`.add()` / `put()`).
   - **Acceptable Exceptions for Loops**: Streaming unbuffered database/network cursors (`ResultSet.next()` wrapped in `generateSequence`), benchmark-proven hot paths where closure allocations cause measured GC pressure, or early-exit state machines. Every exception loop must include an inline comment explaining why a functional construct was rejected.
2. **Immutability by Default**:
   - All domain entities, DTOs, and query AST nodes must be deeply immutable once initialized (`data class` with `val` properties, `List<T>`, `Map<K, V>`, `Set<T>`).
   - Pure functions must never mutate input parameters.
3. **Explicit Demarcation of Side Effects (Functional Core, Imperative Shell)**:
   - **Queries (Pure / Read-Only)**: Idempotent, side-effect free (`get*`, `find*`, `parse*`, `calculate*`, `to*`, `apply*`).
   - **Commands (Side-Effecting)**: Explicitly mutate state or perform I/O (`execute*`, `open*`, `create*`, `update*`, `delete*`, `save*`, `write*`).
   - Isolate database JDBC I/O and HTTP exception handling to explicit outer boundaries.
4. **Zero Magic Numbers & String Literals**:
   - Ban raw numeric or string literals in code (`404`, `503`, `20`, `100`, `"asc"`, `"ms"`).
   - Replace with `HttpStatus.*`, `QueryOptions.DEFAULT_PAGE_SIZE`, `DatabaseProvider` enums, and `const val` constants.
5. **Eliminate Redundancy via Helper & Extension Functions**:
   - Consolidate repeated boilerplate into reusable pure helper functions, preferring top-level **Kotlin extension functions** (`fun Receiver.action(...)`) where supported.

---

# Part II: Kotlin 2.x / Spring Boot & API Specific Standards

## 2. Functional-First Programming in Kotlin

### 2.1 Maps, Filters, and Folds Over Loops
Use Kotlin collection and sequence pipelines instead of imperative `for` loops:

```kotlin
// ❌ ANTI-PATTERN: Imperative loop with mutable accumulator
val filters = mutableMapOf<String, String?>()
for ((key, value) in allParams) {
    if (RESERVED_QUERY_PARAMETER_NAMES.contains(key.lowercase())) continue
    filters[key] = value
}

// ✅ BEST PRACTICE: Declarative functional pipeline in ControllerResultExtensions.kt
val filters = this.entries
    .filter { (key, _) -> !RESERVED_QUERY_PARAMETER_NAMES.contains(key.lowercase()) }
    .associate { (key, value) -> key to (value as String?) }
```

### 2.2 `when` Expressions Over Statement Cascades
Favor exhaustive `when` expressions and expression-body functions over nested `if / else` blocks:

```kotlin
// ✅ BEST PRACTICE: Concise when expression in DatabaseProviderExtensions.kt
fun String?.toDatabaseProvider(): DatabaseProvider =
    when (this?.lowercase()) {
        MS_PROVIDER_CODE -> DatabaseProvider.Ms
        PG_PROVIDER_CODE -> DatabaseProvider.Pg
        else -> throw IllegalArgumentException(
            "Invalid provider '$this'. Must be '$MS_PROVIDER_CODE' or '$PG_PROVIDER_CODE'.",
        )
    }
```

---

## 3. Deep Immutability & Stateless Services

### 3.1 Domain Models, Query ASTs, and JetBrains `kotlinx.serialization`
- All domain entities (`Account`, `Company`, `Deal`, etc.), value objects (`QueryOptions`, `PagedResult<T>`), and query AST types (`SqlQuery`, `CompiledSqlQuery`) must be defined as `data class` using `val` properties only.
- Mutable `var` properties are strictly forbidden on domain models, DTOs, and services.
- Prefer JetBrains native tooling over third-party Java reflection libraries: all serializable domain models and DTOs use **JetBrains `kotlinx.serialization`** (`@Serializable` with `org.jetbrains.kotlin.plugin.serialization` and `org.jetbrains.kotlinx:kotlinx-serialization-json`), which generates type-safe serializers at compile time and natively preserves `is*` boolean property names (`isDeleted`, `isClosed`, `isWon`, `isActive`, `isDisabled`) without JavaBean getter name mangling.
- Standard JVM types (`UUID`, `BigDecimal`, `LocalDateTime`) use shared, stateless `KSerializer` objects defined in [`KotlinxSerializers.kt`](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/src/main/kotlin/codes/cy/crmapi/domain/common/KotlinxSerializers.kt) and registered via `@file:UseSerializers(UuidSerializer::class, BigDecimalSerializer::class, LocalDateTimeSerializer::class)`.

### 3.2 Read-Only Collections
- Expose Kotlin's read-only `List<T>`, `Map<K, V>`, and `Set<T>` interfaces across all domain models, repositories, services, and controllers.
- Never return `MutableList<T>` or `MutableMap<K, V>`.

### 3.3 Final Stateless Services
- In Kotlin, classes are `final` (`sealed` against arbitrary subclassing) by default. Keep concrete controllers, services, repositories, and factories `final` with `private val` constructor-injected dependencies.

---

## 4. Explicit Demarcation of Side Effects

### 4.1 Command-Query Segregation (CQS)

| Category | Characteristics | Naming Convention | Return Type |
| :--- | :--- | :--- | :--- |
| **Query (Pure / Read-Only)** | Idempotent, safe, no state mutation, no DB writes | `get*`, `find*`, `parse*`, `to*`, `apply*`, `compile` | `T?`, `List<T>`, `PagedResult<T>`, `SqlQuery`, `CompiledSqlQuery` |
| **Command (Side-Effecting)** | Opens JDBC connections, executes SQL I/O, logs errors | `execute*`, `openConnection`, `prepareStatement` | `Connection`, `PreparedStatement`, `ResponseEntity<*>` |

### 4.2 Isolating Database & HTTP I/O Boundaries
- In [`BaseRepository.kt`](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/src/main/kotlin/codes/cy/crmapi/infrastructure/repositories/BaseRepository.kt), query construction (`applyFilters`, `applySorting`, `applyPagination`) and SQL compilation (`compiler.compile(...)`) are 100% pure, while JDBC connection lifecycle I/O is isolated inside `executeWithOpenConnection` and `use { ... }`.
- In [`ExceptionHandlingAdvice.kt`](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/src/main/kotlin/codes/cy/crmapi/api/middleware/ExceptionHandlingAdvice.kt), RFC 7807 / RFC 9457 payload construction (`createProblemDetails`) is a pure companion function, while logging and HTTP response wrapping occur in the `@ExceptionHandler` boundary.

---

## 5. Elimination of Magic Numbers & String Literals

### 5.1 Query Pagination Constants
All pagination and sorting defaults are centralized on [`QueryOptions`](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/src/main/kotlin/codes/cy/crmapi/domain/common/QueryOptions.kt):

```kotlin
data class QueryOptions(
    val page: Int = DEFAULT_PAGE,
    val pageSize: Int = DEFAULT_PAGE_SIZE,
    val sortBy: String? = null,
    val sortDirection: String = DEFAULT_SORT_DIRECTION,
    val filters: Map<String, String?>? = null,
) {
    val normalizedPage: Int
        get() = if (page < DEFAULT_PAGE) DEFAULT_PAGE else page

    val normalizedPageSize: Int
        get() = when {
            pageSize < MIN_PAGE_SIZE -> DEFAULT_PAGE_SIZE
            pageSize > MAX_PAGE_SIZE -> MAX_PAGE_SIZE
            else -> pageSize
        }

    companion object {
        const val DEFAULT_PAGE: Int = 1
        const val MIN_PAGE_SIZE: Int = 1
        const val DEFAULT_PAGE_SIZE: Int = 20
        const val MAX_PAGE_SIZE: Int = 100
        const val DEFAULT_SORT_DIRECTION: String = "asc"
        const val DESCENDING_SORT_DIRECTION: String = "desc"
        const val DEFAULT_ID_COLUMN: String = "Id"

        val Default: QueryOptions = QueryOptions()
    }
}
```

### 5.2 HTTP Status Codes
Always use `HttpStatus.OK`, `HttpStatus.BAD_REQUEST`, `HttpStatus.NOT_FOUND`, `HttpStatus.INTERNAL_SERVER_ERROR`, and `HttpStatus.SERVICE_UNAVAILABLE` rather than raw integer literals.

---

## 6. Eliminate Redundancy via Extension & Helper Functions

Whenever logic is repeated across controllers, query builders, or tests, extract it into a pure top-level **extension function**:
- **[`DatabaseProviderExtensions.kt`](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/src/main/kotlin/codes/cy/crmapi/domain/common/DatabaseProviderExtensions.kt)**: `provider.toDatabaseProvider()` and `provider.toProviderCode()`.
- **[`ControllerResultExtensions.kt`](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/src/main/kotlin/codes/cy/crmapi/api/extensions/ControllerResultExtensions.kt)**: `allParams.toQueryOptions(...)`, `entity.toResponseEntity()`, and `result.toOkResponseEntity()`.
- **[`QueryFilteringExtensions.kt`](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/src/main/kotlin/codes/cy/crmapi/infrastructure/repositories/QueryFilteringExtensions.kt)**: `query.applyFilters(filters).applySorting(sortBy, sortDirection, defaultSortColumn).applyPagination(page, pageSize)`, `identifier.isSafeSqlIdentifier()`, and `rawValue.parseFilterValue()`.

---

## 7. Anti-Patterns & Common Mistakes Catalog for Kotlin / Spring Boot

1. **JavaBean Reflection Serializers or Omitting `encodeDefaults = true` in `kotlinx.serialization`**:
   - *Violation*: Using JavaBean-based reflection serializers that strip `is*` prefixes from Kotlin boolean properties (`isDeleted` -> `deleted`), or leaving `encodeDefaults = false` in `kotlinx.serialization.json.Json` so default-valued properties are omitted from API responses.
   - *Remedy*: Always use **JetBrains `kotlinx.serialization`** (`@Serializable`) configured in [`KotlinSerializationConfig.kt`](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/src/main/kotlin/codes/cy/crmapi/api/config/KotlinSerializationConfig.kt) with `Json { encodeDefaults = true; ignoreUnknownKeys = true; explicitNulls = true }`.
2. **Mutable Properties (`var`) or Field Injection (`@Autowired lateinit var`) in Production Beans**:
   - *Violation*: Using `@Autowired lateinit var` field injection or mutable `var` state in controllers, services, or repositories.
   - *Remedy*: Always use primary constructor injection with `private val`.
3. **Unclosed JDBC Resources (`Connection`, `PreparedStatement`, `ResultSet`)**:
   - *Violation*: Opening a JDBC `Connection` or `PreparedStatement` without deterministic disposal.
   - *Remedy*: Always wrap `AutoCloseable` JDBC resources in Kotlin's `.use { ... }` block.
4. **SQL String Interpolation (Injection Vulnerability)**:
   - *Violation*: Using Kotlin string templates (`"SELECT * FROM $table WHERE $col = '$val'"`) with unvalidated user input.
   - *Remedy*: Validate identifiers via `isSafeSqlIdentifier()`, construct queries with `SqlQuery`, and bind parameters via JDBC `PreparedStatement`.
5. **Catching `NoResourceFoundException` as `500 Internal Server Error`**:
   - *Violation*: Having a catch-all `@ExceptionHandler(Exception::class)` map Spring's `NoResourceFoundException` or `NoHandlerFoundException` to HTTP 500 instead of HTTP 404.
   - *Remedy*: Explicitly classify `NoResourceFoundException` and `NoHandlerFoundException` as `HttpStatus.NOT_FOUND` (404) in `ExceptionHandlingAdvice.createProblemDetails`.
6. **500 Internal Error Information Disclosure**:
   - *Violation*: Returning raw exception messages or stack traces on `500 Internal Server Error` in non-development profiles.
   - *Remedy*: Mask `500` details with `GENERIC_INTERNAL_ERROR_DETAIL` unless an active profile is `dev` or `development`.

---

## 8. Automated API Testing (Bruno) & Pre-Commit Formatting

### 8.1 Bruno API Test Suite (`bruno/`)
- Partitioned by CRM domain (`00-Health`, `01-Salesforce`, `02-HubSpot`, `03-Dynamics365`, `04-GraphQL`, `05-Negative-Tests`).
- Uses environment variables (`{{baseUrl}}`, `{{provider}}`), never hardcoded URLs.

### 8.2 Pre-Commit Auto-Formatting & Unused Import Removal
- `.editorconfig` enforces standard 4-space Kotlin indentation, UTF-8 encoding, final newline, and trailing whitespace trimming.
- On every `git commit`, the pre-commit hook automatically cleans unused imports, sorts remaining imports alphabetically, formats staged `.kt` and `.kts` files, and re-stages the files.
