package codes.cy.crmapi.application.services

import codes.cy.crmapi.application.interfaces.IDynamics365CommandService
import codes.cy.crmapi.application.interfaces.IIdGenerator
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.dynamics365.Account
import codes.cy.crmapi.domain.dynamics365.AccountWriteRequest
import codes.cy.crmapi.domain.dynamics365.Contact
import codes.cy.crmapi.domain.dynamics365.ContactWriteRequest
import codes.cy.crmapi.domain.dynamics365.Opportunity
import codes.cy.crmapi.domain.dynamics365.OpportunityWriteRequest
import codes.cy.crmapi.infrastructure.repositories.IDynamics365Repository
import java.time.Clock
import java.util.UUID
import org.springframework.stereotype.Service

@Service
class Dynamics365CommandService(
    private val repository: IDynamics365Repository,
    private val idGenerator: IIdGenerator,
    private val clock: Clock,
) : IDynamics365CommandService {
    override fun executeCreateAccount(provider: DatabaseProvider, request: AccountWriteRequest): Account =
        repository.executeCreateAccount(provider, idGenerator.executeNextUuid(), request, clock.getUtcNow())

    override fun executeUpdateAccount(provider: DatabaseProvider, id: UUID, request: AccountWriteRequest): Account? =
        repository.executeUpdateAccount(provider, id, request, clock.getUtcNow())

    override fun executeDeleteAccount(provider: DatabaseProvider, id: UUID): Boolean =
        repository.executeDeleteAccount(provider, id)

    override fun executeCreateContact(provider: DatabaseProvider, request: ContactWriteRequest): Contact =
        repository.executeCreateContact(provider, idGenerator.executeNextUuid(), request, clock.getUtcNow())

    override fun executeUpdateContact(provider: DatabaseProvider, id: UUID, request: ContactWriteRequest): Contact? =
        repository.executeUpdateContact(provider, id, request, clock.getUtcNow())

    override fun executeDeleteContact(provider: DatabaseProvider, id: UUID): Boolean =
        repository.executeDeleteContact(provider, id)

    override fun executeCreateOpportunity(provider: DatabaseProvider, request: OpportunityWriteRequest): Opportunity =
        repository.executeCreateOpportunity(provider, idGenerator.executeNextUuid(), request, clock.getUtcNow())

    override fun executeUpdateOpportunity(provider: DatabaseProvider, id: UUID, request: OpportunityWriteRequest): Opportunity? =
        repository.executeUpdateOpportunity(provider, id, request, clock.getUtcNow())

    override fun executeDeleteOpportunity(provider: DatabaseProvider, id: UUID): Boolean =
        repository.executeDeleteOpportunity(provider, id)
}
