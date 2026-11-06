package codes.cy.crmapi.api.controllers

import codes.cy.crmapi.api.extensions.toOkResponseEntity
import codes.cy.crmapi.api.extensions.toResponseEntity
import codes.cy.crmapi.application.interfaces.IHubSpotQueryService
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.common.toDatabaseProvider
import codes.cy.crmapi.domain.hubspot.Company
import codes.cy.crmapi.domain.hubspot.Contact
import codes.cy.crmapi.domain.hubspot.Deal
import codes.cy.crmapi.domain.hubspot.DealStage
import codes.cy.crmapi.domain.hubspot.Engagement
import codes.cy.crmapi.domain.hubspot.LineItem
import codes.cy.crmapi.domain.hubspot.ObjectAssociation
import codes.cy.crmapi.domain.hubspot.Owner
import codes.cy.crmapi.domain.hubspot.Pipeline
import codes.cy.crmapi.domain.hubspot.Product
import codes.cy.crmapi.domain.hubspot.Ticket
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/{provider:^(?:ms|pg)$}/v1/hubspot", produces = [MediaType.APPLICATION_JSON_VALUE])
class HubSpotController(
    private val queryService: IHubSpotQueryService,
) : BaseCrmController() {
    companion object {
        private const val ASSOCIATED_COMPANY_ID_FILTER_KEY = "associatedCompanyId"
    }

    @GetMapping("/companies")
    fun getCompanies(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Company>> =
        queryService
            .getCompanies(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/companies/{id:\\d+}")
    fun getCompanyById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Company> =
        queryService.getCompanyById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/companies/{id:\\d+}/contacts")
    fun getContactsByCompanyId(
        @PathVariable provider: String,
        @PathVariable id: Long,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
    ): ResponseEntity<PagedResult<Contact>> {
        val filters = mapOf(ASSOCIATED_COMPANY_ID_FILTER_KEY to id.toString())
        val options = QueryOptions(page, pageSize, null, QueryOptions.DEFAULT_SORT_DIRECTION, filters)
        return queryService.getContacts(provider.toDatabaseProvider(), options).toOkResponseEntity()
    }

    @GetMapping("/companies/{id:\\d+}/deals")
    fun getDealsByCompanyId(
        @PathVariable provider: String,
        @PathVariable id: Long,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
    ): ResponseEntity<PagedResult<Deal>> =
        queryService
            .getDealsByCompanyId(
                provider.toDatabaseProvider(),
                id,
                QueryOptions(page, pageSize, null, QueryOptions.DEFAULT_SORT_DIRECTION),
            )
            .toOkResponseEntity()

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

    @GetMapping("/contacts/{id:\\d+}")
    fun getContactById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Contact> =
        queryService.getContactById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/deals")
    fun getDeals(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Deal>> =
        queryService
            .getDeals(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/deals/{id:\\d+}")
    fun getDealById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Deal> =
        queryService.getDealById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/deals/{id:\\d+}/line-items")
    fun getLineItemsByDealId(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<List<LineItem>> =
        queryService.getLineItemsByDealId(provider.toDatabaseProvider(), id).toOkResponseEntity()

    @GetMapping("/tickets")
    fun getTickets(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Ticket>> =
        queryService
            .getTickets(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/tickets/{id:\\d+}")
    fun getTicketById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Ticket> =
        queryService.getTicketById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/owners")
    fun getOwners(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Owner>> =
        queryService
            .getOwners(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/owners/{id:\\d+}")
    fun getOwnerById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Owner> =
        queryService.getOwnerById(provider.toDatabaseProvider(), id).toResponseEntity()

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

    @GetMapping("/products/{id:\\d+}")
    fun getProductById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Product> =
        queryService.getProductById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/line-items")
    fun getLineItems(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<LineItem>> =
        queryService
            .getLineItems(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/line-items/{id:\\d+}")
    fun getLineItemById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<LineItem> =
        queryService.getLineItemById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/pipelines")
    fun getPipelines(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Pipeline>> =
        queryService
            .getPipelines(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/pipelines/{id}")
    fun getPipelineById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Pipeline> =
        queryService.getPipelineById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/pipelines/{id}/stages")
    fun getStagesByPipelineId(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<List<DealStage>> =
        queryService.getStagesByPipelineId(provider.toDatabaseProvider(), id).toOkResponseEntity()

    @GetMapping("/deal-stages")
    fun getDealStages(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<DealStage>> =
        queryService
            .getDealStages(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/deal-stages/{id}")
    fun getDealStageById(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<DealStage> =
        queryService.getDealStageById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/engagements")
    fun getEngagements(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<Engagement>> =
        queryService
            .getEngagements(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/engagements/{id:\\d+}")
    fun getEngagementById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Engagement> =
        queryService.getEngagementById(provider.toDatabaseProvider(), id).toResponseEntity()

    @GetMapping("/object-associations")
    fun getObjectAssociations(
        @PathVariable provider: String,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE}") page: Int,
        @RequestParam(defaultValue = "${QueryOptions.DEFAULT_PAGE_SIZE}") pageSize: Int,
        @RequestParam(required = false) sortBy: String?,
        @RequestParam(defaultValue = QueryOptions.DEFAULT_SORT_DIRECTION) sortDirection: String,
        @RequestParam allParams: Map<String, String>,
    ): ResponseEntity<PagedResult<ObjectAssociation>> =
        queryService
            .getObjectAssociations(provider.toDatabaseProvider(), createQueryOptions(allParams, page, pageSize, sortBy, sortDirection))
            .toOkResponseEntity()

    @GetMapping("/object-associations/{id:\\d+}")
    fun getObjectAssociationById(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<ObjectAssociation> =
        queryService.getObjectAssociationById(provider.toDatabaseProvider(), id).toResponseEntity()
}
