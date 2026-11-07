package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.hubspot.Company
import codes.cy.crmapi.domain.hubspot.CompanyWriteRequest
import codes.cy.crmapi.domain.hubspot.Contact
import codes.cy.crmapi.domain.hubspot.ContactWriteRequest
import codes.cy.crmapi.domain.hubspot.Deal
import codes.cy.crmapi.domain.hubspot.DealStage
import codes.cy.crmapi.domain.hubspot.DealWriteRequest
import codes.cy.crmapi.domain.hubspot.Engagement
import codes.cy.crmapi.domain.hubspot.LineItem
import codes.cy.crmapi.domain.hubspot.ObjectAssociation
import codes.cy.crmapi.domain.hubspot.Owner
import codes.cy.crmapi.domain.hubspot.Pipeline
import codes.cy.crmapi.domain.hubspot.Product
import codes.cy.crmapi.domain.hubspot.Ticket
import codes.cy.crmapi.infrastructure.compilers.SqlQuery
import java.time.OffsetDateTime
import org.springframework.stereotype.Repository

@Repository
class HubSpotRepository(
    private val baseRepository: IBaseRepository,
) : IHubSpotRepository {
    override fun getCompanies(provider: DatabaseProvider, options: QueryOptions): PagedResult<Company> =
        baseRepository.getPaged(provider, "HubSpot.Company", options, Company::class, "companyId")

    override fun getCompanyById(provider: DatabaseProvider, id: Long): Company? =
        baseRepository.getById(provider, "HubSpot.Company", "companyId", id, Company::class)

    override fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact> =
        baseRepository.getPaged(provider, "HubSpot.Contact", options, Contact::class, "contactId")

    override fun getContactById(provider: DatabaseProvider, id: Long): Contact? =
        baseRepository.getById(provider, "HubSpot.Contact", "contactId", id, Contact::class)

    override fun getDeals(provider: DatabaseProvider, options: QueryOptions): PagedResult<Deal> =
        baseRepository.getPaged(provider, "HubSpot.Deal", options, Deal::class, "dealId")

    override fun getDealsByCompanyId(provider: DatabaseProvider, companyId: Long, options: QueryOptions): PagedResult<Deal> =
        baseRepository.getPaged(
            provider,
            "HubSpot.Deal",
            options,
            Deal::class,
            "dealId",
        ) { q ->
            q.whereIn(
                "dealId",
                SqlQuery("HubSpot.ObjectAssociation")
                    .select("fromObjectId")
                    .where("fromObjectType", "deal")
                    .where("toObjectType", "company")
                    .where("toObjectId", companyId),
            )
        }

    override fun getDealById(provider: DatabaseProvider, id: Long): Deal? =
        baseRepository.getById(provider, "HubSpot.Deal", "dealId", id, Deal::class)

    override fun getLineItemsByDealId(provider: DatabaseProvider, dealId: Long): List<LineItem> =
        baseRepository.getList(provider, "HubSpot.LineItem", LineItem::class) { q -> q.where("dealId", dealId) }

    override fun getTickets(provider: DatabaseProvider, options: QueryOptions): PagedResult<Ticket> =
        baseRepository.getPaged(provider, "HubSpot.Ticket", options, Ticket::class, "ticketId")

    override fun getTicketById(provider: DatabaseProvider, id: Long): Ticket? =
        baseRepository.getById(provider, "HubSpot.Ticket", "ticketId", id, Ticket::class)

    override fun getOwners(provider: DatabaseProvider, options: QueryOptions): PagedResult<Owner> =
        baseRepository.getPaged(provider, "HubSpot.Owner", options, Owner::class, "ownerId")

    override fun getOwnerById(provider: DatabaseProvider, id: Long): Owner? =
        baseRepository.getById(provider, "HubSpot.Owner", "ownerId", id, Owner::class)

    override fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product> =
        baseRepository.getPaged(provider, "HubSpot.Product", options, Product::class, "productId")

    override fun getProductById(provider: DatabaseProvider, id: Long): Product? =
        baseRepository.getById(provider, "HubSpot.Product", "productId", id, Product::class)

    override fun getLineItems(provider: DatabaseProvider, options: QueryOptions): PagedResult<LineItem> =
        baseRepository.getPaged(provider, "HubSpot.LineItem", options, LineItem::class, "lineItemId")

    override fun getLineItemById(provider: DatabaseProvider, id: Long): LineItem? =
        baseRepository.getById(provider, "HubSpot.LineItem", "lineItemId", id, LineItem::class)

    override fun getPipelines(provider: DatabaseProvider, options: QueryOptions): PagedResult<Pipeline> =
        baseRepository.getPaged(provider, "HubSpot.Pipeline", options, Pipeline::class, "pipelineId")

    override fun getPipelineById(provider: DatabaseProvider, id: String): Pipeline? =
        baseRepository.getById(provider, "HubSpot.Pipeline", "pipelineId", id, Pipeline::class)

    override fun getStagesByPipelineId(provider: DatabaseProvider, pipelineId: String): List<DealStage> =
        baseRepository.getList(provider, "HubSpot.DealStage", DealStage::class) { q -> q.where("pipelineId", pipelineId) }

    override fun getDealStages(provider: DatabaseProvider, options: QueryOptions): PagedResult<DealStage> =
        baseRepository.getPaged(provider, "HubSpot.DealStage", options, DealStage::class, "stageId")

    override fun getDealStageById(provider: DatabaseProvider, id: String): DealStage? =
        baseRepository.getById(provider, "HubSpot.DealStage", "stageId", id, DealStage::class)

    override fun getEngagements(provider: DatabaseProvider, options: QueryOptions): PagedResult<Engagement> =
        baseRepository.getPaged(provider, "HubSpot.Engagement", options, Engagement::class, "engagementId")

    override fun getEngagementById(provider: DatabaseProvider, id: Long): Engagement? =
        baseRepository.getById(provider, "HubSpot.Engagement", "engagementId", id, Engagement::class)

    override fun getObjectAssociations(provider: DatabaseProvider, options: QueryOptions): PagedResult<ObjectAssociation> =
        baseRepository.getPaged(provider, "HubSpot.ObjectAssociation", options, ObjectAssociation::class, "associationId")

    override fun getObjectAssociationById(provider: DatabaseProvider, id: Long): ObjectAssociation? =
        baseRepository.getById(provider, "HubSpot.ObjectAssociation", "associationId", id, ObjectAssociation::class)

    override fun executeCreateCompany(provider: DatabaseProvider, id: Long, request: CompanyWriteRequest, now: OffsetDateTime): Company =
        baseRepository.executeCreate(provider, HUBSPOT_COMPANY_TABLE, HUBSPOT_COMPANY_ID_COLUMN, id, request.toInsertColumns(id, now), Company::class)

    override fun executeUpdateCompany(provider: DatabaseProvider, id: Long, request: CompanyWriteRequest, now: OffsetDateTime): Company? =
        baseRepository.executeUpdate(provider, HUBSPOT_COMPANY_TABLE, HUBSPOT_COMPANY_ID_COLUMN, id, request.toUpdateColumns(now), Company::class)

    override fun executeDeleteCompany(provider: DatabaseProvider, id: Long): Boolean =
        baseRepository.executeDelete(provider, HUBSPOT_COMPANY_TABLE, HUBSPOT_COMPANY_ID_COLUMN, id)

    override fun executeCreateContact(provider: DatabaseProvider, id: Long, request: ContactWriteRequest, now: OffsetDateTime): Contact =
        baseRepository.executeCreate(provider, HUBSPOT_CONTACT_TABLE, HUBSPOT_CONTACT_ID_COLUMN, id, request.toInsertColumns(id, now), Contact::class)

    override fun executeUpdateContact(provider: DatabaseProvider, id: Long, request: ContactWriteRequest, now: OffsetDateTime): Contact? =
        baseRepository.executeUpdate(provider, HUBSPOT_CONTACT_TABLE, HUBSPOT_CONTACT_ID_COLUMN, id, request.toUpdateColumns(now), Contact::class)

    override fun executeDeleteContact(provider: DatabaseProvider, id: Long): Boolean =
        baseRepository.executeDelete(provider, HUBSPOT_CONTACT_TABLE, HUBSPOT_CONTACT_ID_COLUMN, id)

    override fun executeCreateDeal(provider: DatabaseProvider, id: Long, request: DealWriteRequest, now: OffsetDateTime): Deal =
        baseRepository.executeCreate(provider, HUBSPOT_DEAL_TABLE, HUBSPOT_DEAL_ID_COLUMN, id, request.toInsertColumns(id, now), Deal::class)

    override fun executeUpdateDeal(provider: DatabaseProvider, id: Long, request: DealWriteRequest, now: OffsetDateTime): Deal? =
        baseRepository.executeUpdate(provider, HUBSPOT_DEAL_TABLE, HUBSPOT_DEAL_ID_COLUMN, id, request.toUpdateColumns(now), Deal::class)

    override fun executeDeleteDeal(provider: DatabaseProvider, id: Long): Boolean =
        baseRepository.executeDelete(provider, HUBSPOT_DEAL_TABLE, HUBSPOT_DEAL_ID_COLUMN, id)
}
