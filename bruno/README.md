# CrmApiKotlinSpringBoot Bruno Collection

Git-native [Bruno](https://www.usebruno.com/) API collection for testing and verifying all REST and GraphQL endpoints in **CrmApiKotlinSpringBoot**.

---

## 🏛️ Architectural Highlights & Authoritative Sources

The testing approach using Bruno follows established continuous delivery and test architecture principles:

1. **Git-Native Test Artifacts in Source Control**:
   - Rather than storing API collections in external proprietary clouds or UI silos, `.bru` plain-text files live directly inside the repository alongside application source code, versioned with git branches and peer-reviewed in pull requests.
   - *References*:
     - Martin Fowler: [Continuous Integration](https://martinfowler.com/articles/continuousIntegration.html) — *"Keep all assets in a single source repository... everything you need to build and test the project should be version controlled."*
     - Bruno Documentation: [Bruno Git-Friendly Collections](https://docs.usebruno.com/).

2. **Automated API Testing in the Deployment Pipeline**:
   - Automated CLI test execution (`bru run`) integrates directly into CI/CD build scripts (`verify_all_pipelines.sh`), validating end-to-end functionality across both SQL Server and PostgreSQL.
   - *References*:
     - Martin Fowler: [DeploymentPipeline](https://martinfowler.com/bliki/DeploymentPipeline.html) — Automated gates progressing code from commit to production through verified stages.
     - Martin Fowler: [The Practical Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html) & [Microservice Testing](https://martinfowler.com/articles/microservice-testing/) — API and component testing validating external transport contracts.

3. **Multi-Environment Parity (ms vs pg)**:
   - Bruno environments (`Local-MsSql` vs `Local-Postgres`) validate provider transparency by running the identical test suite against both database backends using variable parameterization.
   - *References*:
     - The Twelve-Factor App: [Dev/Prod Parity](https://12factor.net/dev-prod-parity) — Keeping development, testing, and production environments as similar as possible.
     - Martin Fowler: [Branch By Abstraction](https://martinfowler.com/bliki/BranchByAbstraction.html) — Verifying equivalent behavior across swappable backends.

---

## 📁 Collection Structure

```
bruno/
├── bruno.json                               # Bruno collection manifest
├── environments/
│   ├── Local-MsSql.bru                      # SQL Server environment (provider: ms)
│   └── Local-Postgres.bru                   # PostgreSQL environment (provider: pg)
├── 00-Health/                               # Health checks
│   ├── General Health.bru                   # GET /health
│   ├── SQL Server Health.bru                # GET /health/ms
│   └── PostgreSQL Health.bru                # GET /health/pg
├── 01-Salesforce/                           # 15 entities (26 endpoints)
│   ├── 01-Accounts/                         # Paged list, ID lookup, child contacts & opps
│   ├── 02-Contacts/
│   ├── 03-Opportunities/                   # Paged list, ID lookup, line items
│   ├── 04-Leads/
│   ├── 05-Cases/
│   ├── 06-Users/
│   ├── 07-Campaigns/                        # Paged list, ID lookup, members
│   ├── 08-Tasks/
│   ├── 09-Events/
│   ├── 10-Products/
│   ├── 11-Pricebooks/
│   ├── 12-PricebookEntries/
│   ├── 13-OpportunityLineItems/
│   ├── 14-AccountContactRelations/
│   └── 15-CampaignMembers/
├── 02-HubSpot/                              # 11 entities (20 endpoints)
│   ├── 01-Companies/                        # Paged list, ID lookup, child contacts & deals
│   ├── 02-Contacts/
│   ├── 03-Deals/                            # Paged list, ID lookup, line items
│   ├── 04-Tickets/
│   ├── 05-Owners/
│   ├── 06-Products/
│   ├── 07-LineItems/
│   ├── 08-Pipelines/                        # Paged list, ID lookup, stages
│   ├── 09-DealStages/
│   ├── 10-Engagements/
│   └── 11-ObjectAssociations/
├── 03-Dynamics365/                          # 11 entities (19 endpoints)
│   ├── 01-Accounts/                         # Paged list, ID lookup, child contacts & opps
│   ├── 02-Contacts/
│   ├── 03-Opportunities/                   # Paged list, ID lookup, products
│   ├── 04-Leads/
│   ├── 05-Incidents/
│   ├── 06-Products/
│   ├── 07-PriceLevels/
│   ├── 08-OpportunityProducts/
│   ├── 09-BusinessUnits/
│   ├── 10-SystemUsers/
│   └── 11-ActivityPointers/
├── 04-GraphQL/                              # GraphQL operations
│   ├── Salesforce Accounts Query.bru
│   ├── HubSpot Companies Query.bru
│   └── Dynamics 365 Accounts Query.bru
└── 05-Negative-Tests/                       # Route validation & error handling
    ├── Invalid Provider Routing.bru         # Expects 404
    └── Nonexistent SF Account.bru           # Expects 404
```

---

## ⚙️ Environments & Variable Chaining

### Switching Database Providers
Select the active environment in Bruno:
- **`Local-MsSql`**: Points `provider` to `ms` (Microsoft SQL Server `CRMDatabaseMS`).
- **`Local-Postgres`**: Points `provider` to `pg` (PostgreSQL `CRMDatabasePG`).

### Dynamic Variable Extraction (Chaining)
Each entity list request (`Get <Entity> (Paged)`) executes a `post-response` script that extracts the first item's ID and updates runtime variables (e.g., `sf_account_id`, `hs_company_id`, `d365_account_id`). Subsequent detail and child-collection requests automatically consume these variables.

Default fallback IDs are pre-configured in each environment file so individual requests can also be run standalone.

---

## 🚀 Running the Collection

### 1. In Bruno Desktop GUI
1. Open Bruno.
2. Click **Open Collection** and select the `/bruno` folder.
3. In the top-right environment selector, choose **Local-MsSql** or **Local-Postgres**.
4. Run individual requests or right-click any folder to execute a runner.

### 2. Using Bruno CLI (`bru`)

Make sure your API is running:
```bash
./gradlew bootRun
```

Run all tests against **Microsoft SQL Server**:
```bash
bru run bruno -r --env Local-MsSql
```

Run all tests against **PostgreSQL**:
```bash
bru run bruno -r --env Local-Postgres
```

Run a specific CRM module:
```bash
# Run only Salesforce endpoints
bru run bruno/01-Salesforce -r --env Local-MsSql

# Run only HubSpot endpoints
bru run bruno/02-HubSpot -r --env Local-MsSql

# Run only Dynamics 365 endpoints
bru run bruno/03-Dynamics365 -r --env Local-MsSql

# Run GraphQL queries
bru run bruno/04-GraphQL -r --env Local-MsSql
```

Generate a JUnit or HTML test report for CI/CD:
```bash
bru run bruno -r --env Local-MsSql --output test-results.html --format html
```
