package codes.cy.crmapi.api

import codes.cy.crmapi.application.interfaces.IDynamics365CommandService
import codes.cy.crmapi.application.interfaces.IDynamics365QueryService
import codes.cy.crmapi.application.interfaces.IHubSpotCommandService
import codes.cy.crmapi.application.interfaces.IHubSpotQueryService
import codes.cy.crmapi.application.interfaces.ISalesforceCommandService
import codes.cy.crmapi.application.interfaces.ISalesforceQueryService
import codes.cy.crmapi.domain.common.DataConflictException
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.dynamics365.Account as D365Account
import codes.cy.crmapi.domain.dynamics365.AccountWriteRequest as D365AccountWriteRequest
import codes.cy.crmapi.domain.hubspot.Company
import codes.cy.crmapi.domain.hubspot.CompanyWriteRequest
import codes.cy.crmapi.domain.salesforce.Account as SfAccount
import codes.cy.crmapi.domain.salesforce.AccountWriteRequest as SfAccountWriteRequest
import codes.cy.crmapi.domain.salesforce.Contact as SfContact
import java.lang.reflect.Proxy
import java.util.UUID
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * Shared test fixture configuration providing pre-configured dynamic proxy query services for API integration tests.
 */
@TestConfiguration
class TestMockConfiguration {
    companion object {
        const val SAMPLE_SALESFORCE_ACCOUNT_ID = "001000000000001AAA"
        const val SAMPLE_SALESFORCE_CONTACT_ID = "003000000000001AAA"
        const val SAMPLE_HUBSPOT_COMPANY_ID = 101L
        val SAMPLE_DYNAMICS_ACCOUNT_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")

        /** Ids returned by the mocked command services for newly created entities. */
        const val CREATED_SALESFORCE_ACCOUNT_ID = "001000000000099AAA"
        const val CREATED_HUBSPOT_COMPANY_ID = 909L
        val CREATED_DYNAMICS_ACCOUNT_ID: UUID = UUID.fromString("99999999-9999-9999-9999-999999999999")

        /** A write request named this value makes the mocked command services raise a [DataConflictException]. */
        const val CONFLICT_ENTITY_NAME = "conflict"

        @Suppress("UNCHECKED_CAST")
        private inline fun <reified T : Any> createInterfaceProxy(
            crossinline handler: (methodName: String, args: Array<out Any?>) -> Any?,
        ): T =
            Proxy.newProxyInstance(
                T::class.java.classLoader,
                arrayOf(T::class.java),
            ) { proxy, method, rawArgs ->
                val args = rawArgs ?: emptyArray()
                when (method.name) {
                    "equals" -> proxy === args.firstOrNull()
                    "hashCode" -> System.identityHashCode(proxy)
                    "toString" -> "${T::class.simpleName}TestProxy"
                    else -> handler(method.name, args)
                }
            } as T
    }

    @Bean
    @Primary
    fun mockSalesforceQueryService(): ISalesforceQueryService {
        val sampleSfAccount = SfAccount(
            id = SAMPLE_SALESFORCE_ACCOUNT_ID,
            name = "Acme Corp",
            industry = "Technology",
        )

        return createInterfaceProxy { methodName, args ->
            when (methodName) {
                "getAccounts" -> {
                    val options = args[1] as QueryOptions
                    val industryFilter = options.filters?.get("Industry")
                    if (industryFilter == "Technology") {
                        PagedResult(
                            listOf(SfAccount(id = "001Filtered", name = "Tech Corp", industry = "Technology")),
                            1,
                            QueryOptions.DEFAULT_PAGE,
                            QueryOptions.DEFAULT_PAGE_SIZE,
                        )
                    } else {
                        PagedResult(
                            listOf(sampleSfAccount),
                            1,
                            QueryOptions.DEFAULT_PAGE,
                            QueryOptions.DEFAULT_PAGE_SIZE,
                        )
                    }
                }
                "getAccountById" -> if (args[1] == SAMPLE_SALESFORCE_ACCOUNT_ID) sampleSfAccount else null
                "getContactsByAccountId" -> if (args[1] == SAMPLE_SALESFORCE_ACCOUNT_ID) {
                    listOf(
                        SfContact(
                            id = SAMPLE_SALESFORCE_CONTACT_ID,
                            accountId = SAMPLE_SALESFORCE_ACCOUNT_ID,
                            lastName = "Doe",
                            firstName = "John",
                        ),
                    )
                } else {
                    emptyList<SfContact>()
                }
                else -> null
            }
        }
    }

