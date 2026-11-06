package codes.cy.crmapi.application.services

import codes.cy.crmapi.application.interfaces.IHubSpotQueryService
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
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
import codes.cy.crmapi.infrastructure.repositories.IHubSpotRepository
import org.springframework.stereotype.Service

@Service
class HubSpotQueryService(
    private val repository: IHubSpotRepository,
) : IHubSpotQueryService {
    override fun getCompanies(provider: DatabaseProvider, options: QueryOptions): PagedResult<Company> =
        repository.getCompanies(provider, options)

    override fun getCompanyById(provider: DatabaseProvider, id: Long): Company? =
        repository.getCompanyById(provider, id)

    override fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact> =
        repository.getContacts(provider, options)

    override fun getContactById(provider: DatabaseProvider, id: Long): Contact? =
        repository.getContactById(provider, id)

    override fun getDeals(provider: DatabaseProvider, options: QueryOptions): PagedResult<Deal> =
        repository.getDeals(provider, options)

    override fun getDealsByCompanyId(provider: DatabaseProvider, companyId: Long, options: QueryOptions): PagedResult<Deal> =
        repository.getDealsByCompanyId(provider, companyId, options)

    override fun getDealById(provider: DatabaseProvider, id: Long): Deal? =
        repository.getDealById(provider, id)

    override fun getLineItemsByDealId(provider: DatabaseProvider, dealId: Long): List<LineItem> =
        repository.getLineItemsByDealId(provider, dealId)

    override fun getTickets(provider: DatabaseProvider, options: QueryOptions): PagedResult<Ticket> =
        repository.getTickets(provider, options)

    override fun getTicketById(provider: DatabaseProvider, id: Long): Ticket? =
        repository.getTicketById(provider, id)

    override fun getOwners(provider: DatabaseProvider, options: QueryOptions): PagedResult<Owner> =
        repository.getOwners(provider, options)

    override fun getOwnerById(provider: DatabaseProvider, id: Long): Owner? =
        repository.getOwnerById(provider, id)

    override fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product> =
        repository.getProducts(provider, options)

    override fun getProductById(provider: DatabaseProvider, id: Long): Product? =
        repository.getProductById(provider, id)

    override fun getLineItems(provider: DatabaseProvider, options: QueryOptions): PagedResult<LineItem> =
        repository.getLineItems(provider, options)

    override fun getLineItemById(provider: DatabaseProvider, id: Long): LineItem? =
        repository.getLineItemById(provider, id)

    override fun getPipelines(provider: DatabaseProvider, options: QueryOptions): PagedResult<Pipeline> =
        repository.getPipelines(provider, options)

    override fun getPipelineById(provider: DatabaseProvider, id: String): Pipeline? =
        repository.getPipelineById(provider, id)

    override fun getStagesByPipelineId(provider: DatabaseProvider, pipelineId: String): List<DealStage> =
        repository.getStagesByPipelineId(provider, pipelineId)

    override fun getDealStages(provider: DatabaseProvider, options: QueryOptions): PagedResult<DealStage> =
        repository.getDealStages(provider, options)

    override fun getDealStageById(provider: DatabaseProvider, id: String): DealStage? =
        repository.getDealStageById(provider, id)

    override fun getEngagements(provider: DatabaseProvider, options: QueryOptions): PagedResult<Engagement> =
        repository.getEngagements(provider, options)

    override fun getEngagementById(provider: DatabaseProvider, id: Long): Engagement? =
        repository.getEngagementById(provider, id)

    override fun getObjectAssociations(provider: DatabaseProvider, options: QueryOptions): PagedResult<ObjectAssociation> =
        repository.getObjectAssociations(provider, options)

    override fun getObjectAssociationById(provider: DatabaseProvider, id: Long): ObjectAssociation? =
        repository.getObjectAssociationById(provider, id)
}
