package codes.cy.crmapi.application.services

import codes.cy.crmapi.application.interfaces.IIdGenerator
import codes.cy.crmapi.application.interfaces.ISalesforceCommandService
import codes.cy.crmapi.application.interfaces.SalesforceIdPrefix
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.salesforce.Account
import codes.cy.crmapi.domain.salesforce.AccountWriteRequest
import codes.cy.crmapi.domain.salesforce.Contact
import codes.cy.crmapi.domain.salesforce.ContactWriteRequest
import codes.cy.crmapi.domain.salesforce.Opportunity
import codes.cy.crmapi.domain.salesforce.OpportunityWriteRequest
import codes.cy.crmapi.infrastructure.repositories.ISalesforceRepository
import java.time.Clock
import org.springframework.stereotype.Service

@Service
class SalesforceCommandService(
    private val repository: ISalesforceRepository,
    private val idGenerator: IIdGenerator,
    private val clock: Clock,
) : ISalesforceCommandService {
    override fun executeCreateAccount(provider: DatabaseProvider, request: AccountWriteRequest): Account =
        repository.executeCreateAccount(provider, idGenerator.executeNextSalesforceId(SalesforceIdPrefix.Account), request, clock.getUtcNow())

    override fun executeUpdateAccount(provider: DatabaseProvider, id: String, request: AccountWriteRequest): Account? =
        repository.executeUpdateAccount(provider, id, request, clock.getUtcNow())

    override fun executeDeleteAccount(provider: DatabaseProvider, id: String): Boolean =
        repository.executeDeleteAccount(provider, id)

    override fun executeCreateContact(provider: DatabaseProvider, request: ContactWriteRequest): Contact =
        repository.executeCreateContact(provider, idGenerator.executeNextSalesforceId(SalesforceIdPrefix.Contact), request, clock.getUtcNow())

    override fun executeUpdateContact(provider: DatabaseProvider, id: String, request: ContactWriteRequest): Contact? =
        repository.executeUpdateContact(provider, id, request, clock.getUtcNow())

    override fun executeDeleteContact(provider: DatabaseProvider, id: String): Boolean =
        repository.executeDeleteContact(provider, id)

    override fun executeCreateOpportunity(provider: DatabaseProvider, request: OpportunityWriteRequest): Opportunity =
        repository.executeCreateOpportunity(provider, idGenerator.executeNextSalesforceId(SalesforceIdPrefix.Opportunity), request, clock.getUtcNow())

    override fun executeUpdateOpportunity(provider: DatabaseProvider, id: String, request: OpportunityWriteRequest): Opportunity? =
        repository.executeUpdateOpportunity(provider, id, request, clock.getUtcNow())

    override fun executeDeleteOpportunity(provider: DatabaseProvider, id: String): Boolean =
        repository.executeDeleteOpportunity(provider, id)
}