    @Bean
    @Primary
    fun mockHubSpotQueryService(): IHubSpotQueryService {
        val sampleHsCompany = Company(
            companyId = SAMPLE_HUBSPOT_COMPANY_ID,
            name = "HubSpot Co",
            domain = "hubspot.com",
        )

        return createInterfaceProxy { methodName, args ->
            when (methodName) {
                "getCompanies" -> PagedResult(
                    listOf(sampleHsCompany),
                    1,
                    QueryOptions.DEFAULT_PAGE,
                    QueryOptions.DEFAULT_PAGE_SIZE,
                )
                "getCompanyById" -> if (args[1] == SAMPLE_HUBSPOT_COMPANY_ID) sampleHsCompany else null
                else -> null
            }
        }
    }

    @Bean
    @Primary
    fun mockDynamics365QueryService(): IDynamics365QueryService {
        val sampleD365Account = D365Account(
            accountId = SAMPLE_DYNAMICS_ACCOUNT_ID,
            name = "D365 Account",
        )

        return createInterfaceProxy { methodName, args ->
            when (methodName) {
                "getAccounts" -> PagedResult(
                    listOf(sampleD365Account),
                    1,
                    QueryOptions.DEFAULT_PAGE,
                    QueryOptions.DEFAULT_PAGE_SIZE,
                )
                "getAccountById" -> if (args[1] == SAMPLE_DYNAMICS_ACCOUNT_ID) sampleD365Account else null
                else -> null
            }
        }
    }

    @Bean
    @Primary
    fun mockSalesforceCommandService(): ISalesforceCommandService =
        createInterfaceProxy { methodName, args ->
            when (methodName) {
                "executeCreateAccount" -> (args[1] as SfAccountWriteRequest).let { request ->
                    if (request.name == CONFLICT_ENTITY_NAME) throw DataConflictException()
                    SfAccount(id = CREATED_SALESFORCE_ACCOUNT_ID, name = request.name, ownerId = request.ownerId)
                }
                "executeUpdateAccount" -> if (args[1] == SAMPLE_SALESFORCE_ACCOUNT_ID) {
                    (args[2] as SfAccountWriteRequest).let { SfAccount(id = SAMPLE_SALESFORCE_ACCOUNT_ID, name = it.name, ownerId = it.ownerId) }
                } else {
                    null
                }
                "executeDeleteAccount" -> args[1] == SAMPLE_SALESFORCE_ACCOUNT_ID
                else -> null
            }
        }

    @Bean
    @Primary
    fun mockHubSpotCommandService(): IHubSpotCommandService =
        createInterfaceProxy { methodName, args ->
            when (methodName) {
                "executeCreateCompany" -> (args[1] as CompanyWriteRequest).let { request ->
                    if (request.name == CONFLICT_ENTITY_NAME) throw DataConflictException()
                    Company(companyId = CREATED_HUBSPOT_COMPANY_ID, name = request.name)
                }
                "executeUpdateCompany" -> if (args[1] == SAMPLE_HUBSPOT_COMPANY_ID) {
                    Company(companyId = SAMPLE_HUBSPOT_COMPANY_ID, name = (args[2] as CompanyWriteRequest).name)
                } else {
                    null
                }
                "executeDeleteCompany" -> args[1] == SAMPLE_HUBSPOT_COMPANY_ID
                else -> null
            }
        }

    @Bean
    @Primary
    fun mockDynamics365CommandService(): IDynamics365CommandService =
        createInterfaceProxy { methodName, args ->
            when (methodName) {
                "executeCreateAccount" -> (args[1] as D365AccountWriteRequest).let { request ->
                    if (request.name == CONFLICT_ENTITY_NAME) throw DataConflictException()
                    D365Account(accountId = CREATED_DYNAMICS_ACCOUNT_ID, name = request.name, ownerId = request.ownerId)
                }
                "executeUpdateAccount" -> if (args[1] == SAMPLE_DYNAMICS_ACCOUNT_ID) {
                    D365Account(accountId = SAMPLE_DYNAMICS_ACCOUNT_ID, name = (args[2] as D365AccountWriteRequest).name)
                } else {
                    null
                }
                "executeDeleteAccount" -> args[1] == SAMPLE_DYNAMICS_ACCOUNT_ID
                else -> null
            }
        }
}
