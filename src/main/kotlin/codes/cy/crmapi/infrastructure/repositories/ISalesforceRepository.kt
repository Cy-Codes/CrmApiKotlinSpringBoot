package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.salesforce.Account
import codes.cy.crmapi.domain.salesforce.AccountContactRelation
import codes.cy.crmapi.domain.salesforce.Campaign
import codes.cy.crmapi.domain.salesforce.CampaignMember
import codes.cy.crmapi.domain.salesforce.Case
import codes.cy.crmapi.domain.salesforce.Contact
import codes.cy.crmapi.domain.salesforce.Event
import codes.cy.crmapi.domain.salesforce.Lead
import codes.cy.crmapi.domain.salesforce.Opportunity
import codes.cy.crmapi.domain.salesforce.OpportunityLineItem
import codes.cy.crmapi.domain.salesforce.Pricebook2
import codes.cy.crmapi.domain.salesforce.PricebookEntry
import codes.cy.crmapi.domain.salesforce.Product2
import codes.cy.crmapi.domain.salesforce.Task
import codes.cy.crmapi.domain.salesforce.User

interface ISalesforceRepository {
    fun getAccounts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Account>
    fun getAccountById(provider: DatabaseProvider, id: String): Account?
    fun getContactsByAccountId(provider: DatabaseProvider, accountId: String): List<Contact>
    fun getOpportunitiesByAccountId(provider: DatabaseProvider, accountId: String): List<Opportunity>
    fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact>
    fun getContactById(provider: DatabaseProvider, id: String): Contact?
    fun getOpportunities(provider: DatabaseProvider, options: QueryOptions): PagedResult<Opportunity>
    fun getOpportunityById(provider: DatabaseProvider, id: String): Opportunity?
    fun getLineItemsByOpportunityId(provider: DatabaseProvider, opportunityId: String): List<OpportunityLineItem>
    fun getLeads(provider: DatabaseProvider, options: QueryOptions): PagedResult<Lead>
    fun getLeadById(provider: DatabaseProvider, id: String): Lead?
    fun getCases(provider: DatabaseProvider, options: QueryOptions): PagedResult<Case>
    fun getCaseById(provider: DatabaseProvider, id: String): Case?
    fun getUsers(provider: DatabaseProvider, options: QueryOptions): PagedResult<User>
    fun getUserById(provider: DatabaseProvider, id: String): User?
    fun getCampaigns(provider: DatabaseProvider, options: QueryOptions): PagedResult<Campaign>
    fun getCampaignById(provider: DatabaseProvider, id: String): Campaign?
    fun getMembersByCampaignId(provider: DatabaseProvider, campaignId: String): List<CampaignMember>
    fun getTasks(provider: DatabaseProvider, options: QueryOptions): PagedResult<Task>
    fun getTaskById(provider: DatabaseProvider, id: String): Task?
    fun getEvents(provider: DatabaseProvider, options: QueryOptions): PagedResult<Event>
    fun getEventById(provider: DatabaseProvider, id: String): Event?
    fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product2>
    fun getProductById(provider: DatabaseProvider, id: String): Product2?
    fun getPricebooks(provider: DatabaseProvider, options: QueryOptions): PagedResult<Pricebook2>
    fun getPricebookById(provider: DatabaseProvider, id: String): Pricebook2?
    fun getPricebookEntries(provider: DatabaseProvider, options: QueryOptions): PagedResult<PricebookEntry>
    fun getPricebookEntryById(provider: DatabaseProvider, id: String): PricebookEntry?
    fun getOpportunityLineItems(provider: DatabaseProvider, options: QueryOptions): PagedResult<OpportunityLineItem>
    fun getOpportunityLineItemById(provider: DatabaseProvider, id: String): OpportunityLineItem?
    fun getAccountContactRelations(provider: DatabaseProvider, options: QueryOptions): PagedResult<AccountContactRelation>
    fun getAccountContactRelationById(provider: DatabaseProvider, id: String): AccountContactRelation?
    fun getCampaignMembers(provider: DatabaseProvider, options: QueryOptions): PagedResult<CampaignMember>
    fun getCampaignMemberById(provider: DatabaseProvider, id: String): CampaignMember?
}
