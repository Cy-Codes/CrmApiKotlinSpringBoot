package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.dynamics365.Account
import codes.cy.crmapi.domain.dynamics365.AccountWriteRequest
import codes.cy.crmapi.domain.dynamics365.ActivityPointer
import codes.cy.crmapi.domain.dynamics365.BusinessUnit
import codes.cy.crmapi.domain.dynamics365.Contact
import codes.cy.crmapi.domain.dynamics365.ContactWriteRequest
import codes.cy.crmapi.domain.dynamics365.Incident
import codes.cy.crmapi.domain.dynamics365.Lead
import codes.cy.crmapi.domain.dynamics365.Opportunity
import codes.cy.crmapi.domain.dynamics365.OpportunityProduct
import codes.cy.crmapi.domain.dynamics365.OpportunityWriteRequest
import codes.cy.crmapi.domain.dynamics365.PriceLevel
import codes.cy.crmapi.domain.dynamics365.Product
import codes.cy.crmapi.domain.dynamics365.SystemUser
import java.time.OffsetDateTime
import java.util.UUID

interface IDynamics365Repository {
    fun getAccounts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Account>
    fun getAccountById(provider: DatabaseProvider, id: UUID): Account?
    fun getContactsByAccountId(provider: DatabaseProvider, accountId: UUID): List<Contact>
    fun getOpportunitiesByAccountId(provider: DatabaseProvider, accountId: UUID): List<Opportunity>
    fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact>
    fun getContactById(provider: DatabaseProvider, id: UUID): Contact?
    fun getOpportunities(provider: DatabaseProvider, options: QueryOptions): PagedResult<Opportunity>
    fun getOpportunityById(provider: DatabaseProvider, id: UUID): Opportunity?
    fun getProductsByOpportunityId(provider: DatabaseProvider, opportunityId: UUID): List<OpportunityProduct>
    fun getLeads(provider: DatabaseProvider, options: QueryOptions): PagedResult<Lead>
    fun getLeadById(provider: DatabaseProvider, id: UUID): Lead?
    fun getIncidents(provider: DatabaseProvider, options: QueryOptions): PagedResult<Incident>
    fun getIncidentById(provider: DatabaseProvider, id: UUID): Incident?
    fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product>
    fun getProductById(provider: DatabaseProvider, id: UUID): Product?
    fun getPriceLevels(provider: DatabaseProvider, options: QueryOptions): PagedResult<PriceLevel>
    fun getPriceLevelById(provider: DatabaseProvider, id: UUID): PriceLevel?
    fun getOpportunityProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<OpportunityProduct>
    fun getOpportunityProductById(provider: DatabaseProvider, id: UUID): OpportunityProduct?
    fun getBusinessUnits(provider: DatabaseProvider, options: QueryOptions): PagedResult<BusinessUnit>
    fun getBusinessUnitById(provider: DatabaseProvider, id: UUID): BusinessUnit?
    fun getSystemUsers(provider: DatabaseProvider, options: QueryOptions): PagedResult<SystemUser>
    fun getSystemUserById(provider: DatabaseProvider, id: UUID): SystemUser?
    fun getActivityPointers(provider: DatabaseProvider, options: QueryOptions): PagedResult<ActivityPointer>
    fun getActivityPointerById(provider: DatabaseProvider, id: UUID): ActivityPointer?

    fun executeCreateAccount(provider: DatabaseProvider, id: UUID, request: AccountWriteRequest, now: OffsetDateTime): Account
    fun executeUpdateAccount(provider: DatabaseProvider, id: UUID, request: AccountWriteRequest, now: OffsetDateTime): Account?
    fun executeDeleteAccount(provider: DatabaseProvider, id: UUID): Boolean
    fun executeCreateContact(provider: DatabaseProvider, id: UUID, request: ContactWriteRequest, now: OffsetDateTime): Contact
    fun executeUpdateContact(provider: DatabaseProvider, id: UUID, request: ContactWriteRequest, now: OffsetDateTime): Contact?
    fun executeDeleteContact(provider: DatabaseProvider, id: UUID): Boolean
    fun executeCreateOpportunity(provider: DatabaseProvider, id: UUID, request: OpportunityWriteRequest, now: OffsetDateTime): Opportunity
    fun executeUpdateOpportunity(provider: DatabaseProvider, id: UUID, request: OpportunityWriteRequest, now: OffsetDateTime): Opportunity?
    fun executeDeleteOpportunity(provider: DatabaseProvider, id: UUID): Boolean
}
