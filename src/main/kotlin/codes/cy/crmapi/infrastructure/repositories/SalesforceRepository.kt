package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.salesforce.Account
import codes.cy.crmapi.domain.salesforce.AccountContactRelation
import codes.cy.crmapi.domain.salesforce.AccountWriteRequest
import codes.cy.crmapi.domain.salesforce.Campaign
import codes.cy.crmapi.domain.salesforce.CampaignMember
import codes.cy.crmapi.domain.salesforce.Case
import codes.cy.crmapi.domain.salesforce.Contact
import codes.cy.crmapi.domain.salesforce.ContactWriteRequest
import codes.cy.crmapi.domain.salesforce.Event
import codes.cy.crmapi.domain.salesforce.Lead
import codes.cy.crmapi.domain.salesforce.Opportunity
import codes.cy.crmapi.domain.salesforce.OpportunityLineItem
import codes.cy.crmapi.domain.salesforce.OpportunityWriteRequest
import codes.cy.crmapi.domain.salesforce.Pricebook2
import codes.cy.crmapi.domain.salesforce.PricebookEntry
import codes.cy.crmapi.domain.salesforce.Product2
import codes.cy.crmapi.domain.salesforce.Task
import codes.cy.crmapi.domain.salesforce.User
import java.time.OffsetDateTime
import org.springframework.stereotype.Repository

