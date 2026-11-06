# CrmApiKotlinSpringBoot - Multi-Provider CRM Web API (Kotlin 2.x / Spring Boot)

A high-performance Kotlin 2.x + Spring Boot Web API serving relational data from two enterprise CRM database engines:
- **Microsoft SQL Server** (`CRMDatabaseMS`)
- **PostgreSQL** (`CRMDatabasePG`)

Covering **37 tables** across three CRM schemas with 100% functional parity with [CrmApiCSharp](file:///Users/cy/codes/localstack-reference-architecture/CrmApiCSharp/README.md):
- **Salesforce (15 entities)**: `Account`, `Contact`, `Opportunity`, `Lead`, `Campaign`, `Case`, `User`, `Event`, `Task`, `Product2`, `Pricebook2`, `PricebookEntry`, `OpportunityLineItem`, `AccountContactRelation`, `CampaignMember`.
- **HubSpot (11 entities)**: `Company`, `Contact`, `Deal`, `Ticket`, `Owner`, `Product`, `LineItem`, `Pipeline`, `DealStage`, `Engagement`, `ObjectAssociation`.
- **Dynamics 365 (11 entities)**: `Account`, `Contact`, `Opportunity`, `Lead`, `Incident`, `Product`, `PriceLevel`, `OpportunityProduct`, `BusinessUnit`, `SystemUser`, `ActivityPointer`.

Detailed Architecture Decision Records (ADRs) are documented in [docs/architecture-decisions.md](file:///Users/cy/codes/localstack-reference-architecture/docs/architecture-decisions.md). For engineering standards, refer to the [CrmApiKotlinSpringBoot Coding Standards](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/CODING_STANDARDS.md) and repository-wide [Coding Standards](file:///Users/cy/codes/localstack-reference-architecture/CODING_STANDARDS.md).

---

## Architectural Highlights & Authoritative Sources

Every technical design choice in **CrmApiKotlinSpringBoot** is grounded in established enterprise patterns, official specifications, and literature from Martin Fowler, JetBrains Kotlin, and Spring Architecture:

### 1. High-Throughput Lightweight JDBC Data Mapper (`JdbcDataMapper`)
- **Design Choice**: Forego heavyweight JPA/Hibernate ORMs and map JDBC `ResultSet` streams directly into immutable Kotlin `data class` primary constructors via `JdbcDataMapper` for zero dirty-checking overhead and raw query speed.
- **Authoritative References**:
  - Martin Fowler: [OrmHate](https://martinfowler.com/bliki/OrmHate.html) — Navigating the Object-Relational Impedance Mismatch and avoiding unnecessary ORM state tracking when reading relational data.
  - Martin Fowler: [Data Mapper Pattern](https://martinfowler.com/eaaCatalog/dataMapper.html) (*Patterns of Enterprise Application Architecture*) — Decoupling domain records from database schemas.
  - Pramod Sadalage & Martin Fowler: [Evolutionary Database Design](https://martinfowler.com/articles/evodb.html) — Separating database schema lifecycles from application ORM generation.

### 2. Multi-Dialect Immutable Query AST (`SqlQuery` & `ISqlCompiler`)
- **Design Choice**: Immutable programmatic query AST (`SqlQuery`) compiled into dialect-specific SQL (`SqlServerCompiler` for T-SQL bracketed identifiers `[Schema].[Table]` and `OFFSET...FETCH NEXT` vs. `PostgresCompiler` for ANSI double quotes `"Schema"."Table"` and `LIMIT...OFFSET`).
- **Authoritative References**:
  - Martin Fowler: [Query Object Pattern](https://martinfowler.com/eaaCatalog/queryObject.html) (*P of EAA*) — Representing database queries as structured object graphs independent of SQL syntax.
  - OWASP Foundation: [SQL Injection Prevention Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/SQL_Injection_Prevention_Cheat_Sheet.html) — Identifier validation with regex (`isSafeSqlIdentifier()`) and parameterized JDBC `PreparedStatement` bindings.

### 3. Dynamic Multi-Provider Routing (`/api/{provider}/v1/...`)
- **Design Choice**: Single endpoint surface allowing clients to target Microsoft SQL Server (`ms`) or PostgreSQL (`pg`) at runtime via regex-constrained path parameters (`/api/{provider:^(?:ms|pg)$}/v1/...`).
- **Authoritative References**:
  - Martin Fowler: [Branch By Abstraction](https://martinfowler.com/bliki/BranchByAbstraction.html) — Running dual implementations concurrently to allow risk-free migration, testing, and side-by-side benchmarking.
  - Martin Fowler: [Strangler Fig Application](https://martinfowler.com/bliki/StranglerFigApplication.html) — Transparent routing behind a facade.
  - Martin Fowler: [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html) — Standard HTTP verbs and resource URIs.
  - Spring Framework Documentation: [Spring Web MVC URI Pattern Matching](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-requestmapping.html).

### 4. Clean Architecture Layering & Inversion of Control
- **Design Choice**: Strict 4-layer package separation (`domain`, `infrastructure`, `application`, `api`) with inward-pointing dependencies, enabling transport independence where REST controllers and GraphQL resolvers share identical application query services.
- **Authoritative References**:
  - Martin Fowler: [PresentationDomainDataLayering](https://martinfowler.com/bliki/PresentationDomainDataLayering.html) — Modularizing applications into presentation, domain, and data source tiers.
  - Martin Fowler: [Inversion of Control Containers and the Dependency Injection pattern](https://martinfowler.com/articles/injection.html) — Inverting runtime dependencies via Spring constructor DI.
  - Martin Fowler: [Separated Interface](https://martinfowler.com/eaaCatalog/separatedInterface.html) — Defining query service interfaces separately from implementations.
  - Robert C. Martin: [The Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html).

### 5. Unified Multi-CRM GraphQL Endpoint (`POST /graphql`)
- **Design Choice**: GraphQL query execution supporting all three CRM domains (`salesforceAccounts`, `salesforceAccountById`, `salesforceContacts`, `salesforceContactById`, `hubSpotCompanies`, `hubSpotCompanyById`, `hubSpotContacts`, `dynamics365Accounts`, `dynamics365AccountById`, `dynamics365Contacts`) with field selection projection.
- **Authoritative References**:
  - GraphQL Foundation: [Official GraphQL Specification](https://spec.graphql.org/).
  - Martin Fowler: [Data Transfer Object (DTO)](https://martinfowler.com/eaaCatalog/dataTransferObject.html) — Client-driven field selection minimizing payload size.

### 6. Standardized Error Handling via RFC 7807 / RFC 9457 Problem Details
- **Design Choice**: Centralized `@RestControllerAdvice` (`ExceptionHandlingAdvice`) returning RFC 7807 / RFC 9457 compliant `application/problem+json` (`ProblemDetail`) envelopes for client predictability and internal stack-trace sanitization.
- **Authoritative References**:
  - IETF: [RFC 7807: Problem Details for HTTP APIs](https://datatracker.ietf.org/doc/html/rfc7807) & [RFC 9457 (Updated RFC)](https://datatracker.ietf.org/doc/html/rfc9457).
  - Spring Framework Documentation: [RFC 9457 ProblemDetail in Spring Web MVC](https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-ann-rest-exceptions.html).
  - Martin Fowler: [Notification Pattern](https://martinfowler.com/articles/replaceThrowWithNotification.html) — Encapsulating errors for clean consumer communication.

### 7. Stateless Value Objects & JetBrains `kotlinx.serialization`
- **Design Choice**: Immutable `@Serializable` `QueryOptions`, `PagedResult<T>`, and CRM domain `data class`es serialized at compile time via **JetBrains `kotlinx.serialization`** (`org.jetbrains.kotlin.plugin.serialization` + `org.jetbrains.kotlinx:kotlinx-serialization-json`) and Spring's `KotlinSerializationJsonHttpMessageConverter`, avoiding JavaBean reflection overhead and natively preserving Kotlin `is*` boolean property names (`isDeleted`, `isClosed`, `isWon`, `isActive`).
- **Authoritative References**:
  - Martin Fowler: [Value Object](https://martinfowler.com/bliki/ValueObject.html) — Immutable domain records characterized by values rather than identity.
  - JetBrains: [Kotlin Data Classes & Immutability Conventions](https://kotlinlang.org/docs/coding-conventions.html).
  - JetBrains: [Kotlin Serialization Guide (`kotlinx.serialization`)](https://github.com/Kotlin/kotlinx.serialization/blob/master/docs/serialization-guide.md).

### 8. Application Database Encapsulation & Domain Ownership
- **Design Choice**: Business logic, multi-entity validation, and test automation reside exclusively in the API layer, treating the database as an encapsulated Application Database and reserving database CPU for indexed data access and structural constraints.
- **Authoritative References**:
  - Martin Fowler: [Application Database](https://martinfowler.com/bliki/ApplicationDatabase.html) & [Integration Database](https://martinfowler.com/bliki/IntegrationDatabase.html).
  - Martin Fowler: [Domain Logic and SQL](https://martinfowler.com/articles/dblogic.html).
  - Martin Fowler: [The Practical Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html) & [Microservice Testing](https://martinfowler.com/articles/microservice-testing/).
  - The Twelve-Factor App: [Stateless Processes & Backing Services](https://12factor.net/).

---

## REST Route Conventions

All collection routes support pagination, sorting, and field-level filtering:
```http
GET /api/{provider}/v1/{crm}/{resource}?page=1&pageSize=20&sortBy={col}&sortDirection={asc|desc}&{filterKey}={filterVal}
```

Where:
- `{provider}`: `ms` (SQL Server) or `pg` (PostgreSQL)
- `{crm}`: `salesforce`, `hubspot`, `dynamics365`
- `{resource}`: `accounts`, `contacts`, `opportunities`, `companies`, `deals`, `leads`, etc.

### Example Queries
- `GET /api/ms/v1/salesforce/accounts?page=1&pageSize=10`
- `GET /api/pg/v1/salesforce/accounts/001000000000001AAA`
- `GET /api/ms/v1/salesforce/accounts/001000000000001AAA/contacts`
- `GET /api/pg/v1/hubspot/companies?domain=example.com`
- `GET /api/ms/v1/dynamics365/opportunities?statecode=0`

---

## Project Layout

```
CrmApiKotlinSpringBoot/
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── CODING_STANDARDS.md
├── bruno/                                 # Git-native API test collection & environments
├── src/
│   ├── main/
│   │   ├── kotlin/codes/cy/crmapi/
│   │   │   ├── domain/                    # Pure immutable data classes & value objects (PagedResult, QueryOptions)
│   │   │   ├── infrastructure/            # DbConnectionFactory, SqlCompilerFactory, JdbcDataMapper, Repositories
│   │   │   ├── application/               # Query service interfaces & implementations
│   │   │   └── api/                       # Spring Boot REST controllers, GraphQL resolver, ExceptionHandlingAdvice
│   │   └── resources/
│   │       └── application.yml            # Port 5172 & multi-DB connection strings
│   └── test/
│       └── kotlin/codes/cy/crmapi/
│           ├── infrastructure/            # SqlCompilerTests (multi-dialect SQL & filter compilation tests)
│           └── api/                       # Spring Boot MockMvc REST & GraphQL integration tests
```

---

## Getting Started

### Prerequisites
- JDK 21+ (or JDK 25)
- Microsoft SQL Server (`CRMDatabaseMS`) and/or PostgreSQL (`CRMDatabasePG`) instances.
- [Bruno CLI or GUI](https://www.usebruno.com/) (optional, for running the API collection).

### Build and Run
```bash
cd CrmApiKotlinSpringBoot
./gradlew test
./gradlew bootRun
```
Visit Swagger UI at `http://localhost:5172/swagger` or execute GraphQL queries against `POST http://localhost:5172/graphql`.

### Testing Endpoints with Bruno
Run the automated Bruno test suite against your active database provider:
```bash
# Against SQL Server
bru run bruno -r --env Local-MsSql

# Against PostgreSQL
bru run bruno -r --env Local-Postgres
```
See [bruno/README.md](file:///Users/cy/codes/localstack-reference-architecture/CrmApiKotlinSpringBoot/bruno/README.md) for full collection documentation.
