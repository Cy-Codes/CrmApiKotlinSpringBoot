package codes.cy.crmapi.api.controllers

import codes.cy.crmapi.api.extensions.toOkResponseEntity
import codes.cy.crmapi.api.extensions.toResponseEntity
import codes.cy.crmapi.application.interfaces.IDynamics365QueryService
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.common.toDatabaseProvider
import codes.cy.crmapi.domain.dynamics365.Account
import codes.cy.crmapi.domain.dynamics365.ActivityPointer
import codes.cy.crmapi.domain.dynamics365.BusinessUnit
import codes.cy.crmapi.domain.dynamics365.Contact
import codes.cy.crmapi.domain.dynamics365.Incident
import codes.cy.crmapi.domain.dynamics365.Lead
import codes.cy.crmapi.domain.dynamics365.Opportunity
import codes.cy.crmapi.domain.dynamics365.OpportunityProduct
import codes.cy.crmapi.domain.dynamics365.PriceLevel
import codes.cy.crmapi.domain.dynamics365.Product
import codes.cy.crmapi.domain.dynamics365.SystemUser
import java.util.UUID
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/{provider:^(?:ms|pg)$}/v1/dynamics365", produces = [MediaType.APPLICATION_JSON_VALUE])
class Dynamics365Controller(
    private val queryService: IDynamics365QueryService,
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
        @PathVariable id: UUID,
    ): ResponseEntity<Account> =
        queryService.getAccountById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/accounts/{id}/contacts")
    fun getContactsByAccountId(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<List<Contact>> =
        queryService.getContactsByAccountId(provider.toDatabaseProvider(), id).toOkResponseEntity()

    @GetMapping("/accounts/{id}/opportunities")
    fun getOpportunitiesByAccountId(
        @PathVariable provider: String,
        @PathVariable id: UUID,
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
        @PathVariable id: UUID,
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
        @PathVariable id: UUID,
    ): ResponseEntity<Opportunity> =
        queryService.getOpportunityById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/opportunities/{id}/products")
    fun getProductsByOpportunityId(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<List<OpportunityProduct>> =
        queryService.getProductsByOpportunityId(provider.toDatabaseProvider(), id).toOkResponseEntity()

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
        @PathVariable id: UUID,
    ): ResponseEntity<Lead> =
        queryService.getLeadById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/incidents")
    fun getIncidents(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Incident>> =
        queryService
            .getIncidents(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/incidents/{id}")
    fun getIncidentById(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<Incident> =
        queryService.getIncidentById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/products")
    fun getProducts(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Product>> =
        queryService
            .getProducts(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/products/{id}")
    fun getProductById(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<Product> =
        queryService.getProductById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/price-levels")
    fun getPriceLevels(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<PriceLevel>> =
        queryService
            .getPriceLevels(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/price-levels/{id}")
    fun getPriceLevelById(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<PriceLevel> =
        queryService.getPriceLevelById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/opportunity-products")
    fun getOpportunityProducts(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<OpportunityProduct>> =
        queryService
            .getOpportunityProducts(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/opportunity-products/{id}")
    fun getOpportunityProductById(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<OpportunityProduct> =
        queryService.getOpportunityProductById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/business-units")
    fun getBusinessUnits(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<BusinessUnit>> =
        queryService
            .getBusinessUnits(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/business-units/{id}")
    fun getBusinessUnitById(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<BusinessUnit> =
        queryService.getBusinessUnitById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/system-users")
    fun getSystemUsers(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<SystemUser>> =
        queryService
            .getSystemUsers(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/system-users/{id}")
    fun getSystemUserById(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<SystemUser> =
        queryService.getSystemUserById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/activity-pointers")
    fun getActivityPointers(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<ActivityPointer>> =
        queryService
            .getActivityPointers(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/activity-pointers/{id}")
    fun getActivityPointerById(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<ActivityPointer> =
        queryService.getActivityPointerById(provider.toDatabaseProvider(), id).toResponseEntity()
}
