package codes.cy.crmapi.api.graphql

import codes.cy.crmapi.application.interfaces.IDynamics365QueryService
import codes.cy.crmapi.application.interfaces.IHubSpotQueryService
import codes.cy.crmapi.application.interfaces.ISalesforceQueryService
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.dynamics365.Account as D365Account
import codes.cy.crmapi.domain.dynamics365.Contact as D365Contact
import codes.cy.crmapi.domain.hubspot.Company
import codes.cy.crmapi.domain.hubspot.Contact as HsContact
import codes.cy.crmapi.domain.salesforce.Account as SfAccount
import codes.cy.crmapi.domain.salesforce.Contact as SfContact
import java.util.UUID
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

/**
 * GraphQL root query resolver providing unified multi-CRM queries across SQL Server and PostgreSQL.
 */
@Controller
class Query(
    private val salesforceQueryService: ISalesforceQueryService,
    private val hubSpotQueryService: IHubSpotQueryService,
    private val dynamics365QueryService: IDynamics365QueryService,
) {
    // Salesforce
    @QueryMapping
    fun salesforceAccounts(
        @Argument provider: DatabaseProvider?,
        @Argument page: Int?,
        @Argument pageSize: Int?,
    ): List<SfAccount> =
        salesforceQueryService.getAccounts(
            provider ?: DatabaseProvider.Ms,
            QueryOptions(page ?: QueryOptions.DEFAULT_PAGE, pageSize ?: QueryOptions.DEFAULT_PAGE_SIZE),
        ).items

    @QueryMapping
    fun salesforceAccountById(
        @Argument id: String,
        @Argument provider: DatabaseProvider?,
    ): SfAccount? =
        salesforceQueryService.getAccountById(provider ?: DatabaseProvider.Ms, id)

    @QueryMapping
    fun salesforceContacts(
        @Argument provider: DatabaseProvider?,
        @Argument page: Int?,
        @Argument pageSize: Int?,
    ): List<SfContact> =
        salesforceQueryService.getContacts(
            provider ?: DatabaseProvider.Ms,
            QueryOptions(page ?: QueryOptions.DEFAULT_PAGE, pageSize ?: QueryOptions.DEFAULT_PAGE_SIZE),
        ).items

    @QueryMapping
    fun salesforceContactById(
        @Argument id: String,
        @Argument provider: DatabaseProvider?,
    ): SfContact? =
        salesforceQueryService.getContactById(provider ?: DatabaseProvider.Ms, id)

    // HubSpot
    @QueryMapping
    fun hubSpotCompanies(
        @Argument provider: DatabaseProvider?,
        @Argument page: Int?,
        @Argument pageSize: Int?,
    ): List<Company> =
        hubSpotQueryService.getCompanies(
            provider ?: DatabaseProvider.Ms,
            QueryOptions(page ?: QueryOptions.DEFAULT_PAGE, pageSize ?: QueryOptions.DEFAULT_PAGE_SIZE),
        ).items

    @QueryMapping
    fun hubSpotCompanyById(
        @Argument id: String,
        @Argument provider: DatabaseProvider?,
    ): Company? =
        hubSpotQueryService.getCompanyById(provider ?: DatabaseProvider.Ms, id.toLongOrNull() ?: 0L)

    @QueryMapping
    fun hubSpotContacts(
        @Argument provider: DatabaseProvider?,
        @Argument page: Int?,
        @Argument pageSize: Int?,
    ): List<HsContact> =
        hubSpotQueryService.getContacts(
            provider ?: DatabaseProvider.Ms,
            QueryOptions(page ?: QueryOptions.DEFAULT_PAGE, pageSize ?: QueryOptions.DEFAULT_PAGE_SIZE),
        ).items

    // Dynamics 365
    @QueryMapping
    fun dynamics365Accounts(
        @Argument provider: DatabaseProvider?,
        @Argument page: Int?,
        @Argument pageSize: Int?,
    ): List<D365Account> =
        dynamics365QueryService.getAccounts(
            provider ?: DatabaseProvider.Ms,
            QueryOptions(page ?: QueryOptions.DEFAULT_PAGE, pageSize ?: QueryOptions.DEFAULT_PAGE_SIZE),
        ).items

    @QueryMapping
    fun dynamics365AccountById(
        @Argument id: String,
        @Argument provider: DatabaseProvider?,
    ): D365Account? =
        runCatching { UUID.fromString(id) }.getOrNull()?.let { uuid ->
            dynamics365QueryService.getAccountById(provider ?: DatabaseProvider.Ms, uuid)
        }

    @QueryMapping
    fun dynamics365Contacts(
        @Argument provider: DatabaseProvider?,
        @Argument page: Int?,
        @Argument pageSize: Int?,
    ): List<D365Contact> =
        dynamics365QueryService.getContacts(
            provider ?: DatabaseProvider.Ms,
            QueryOptions(page ?: QueryOptions.DEFAULT_PAGE, pageSize ?: QueryOptions.DEFAULT_PAGE_SIZE),
        ).items
}
