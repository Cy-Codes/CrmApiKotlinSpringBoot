package codes.cy.crmapi.application.services

import codes.cy.crmapi.application.interfaces.ISalesforceQueryService
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
import codes.cy.crmapi.infrastructure.repositories.ISalesforceRepository
import org.springframework.stereotype.Service

@Service
class SalesforceQueryService(
    private val repository: ISalesforceRepository,
) : ISalesforceQueryService {
    override fun getAccounts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Account> =
        repository.getAccounts(provider, options)

    override fun getAccountById(provider: DatabaseProvider, id: String): Account? =
        repository.getAccountById(provider, id)

    override fun getContactsByAccountId(provider: DatabaseProvider, accountId: String): List<Contact> =
        repository.getContactsByAccountId(provider, accountId)

    override fun getOpportunitiesByAccountId(provider: DatabaseProvider, accountId: String): List<Opportunity> =
        repository.getOpportunitiesByAccountId(provider, accountId)

    override fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact> =
        repository.getContacts(provider, options)

    override fun getContactById(provider: DatabaseProvider, id: String): Contact? =
        repository.getContactById(provider, id)

    override fun getOpportunities(provider: DatabaseProvider, options: QueryOptions): PagedResult<Opportunity> =
        repository.getOpportunities(provider, options)

    override fun getOpportunityById(provider: DatabaseProvider, id: String): Opportunity? =
        repository.getOpportunityById(provider, id)

    override fun getLineItemsByOpportunityId(provider: DatabaseProvider, opportunityId: String): List<OpportunityLineItem> =
        repository.getLineItemsByOpportunityId(provider, opportunityId)

    override fun getLeads(provider: DatabaseProvider, options: QueryOptions): PagedResult<Lead> =
        repository.getLeads(provider, options)

    override fun getLeadById(provider: DatabaseProvider, id: String): Lead? =
        repository.getLeadById(provider, id)

    override fun getCases(provider: DatabaseProvider, options: QueryOptions): PagedResult<Case> =
        repository.getCases(provider, options)

    override fun getCaseById(provider: DatabaseProvider, id: String): Case? =
        repository.getCaseById(provider, id)

    override fun getUsers(provider: DatabaseProvider, options: QueryOptions): PagedResult<User> =
        repository.getUsers(provider, options)

    override fun getUserById(provider: DatabaseProvider, id: String): User? =
        repository.getUserById(provider, id)

    override fun getCampaigns(provider: DatabaseProvider, options: QueryOptions): PagedResult<Campaign> =
        repository.getCampaigns(provider, options)

    override fun getCampaignById(provider: DatabaseProvider, id: String): Campaign? =
        repository.getCampaignById(provider, id)

    override fun getMembersByCampaignId(provider: DatabaseProvider, campaignId: String): List<CampaignMember> =
        repository.getMembersByCampaignId(provider, campaignId)

    override fun getTasks(provider: DatabaseProvider, options: QueryOptions): PagedResult<Task> =
        repository.getTasks(provider, options)

    override fun getTaskById(provider: DatabaseProvider, id: String): Task? =
        repository.getTaskById(provider, id)

    override fun getEvents(provider: DatabaseProvider, options: QueryOptions): PagedResult<Event> =
        repository.getEvents(provider, options)

    override fun getEventById(provider: DatabaseProvider, id: String): Event? =
        repository.getEventById(provider, id)

    override fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product2> =
        repository.getProducts(provider, options)

    override fun getProductById(provider: DatabaseProvider, id: String): Product2? =
        repository.getProductById(provider, id)

    override fun getPricebooks(provider: DatabaseProvider, options: QueryOptions): PagedResult<Pricebook2> =
        repository.getPricebooks(provider, options)

    override fun getPricebookById(provider: DatabaseProvider, id: String): Pricebook2? =
        repository.getPricebookById(provider, id)

    override fun getPricebookEntries(provider: DatabaseProvider, options: QueryOptions): PagedResult<PricebookEntry> =
        repository.getPricebookEntries(provider, options)

    override fun getPricebookEntryById(provider: DatabaseProvider, id: String): PricebookEntry? =
        repository.getPricebookEntryById(provider, id)

    override fun getOpportunityLineItems(provider: DatabaseProvider, options: QueryOptions): PagedResult<OpportunityLineItem> =
        repository.getOpportunityLineItems(provider, options)

    override fun getOpportunityLineItemById(provider: DatabaseProvider, id: String): OpportunityLineItem? =
        repository.getOpportunityLineItemById(provider, id)

    override fun getAccountContactRelations(provider: DatabaseProvider, options: QueryOptions): PagedResult<AccountContactRelation> =
        repository.getAccountContactRelations(provider, options)

    override fun getAccountContactRelationById(provider: DatabaseProvider, id: String): AccountContactRelation? =
        repository.getAccountContactRelationById(provider, id)

    override fun getCampaignMembers(provider: DatabaseProvider, options: QueryOptions): PagedResult<CampaignMember> =
        repository.getCampaignMembers(provider, options)

    override fun getCampaignMemberById(provider: DatabaseProvider, id: String): CampaignMember? =
        repository.getCampaignMemberById(provider, id)
}
