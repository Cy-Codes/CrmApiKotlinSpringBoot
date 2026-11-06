package codes.cy.crmapi.api.controllers

import codes.cy.crmapi.api.extensions.toOkResponseEntity
import codes.cy.crmapi.api.extensions.toResponseEntity
import codes.cy.crmapi.application.interfaces.ISalesforceQueryService
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.common.toDatabaseProvider
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
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/{provider:^(?:ms|pg)$}/v1/salesforce", produces = [MediaType.APPLICATION_JSON_VALUE])
class SalesforceController(
    private val queryService: ISalesforceQueryService,
) : BaseCrmController() {
    @GetMapping("/accounts")
    fun getAccounts(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Account>> =
        queryService
            .getAccounts(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/accounts/{id}")
    fun getAccountById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Account> =
        queryService.getAccountById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/accounts/{id}/contacts")
    fun getContactsByAccountId(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<List<Contact>> =
        queryService.getContactsByAccountId(provider.toDatabaseProvider(), id).toOkResponseEntity()

    @GetMapping("/accounts/{id}/opportunities")
    fun getOpportunitiesByAccountId(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<List<Opportunity>> =
        queryService.getOpportunitiesByAccountId(provider.toDatabaseProvider(), id).toOkResponseEntity()

    @GetMapping("/contacts")
    fun getContacts(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Contact>> =
        queryService
            .getContacts(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/contacts/{id}")
    fun getContactById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Contact> =
        queryService.getContactById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/opportunities")
    fun getOpportunities(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Opportunity>> =
        queryService
            .getOpportunities(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/opportunities/{id}")
    fun getOpportunityById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Opportunity> =
        queryService.getOpportunityById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/opportunities/{id}/line-items")
    fun getLineItemsByOpportunityId(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<List<OpportunityLineItem>> =
        queryService.getLineItemsByOpportunityId(provider.toDatabaseProvider(), id).toOkResponseEntity()

    @GetMapping("/leads")
    fun getLeads(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Lead>> =
        queryService
            .getLeads(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/leads/{id}")
    fun getLeadById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Lead> =
        queryService.getLeadById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/cases")
    fun getCases(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Case>> =
        queryService
            .getCases(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/cases/{id}")
    fun getCaseById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Case> =
        queryService.getCaseById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/users")
    fun getUsers(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<User>> =
        queryService
            .getUsers(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/users/{id}")
    fun getUserById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<User> =
        queryService.getUserById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/campaigns")
    fun getCampaigns(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Campaign>> =
        queryService
            .getCampaigns(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/campaigns/{id}")
    fun getCampaignById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Campaign> =
        queryService.getCampaignById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/campaigns/{id}/members")
    fun getMembersByCampaignId(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<List<CampaignMember>> =
        queryService.getMembersByCampaignId(provider.toDatabaseProvider(), id).toOkResponseEntity()

    @GetMapping("/tasks")
    fun getTasks(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Task>> =
        queryService
            .getTasks(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/tasks/{id}")
    fun getTaskById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Task> =
        queryService.getTaskById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/events")
    fun getEvents(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Event>> =
        queryService
            .getEvents(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/events/{id}")
    fun getEventById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Event> =
        queryService.getEventById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/products")
    fun getProducts(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Product2>> =
        queryService
            .getProducts(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/products/{id}")
    fun getProductById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Product2> =
        queryService.getProductById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/pricebooks")
    fun getPricebooks(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Pricebook2>> =
        queryService
            .getPricebooks(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/pricebooks/{id}")
    fun getPricebookById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Pricebook2> =
        queryService.getPricebookById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/pricebook-entries")
    fun getPricebookEntries(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<PricebookEntry>> =
        queryService
            .getPricebookEntries(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/pricebook-entries/{id}")
    fun getPricebookEntryById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<PricebookEntry> =
        queryService.getPricebookEntryById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/opportunity-line-items")
    fun getOpportunityLineItems(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<OpportunityLineItem>> =
        queryService
            .getOpportunityLineItems(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/opportunity-line-items/{id}")
    fun getOpportunityLineItemById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<OpportunityLineItem> =
        queryService.getOpportunityLineItemById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/account-contact-relations")
    fun getAccountContactRelations(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<AccountContactRelation>> =
        queryService
            .getAccountContactRelations(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/account-contact-relations/{id}")
    fun getAccountContactRelationById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<AccountContactRelation> =
        queryService.getAccountContactRelationById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/campaign-members")
    fun getCampaignMembers(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<CampaignMember>> =
        queryService
            .getCampaignMembers(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/campaign-members/{id}")
    fun getCampaignMemberById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<CampaignMember> =
        queryService.getCampaignMemberById(provider.toDatabaseProvider(), id).toResponseEntity()
}
