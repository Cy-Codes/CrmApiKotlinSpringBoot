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
import java.time.OffsetDateTime

interface IHubSpotRepository {
    fun getCompanies(provider: DatabaseProvider, options: QueryOptions): PagedResult<Company>
    fun getCompanyById(provider: DatabaseProvider, id: Long): Company?
    fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact>
    fun getContactById(provider: DatabaseProvider, id: Long): Contact?
    fun getDeals(provider: DatabaseProvider, options: QueryOptions): PagedResult<Deal>
    fun getDealsByCompanyId(provider: DatabaseProvider, companyId: Long, options: QueryOptions): PagedResult<Deal>
    fun getDealById(provider: DatabaseProvider, id: Long): Deal?
    fun getLineItemsByDealId(provider: DatabaseProvider, dealId: Long): List<LineItem>
    fun getTickets(provider: DatabaseProvider, options: QueryOptions): PagedResult<Ticket>
    fun getTicketById(provider: DatabaseProvider, id: Long): Ticket?
    fun getOwners(provider: DatabaseProvider, options: QueryOptions): PagedResult<Owner>
    fun getOwnerById(provider: DatabaseProvider, id: Long): Owner?
    fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product>
    fun getProductById(provider: DatabaseProvider, id: Long): Product?
    fun getLineItems(provider: DatabaseProvider, options: QueryOptions): PagedResult<LineItem>
    fun getLineItemById(provider: DatabaseProvider, id: Long): LineItem?
    fun getPipelines(provider: DatabaseProvider, options: QueryOptions): PagedResult<Pipeline>
    fun getPipelineById(provider: DatabaseProvider, id: String): Pipeline?
    fun getStagesByPipelineId(provider: DatabaseProvider, pipelineId: String): List<DealStage>
    fun getDealStages(provider: DatabaseProvider, options: QueryOptions): PagedResult<DealStage>
    fun getDealStageById(provider: DatabaseProvider, id: String): DealStage?
    fun getEngagements(provider: DatabaseProvider, options: QueryOptions): PagedResult<Engagement>
    fun getEngagementById(provider: DatabaseProvider, id: Long): Engagement?
    fun getObjectAssociations(provider: DatabaseProvider, options: QueryOptions): PagedResult<ObjectAssociation>
    fun getObjectAssociationById(provider: DatabaseProvider, id: Long): ObjectAssociation?

    fun executeCreateCompany(provider: DatabaseProvider, id: Long, request: CompanyWriteRequest, now: OffsetDateTime): Company
    fun executeUpdateCompany(provider: DatabaseProvider, id: Long, request: CompanyWriteRequest, now: OffsetDateTime): Company?
    fun executeDeleteCompany(provider: DatabaseProvider, id: Long): Boolean
    fun executeCreateContact(provider: DatabaseProvider, id: Long, request: ContactWriteRequest, now: OffsetDateTime): Contact
    fun executeUpdateContact(provider: DatabaseProvider, id: Long, request: ContactWriteRequest, now: OffsetDateTime): Contact?
    fun executeDeleteContact(provider: DatabaseProvider, id: Long): Boolean
    fun executeCreateDeal(provider: DatabaseProvider, id: Long, request: DealWriteRequest, now: OffsetDateTime): Deal
    fun executeUpdateDeal(provider: DatabaseProvider, id: Long, request: DealWriteRequest, now: OffsetDateTime): Deal?
    fun executeDeleteDeal(provider: DatabaseProvider, id: Long): Boolean
}
