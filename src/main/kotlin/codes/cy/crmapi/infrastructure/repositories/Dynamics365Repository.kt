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
import org.springframework.stereotype.Repository

@Repository
class Dynamics365Repository(
    private val baseRepository: IBaseRepository,
) : IDynamics365Repository {
    override fun getAccounts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Account> =
        baseRepository.getPaged(provider, "Dynamics365.Account", options, Account::class, "accountid")

    override fun getAccountById(provider: DatabaseProvider, id: UUID): Account? =
        baseRepository.getById(provider, "Dynamics365.Account", "accountid", id, Account::class)

    override fun getContactsByAccountId(provider: DatabaseProvider, accountId: UUID): List<Contact> =
        baseRepository.getList(provider, "Dynamics365.Contact", Contact::class) { q -> q.where("parentcustomerid", accountId) }

    override fun getOpportunitiesByAccountId(provider: DatabaseProvider, accountId: UUID): List<Opportunity> =
        baseRepository.getList(provider, "Dynamics365.Opportunity", Opportunity::class) { q -> q.where("parentaccountid", accountId) }

    override fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact> =
        baseRepository.getPaged(provider, "Dynamics365.Contact", options, Contact::class, "contactid")

    override fun getContactById(provider: DatabaseProvider, id: UUID): Contact? =
        baseRepository.getById(provider, "Dynamics365.Contact", "contactid", id, Contact::class)

    override fun getOpportunities(provider: DatabaseProvider, options: QueryOptions): PagedResult<Opportunity> =
        baseRepository.getPaged(provider, "Dynamics365.Opportunity", options, Opportunity::class, "opportunityid")

    override fun getOpportunityById(provider: DatabaseProvider, id: UUID): Opportunity? =
        baseRepository.getById(provider, "Dynamics365.Opportunity", "opportunityid", id, Opportunity::class)

    override fun getProductsByOpportunityId(provider: DatabaseProvider, opportunityId: UUID): List<OpportunityProduct> =
        baseRepository.getList(provider, "Dynamics365.OpportunityProduct", OpportunityProduct::class) { q -> q.where("opportunityid", opportunityId) }

    override fun getLeads(provider: DatabaseProvider, options: QueryOptions): PagedResult<Lead> =
        baseRepository.getPaged(provider, "Dynamics365.Lead", options, Lead::class, "leadid")

    override fun getLeadById(provider: DatabaseProvider, id: UUID): Lead? =
        baseRepository.getById(provider, "Dynamics365.Lead", "leadid", id, Lead::class)

    override fun getIncidents(provider: DatabaseProvider, options: QueryOptions): PagedResult<Incident> =
        baseRepository.getPaged(provider, "Dynamics365.Incident", options, Incident::class, "incidentid")

    override fun getIncidentById(provider: DatabaseProvider, id: UUID): Incident? =
        baseRepository.getById(provider, "Dynamics365.Incident", "incidentid", id, Incident::class)

    override fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product> =
        baseRepository.getPaged(provider, "Dynamics365.Product", options, Product::class, "productid")

    override fun getProductById(provider: DatabaseProvider, id: UUID): Product? =
        baseRepository.getById(provider, "Dynamics365.Product", "productid", id, Product::class)

    override fun getPriceLevels(provider: DatabaseProvider, options: QueryOptions): PagedResult<PriceLevel> =
        baseRepository.getPaged(provider, "Dynamics365.PriceLevel", options, PriceLevel::class, "pricelevelid")

    override fun getPriceLevelById(provider: DatabaseProvider, id: UUID): PriceLevel? =
        baseRepository.getById(provider, "Dynamics365.PriceLevel", "pricelevelid", id, PriceLevel::class)

    override fun getOpportunityProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<OpportunityProduct> =
        baseRepository.getPaged(provider, "Dynamics365.OpportunityProduct", options, OpportunityProduct::class, "opportunityproductid")

    override fun getOpportunityProductById(provider: DatabaseProvider, id: UUID): OpportunityProduct? =
        baseRepository.getById(provider, "Dynamics365.OpportunityProduct", "opportunityproductid", id, OpportunityProduct::class)

    override fun getBusinessUnits(provider: DatabaseProvider, options: QueryOptions): PagedResult<BusinessUnit> =
        baseRepository.getPaged(provider, "Dynamics365.BusinessUnit", options, BusinessUnit::class, "businessunitid")

    override fun getBusinessUnitById(provider: DatabaseProvider, id: UUID): BusinessUnit? =
        baseRepository.getById(provider, "Dynamics365.BusinessUnit", "businessunitid", id, BusinessUnit::class)

    override fun getSystemUsers(provider: DatabaseProvider, options: QueryOptions): PagedResult<SystemUser> =
        baseRepository.getPaged(provider, "Dynamics365.SystemUser", options, SystemUser::class, "systemuserid")

    override fun getSystemUserById(provider: DatabaseProvider, id: UUID): SystemUser? =
        baseRepository.getById(provider, "Dynamics365.SystemUser", "systemuserid", id, SystemUser::class)

    override fun getActivityPointers(provider: DatabaseProvider, options: QueryOptions): PagedResult<ActivityPointer> =
        baseRepository.getPaged(provider, "Dynamics365.ActivityPointer", options, ActivityPointer::class, "activityid")

    override fun getActivityPointerById(provider: DatabaseProvider, id: UUID): ActivityPointer? =
        baseRepository.getById(provider, "Dynamics365.ActivityPointer", "activityid", id, ActivityPointer::class)

    override fun executeCreateAccount(provider: DatabaseProvider, id: UUID, request: AccountWriteRequest, now: OffsetDateTime): Account =
        baseRepository.executeCreate(provider, DYNAMICS365_ACCOUNT_TABLE, DYNAMICS365_ACCOUNT_ID_COLUMN, id, request.toInsertColumns(id, now), Account::class)

    override fun executeUpdateAccount(provider: DatabaseProvider, id: UUID, request: AccountWriteRequest, now: OffsetDateTime): Account? =
        baseRepository.executeUpdate(provider, DYNAMICS365_ACCOUNT_TABLE, DYNAMICS365_ACCOUNT_ID_COLUMN, id, request.toUpdateColumns(now), Account::class)

    override fun executeDeleteAccount(provider: DatabaseProvider, id: UUID): Boolean =
        baseRepository.executeDelete(provider, DYNAMICS365_ACCOUNT_TABLE, DYNAMICS365_ACCOUNT_ID_COLUMN, id)

    override fun executeCreateContact(provider: DatabaseProvider, id: UUID, request: ContactWriteRequest, now: OffsetDateTime): Contact =
        baseRepository.executeCreate(provider, DYNAMICS365_CONTACT_TABLE, DYNAMICS365_CONTACT_ID_COLUMN, id, request.toInsertColumns(id, now), Contact::class)

    override fun executeUpdateContact(provider: DatabaseProvider, id: UUID, request: ContactWriteRequest, now: OffsetDateTime): Contact? =
        baseRepository.executeUpdate(provider, DYNAMICS365_CONTACT_TABLE, DYNAMICS365_CONTACT_ID_COLUMN, id, request.toUpdateColumns(now), Contact::class)

    override fun executeDeleteContact(provider: DatabaseProvider, id: UUID): Boolean =
        baseRepository.executeDelete(provider, DYNAMICS365_CONTACT_TABLE, DYNAMICS365_CONTACT_ID_COLUMN, id)

    override fun executeCreateOpportunity(provider: DatabaseProvider, id: UUID, request: OpportunityWriteRequest, now: OffsetDateTime): Opportunity =
        baseRepository.executeCreate(provider, DYNAMICS365_OPPORTUNITY_TABLE, DYNAMICS365_OPPORTUNITY_ID_COLUMN, id, request.toInsertColumns(id, now), Opportunity::class)

    override fun executeUpdateOpportunity(provider: DatabaseProvider, id: UUID, request: OpportunityWriteRequest, now: OffsetDateTime): Opportunity? =
        baseRepository.executeUpdate(provider, DYNAMICS365_OPPORTUNITY_TABLE, DYNAMICS365_OPPORTUNITY_ID_COLUMN, id, request.toUpdateColumns(now), Opportunity::class)

    override fun executeDeleteOpportunity(provider: DatabaseProvider, id: UUID): Boolean =
        baseRepository.executeDelete(provider, DYNAMICS365_OPPORTUNITY_TABLE, DYNAMICS365_OPPORTUNITY_ID_COLUMN, id)
}
