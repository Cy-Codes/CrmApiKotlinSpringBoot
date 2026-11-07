package codes.cy.crmapi.application.services

import codes.cy.crmapi.application.interfaces.IHubSpotCommandService
import codes.cy.crmapi.application.interfaces.IIdGenerator
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.hubspot.Company
import codes.cy.crmapi.domain.hubspot.CompanyWriteRequest
import codes.cy.crmapi.domain.hubspot.Contact
import codes.cy.crmapi.domain.hubspot.ContactWriteRequest
import codes.cy.crmapi.domain.hubspot.Deal
import codes.cy.crmapi.domain.hubspot.DealWriteRequest
import codes.cy.crmapi.infrastructure.repositories.IHubSpotRepository
import java.time.Clock
import org.springframework.stereotype.Service

@Service
class HubSpotCommandService(
    private val repository: IHubSpotRepository,
    private val idGenerator: IIdGenerator,
    private val clock: Clock,
) : IHubSpotCommandService {
    override fun executeCreateCompany(provider: DatabaseProvider, request: CompanyWriteRequest): Company =
        repository.executeCreateCompany(provider, idGenerator.executeNextHubSpotId(), request, clock.getUtcNow())

    override fun executeUpdateCompany(provider: DatabaseProvider, id: Long, request: CompanyWriteRequest): Company? =
        repository.executeUpdateCompany(provider, id, request, clock.getUtcNow())

    override fun executeDeleteCompany(provider: DatabaseProvider, id: Long): Boolean =
        repository.executeDeleteCompany(provider, id)

    override fun executeCreateContact(provider: DatabaseProvider, request: ContactWriteRequest): Contact =
        repository.executeCreateContact(provider, idGenerator.executeNextHubSpotId(), request, clock.getUtcNow())

    override fun executeUpdateContact(provider: DatabaseProvider, id: Long, request: ContactWriteRequest): Contact? =
        repository.executeUpdateContact(provider, id, request, clock.getUtcNow())

    override fun executeDeleteContact(provider: DatabaseProvider, id: Long): Boolean =
        repository.executeDeleteContact(provider, id)

    override fun executeCreateDeal(provider: DatabaseProvider, request: DealWriteRequest): Deal =
        repository.executeCreateDeal(provider, idGenerator.executeNextHubSpotId(), request, clock.getUtcNow())

    override fun executeUpdateDeal(provider: DatabaseProvider, id: Long, request: DealWriteRequest): Deal? =
        repository.executeUpdateDeal(provider, id, request, clock.getUtcNow())

    override fun executeDeleteDeal(provider: DatabaseProvider, id: Long): Boolean =
        repository.executeDeleteDeal(provider, id)
}