@Repository
class SalesforceRepository(
    private val baseRepository: IBaseRepository,
) : ISalesforceRepository {
    override fun getAccounts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Account> =
        baseRepository.getPaged(provider, "Salesforce.Account", options, Account::class, "Id")

    override fun getAccountById(provider: DatabaseProvider, id: String): Account? =
        baseRepository.getById(provider, "Salesforce.Account", "Id", id, Account::class)

    override fun getContactsByAccountId(provider: DatabaseProvider, accountId: String): List<Contact> =
        baseRepository.getList(provider, "Salesforce.Contact", Contact::class) { q -> q.where("AccountId", accountId) }

    override fun getOpportunitiesByAccountId(provider: DatabaseProvider, accountId: String): List<Opportunity> =
        baseRepository.getList(provider, "Salesforce.Opportunity", Opportunity::class) { q -> q.where("AccountId", accountId) }

    override fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact> =
        baseRepository.getPaged(provider, "Salesforce.Contact", options, Contact::class, "Id")

    override fun getContactById(provider: DatabaseProvider, id: String): Contact? =
        baseRepository.getById(provider, "Salesforce.Contact", "Id", id, Contact::class)

    override fun getOpportunities(provider: DatabaseProvider, options: QueryOptions): PagedResult<Opportunity> =
        baseRepository.getPaged(provider, "Salesforce.Opportunity", options, Opportunity::class, "Id")

    override fun getOpportunityById(provider: DatabaseProvider, id: String): Opportunity? =
        baseRepository.getById(provider, "Salesforce.Opportunity", "Id", id, Opportunity::class)

    override fun getLineItemsByOpportunityId(provider: DatabaseProvider, opportunityId: String): List<OpportunityLineItem> =
        baseRepository.getList(provider, "Salesforce.OpportunityLineItem", OpportunityLineItem::class) { q -> q.where("OpportunityId", opportunityId) }

    override fun getLeads(provider: DatabaseProvider, options: QueryOptions): PagedResult<Lead> =
        baseRepository.getPaged(provider, "Salesforce.Lead", options, Lead::class, "Id")

    override fun getLeadById(provider: DatabaseProvider, id: String): Lead? =
        baseRepository.getById(provider, "Salesforce.Lead", "Id", id, Lead::class)

    override fun getCases(provider: DatabaseProvider, options: QueryOptions): PagedResult<Case> =
        baseRepository.getPaged(provider, "Salesforce.Case", options, Case::class, "Id")

    override fun getCaseById(provider: DatabaseProvider, id: String): Case? =
        baseRepository.getById(provider, "Salesforce.Case", "Id", id, Case::class)

    override fun getUsers(provider: DatabaseProvider, options: QueryOptions): PagedResult<User> =
        baseRepository.getPaged(provider, "Salesforce.User", options, User::class, "Id")

    override fun getUserById(provider: DatabaseProvider, id: String): User? =
        baseRepository.getById(provider, "Salesforce.User", "Id", id, User::class)

    override fun getCampaigns(provider: DatabaseProvider, options: QueryOptions): PagedResult<Campaign> =
        baseRepository.getPaged(provider, "Salesforce.Campaign", options, Campaign::class, "Id")

    override fun getCampaignById(provider: DatabaseProvider, id: String): Campaign? =
        baseRepository.getById(provider, "Salesforce.Campaign", "Id", id, Campaign::class)

    override fun getMembersByCampaignId(provider: DatabaseProvider, campaignId: String): List<CampaignMember> =
        baseRepository.getList(provider, "Salesforce.CampaignMember", CampaignMember::class) { q -> q.where("CampaignId", campaignId) }

    override fun getTasks(provider: DatabaseProvider, options: QueryOptions): PagedResult<Task> =
        baseRepository.getPaged(provider, "Salesforce.Task", options, Task::class, "Id")

    override fun getTaskById(provider: DatabaseProvider, id: String): Task? =
        baseRepository.getById(provider, "Salesforce.Task", "Id", id, Task::class)

    override fun getEvents(provider: DatabaseProvider, options: QueryOptions): PagedResult<Event> =
        baseRepository.getPaged(provider, "Salesforce.Event", options, Event::class, "Id")

    override fun getEventById(provider: DatabaseProvider, id: String): Event? =
        baseRepository.getById(provider, "Salesforce.Event", "Id", id, Event::class)

    override fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product2> =
        baseRepository.getPaged(provider, "Salesforce.Product2", options, Product2::class, "Id")

    override fun getProductById(provider: DatabaseProvider, id: String): Product2? =
        baseRepository.getById(provider, "Salesforce.Product2", "Id", id, Product2::class)

    override fun getPricebooks(provider: DatabaseProvider, options: QueryOptions): PagedResult<Pricebook2> =
        baseRepository.getPaged(provider, "Salesforce.Pricebook2", options, Pricebook2::class, "Id")

    override fun getPricebookById(provider: DatabaseProvider, id: String): Pricebook2? =
        baseRepository.getById(provider, "Salesforce.Pricebook2", "Id", id, Pricebook2::class)

    override fun getPricebookEntries(provider: DatabaseProvider, options: QueryOptions): PagedResult<PricebookEntry> =
        baseRepository.getPaged(provider, "Salesforce.PricebookEntry", options, PricebookEntry::class, "Id")

    override fun getPricebookEntryById(provider: DatabaseProvider, id: String): PricebookEntry? =
        baseRepository.getById(provider, "Salesforce.PricebookEntry", "Id", id, PricebookEntry::class)

    override fun getOpportunityLineItems(provider: DatabaseProvider, options: QueryOptions): PagedResult<OpportunityLineItem> =
        baseRepository.getPaged(provider, "Salesforce.OpportunityLineItem", options, OpportunityLineItem::class, "Id")

    override fun getOpportunityLineItemById(provider: DatabaseProvider, id: String): OpportunityLineItem? =
        baseRepository.getById(provider, "Salesforce.OpportunityLineItem", "Id", id, OpportunityLineItem::class)

    override fun getAccountContactRelations(provider: DatabaseProvider, options: QueryOptions): PagedResult<AccountContactRelation> =
        baseRepository.getPaged(provider, "Salesforce.AccountContactRelation", options, AccountContactRelation::class, "Id")

    override fun getAccountContactRelationById(provider: DatabaseProvider, id: String): AccountContactRelation? =
        baseRepository.getById(provider, "Salesforce.AccountContactRelation", "Id", id, AccountContactRelation::class)

    override fun getCampaignMembers(provider: DatabaseProvider, options: QueryOptions): PagedResult<CampaignMember> =
        baseRepository.getPaged(provider, "Salesforce.CampaignMember", options, CampaignMember::class, "Id")

    override fun getCampaignMemberById(provider: DatabaseProvider, id: String): CampaignMember? =
        baseRepository.getById(provider, "Salesforce.CampaignMember", "Id", id, CampaignMember::class)

    override fun executeCreateAccount(provider: DatabaseProvider, id: String, request: AccountWriteRequest, now: OffsetDateTime): Account =
        baseRepository.executeCreate(provider, SALESFORCE_ACCOUNT_TABLE, SALESFORCE_ID_COLUMN, id, request.toInsertColumns(id, now), Account::class)

    override fun executeUpdateAccount(provider: DatabaseProvider, id: String, request: AccountWriteRequest, now: OffsetDateTime): Account? =
        baseRepository.executeUpdate(provider, SALESFORCE_ACCOUNT_TABLE, SALESFORCE_ID_COLUMN, id, request.toUpdateColumns(now), Account::class)

    override fun executeDeleteAccount(provider: DatabaseProvider, id: String): Boolean =
        baseRepository.executeDelete(provider, SALESFORCE_ACCOUNT_TABLE, SALESFORCE_ID_COLUMN, id)

    override fun executeCreateContact(provider: DatabaseProvider, id: String, request: ContactWriteRequest, now: OffsetDateTime): Contact =
        baseRepository.executeCreate(provider, SALESFORCE_CONTACT_TABLE, SALESFORCE_ID_COLUMN, id, request.toInsertColumns(id, now), Contact::class)

    override fun executeUpdateContact(provider: DatabaseProvider, id: String, request: ContactWriteRequest, now: OffsetDateTime): Contact? =
        baseRepository.executeUpdate(provider, SALESFORCE_CONTACT_TABLE, SALESFORCE_ID_COLUMN, id, request.toUpdateColumns(now), Contact::class)

    override fun executeDeleteContact(provider: DatabaseProvider, id: String): Boolean =
        baseRepository.executeDelete(provider, SALESFORCE_CONTACT_TABLE, SALESFORCE_ID_COLUMN, id)

    override fun executeCreateOpportunity(provider: DatabaseProvider, id: String, request: OpportunityWriteRequest, now: OffsetDateTime): Opportunity =
        baseRepository.executeCreate(provider, SALESFORCE_OPPORTUNITY_TABLE, SALESFORCE_ID_COLUMN, id, request.toInsertColumns(id, now), Opportunity::class)

    override fun executeUpdateOpportunity(provider: DatabaseProvider, id: String, request: OpportunityWriteRequest, now: OffsetDateTime): Opportunity? =
        baseRepository.executeUpdate(provider, SALESFORCE_OPPORTUNITY_TABLE, SALESFORCE_ID_COLUMN, id, request.toUpdateColumns(now), Opportunity::class)

    override fun executeDeleteOpportunity(provider: DatabaseProvider, id: String): Boolean =
        baseRepository.executeDelete(provider, SALESFORCE_OPPORTUNITY_TABLE, SALESFORCE_ID_COLUMN, id)
}
