package codes.cy.crmapi.application.services

import codes.cy.crmapi.application.interfaces.IDynamics365QueryService
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.PagedResult
import codes.cy.crmapi.domain.common.QueryOptions
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
import codes.cy.crmapi.infrastructure.repositories.IDynamics365Repository
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class Dynamics365QueryService(
    private val repository: IDynamics365Repository,
) : IDynamics365QueryService {
    override fun getAccounts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Account> =
        repository.getAccounts(provider, options)

    override fun getAccountById(provider: DatabaseProvider, id: UUID): Account? =
        repository.getAccountById(provider, id)

    override fun getContactsByAccountId(provider: DatabaseProvider, accountId: UUID): List<Contact> =
        repository.getContactsByAccountId(provider, accountId)

    override fun getOpportunitiesByAccountId(provider: DatabaseProvider, accountId: UUID): List<Opportunity> =
        repository.getOpportunitiesByAccountId(provider, accountId)

    override fun getContacts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Contact> =
        repository.getContacts(provider, options)

    override fun getContactById(provider: DatabaseProvider, id: UUID): Contact? =
        repository.getContactById(provider, id)

    override fun getOpportunities(provider: DatabaseProvider, options: QueryOptions): PagedResult<Opportunity> =
        repository.getOpportunities(provider, options)

    override fun getOpportunityById(provider: DatabaseProvider, id: UUID): Opportunity? =
        repository.getOpportunityById(provider, id)

    override fun getProductsByOpportunityId(provider: DatabaseProvider, opportunityId: UUID): List<OpportunityProduct> =
        repository.getProductsByOpportunityId(provider, opportunityId)

    override fun getLeads(provider: DatabaseProvider, options: QueryOptions): PagedResult<Lead> =
        repository.getLeads(provider, options)

    override fun getLeadById(provider: DatabaseProvider, id: UUID): Lead? =
        repository.getLeadById(provider, id)

    override fun getIncidents(provider: DatabaseProvider, options: QueryOptions): PagedResult<Incident> =
        repository.getIncidents(provider, options)

    override fun getIncidentById(provider: DatabaseProvider, id: UUID): Incident? =
        repository.getIncidentById(provider, id)

    override fun getProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<Product> =
        repository.getProducts(provider, options)

    override fun getProductById(provider: DatabaseProvider, id: UUID): Product? =
        repository.getProductById(provider, id)

    override fun getPriceLevels(provider: DatabaseProvider, options: QueryOptions): PagedResult<PriceLevel> =
        repository.getPriceLevels(provider, options)

    override fun getPriceLevelById(provider: DatabaseProvider, id: UUID): PriceLevel? =
        repository.getPriceLevelById(provider, id)

    override fun getOpportunityProducts(provider: DatabaseProvider, options: QueryOptions): PagedResult<OpportunityProduct> =
        repository.getOpportunityProducts(provider, options)

    override fun getOpportunityProductById(provider: DatabaseProvider, id: UUID): OpportunityProduct? =
        repository.getOpportunityProductById(provider, id)

    override fun getBusinessUnits(provider: DatabaseProvider, options: QueryOptions): PagedResult<BusinessUnit> =
        repository.getBusinessUnits(provider, options)

    override fun getBusinessUnitById(provider: DatabaseProvider, id: UUID): BusinessUnit? =
        repository.getBusinessUnitById(provider, id)

    override fun getSystemUsers(provider: DatabaseProvider, options: QueryOptions): PagedResult<SystemUser> =
        repository.getSystemUsers(provider, options)

    override fun getSystemUserById(provider: DatabaseProvider, id: UUID): SystemUser? =
        repository.getSystemUserById(provider, id)

    override fun getActivityPointers(provider: DatabaseProvider, options: QueryOptions): PagedResult<ActivityPointer> =
        repository.getActivityPointers(provider, options)

    override fun getActivityPointerById(provider: DatabaseProvider, id: UUID): ActivityPointer? =
        repository.getActivityPointerById(provider, id)
}
