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
import org.springframework.stereotype.Component

/**
 * GraphQL root query resolver providing unified multi-CRM queries across SQL Server and PostgreSQL.
 */
@Component
class Query(
    private val salesforceQueryService: ISalesforceQueryService,
    private val hubSpotQueryService: IHubSpotQueryService,
    private val dynamics365QueryService: IDynamics365QueryService,
) {
    // Salesforce
    fun getSalesforceAccounts(
        provider: DatabaseProvider = DatabaseProvider.Ms,
        page: Int = QueryOptions.DEFAULT_PAGE,
        pageSize: Int = QueryOptions.DEFAULT_PAGE_SIZE,
    ): List<SfAccount> =
        salesforceQueryService.getAccounts(provider, QueryOptions(page, pageSize)).items

    fun getSalesforceAccountById(
        id: String,
        provider: DatabaseProvider = DatabaseProvider.Ms,
    ): SfAccount? =
        salesforceQueryService.getAccountById(provider, id)

    fun getSalesforceContacts(
        provider: DatabaseProvider = DatabaseProvider.Ms,
        page: Int = QueryOptions.DEFAULT_PAGE,
        pageSize: Int = QueryOptions.DEFAULT_PAGE_SIZE,
    ): List<SfContact> =
        salesforceQueryService.getContacts(provider, QueryOptions(page, pageSize)).items

    fun getSalesforceContactById(
        id: String,
        provider: DatabaseProvider = DatabaseProvider.Ms,
    ): SfContact? =
        salesforceQueryService.getContactById(provider, id)

    // HubSpot
    fun getHubSpotCompanies(
        provider: DatabaseProvider = DatabaseProvider.Ms,
        page: Int = QueryOptions.DEFAULT_PAGE,
        pageSize: Int = QueryOptions.DEFAULT_PAGE_SIZE,
    ): List<Company> =
        hubSpotQueryService.getCompanies(provider, QueryOptions(page, pageSize)).items

    fun getHubSpotCompanyById(
        id: Long,
        provider: DatabaseProvider = DatabaseProvider.Ms,
    ): Company? =
        hubSpotQueryService.getCompanyById(provider, id)

    fun getHubSpotContacts(
        provider: DatabaseProvider = DatabaseProvider.Ms,
        page: Int = QueryOptions.DEFAULT_PAGE,
        pageSize: Int = QueryOptions.DEFAULT_PAGE_SIZE,
    ): List<HsContact> =
        hubSpotQueryService.getContacts(provider, QueryOptions(page, pageSize)).items

    // Dynamics 365
    fun getDynamics365Accounts(
        provider: DatabaseProvider = DatabaseProvider.Ms,
        page: Int = QueryOptions.DEFAULT_PAGE,
        pageSize: Int = QueryOptions.DEFAULT_PAGE_SIZE,
    ): List<D365Account> =
        dynamics365QueryService.getAccounts(provider, QueryOptions(page, pageSize)).items

    fun getDynamics365AccountById(
        id: UUID,
        provider: DatabaseProvider = DatabaseProvider.Ms,
    ): D365Account? =
        dynamics365QueryService.getAccountById(provider, id)

    fun getDynamics365Contacts(
        provider: DatabaseProvider = DatabaseProvider.Ms,
        page: Int = QueryOptions.DEFAULT_PAGE,
        pageSize: Int = QueryOptions.DEFAULT_PAGE_SIZE,
    ): List<D365Contact> =
        dynamics365QueryService.getContacts(provider, QueryOptions(page, pageSize)).items
}
