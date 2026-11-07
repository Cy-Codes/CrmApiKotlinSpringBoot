package codes.cy.crmapi.api.controllers

import codes.cy.crmapi.api.extensions.toCreatedResponseEntity
import codes.cy.crmapi.api.extensions.toNoContentResponseEntityOrNotFound
import codes.cy.crmapi.api.extensions.toOkResponseEntityOrNotFound
import codes.cy.crmapi.api.extensions.validated
import codes.cy.crmapi.application.interfaces.IDynamics365CommandService
import codes.cy.crmapi.domain.common.toDatabaseProvider
import codes.cy.crmapi.domain.dynamics365.Account
import codes.cy.crmapi.domain.dynamics365.AccountWriteRequest
import codes.cy.crmapi.domain.dynamics365.Contact
import codes.cy.crmapi.domain.dynamics365.ContactWriteRequest
import codes.cy.crmapi.domain.dynamics365.Opportunity
import codes.cy.crmapi.domain.dynamics365.OpportunityWriteRequest
import java.util.UUID
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Write endpoints (create / full replace / hard delete) for Dynamics365. Authorization is enforced in `SecurityConfig`:
 * POST/PUT require role `admin` or `user`, DELETE requires `admin`.
 */
@RestController
@RequestMapping("/api/{provider:^(?:ms|pg)$}/v1/dynamics365", produces = [MediaType.APPLICATION_JSON_VALUE])
class Dynamics365WriteController(
    private val commandService: IDynamics365CommandService,
) {
    @PostMapping("/accounts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createAccount(
        @PathVariable provider: String,
        @RequestBody request: AccountWriteRequest,
    ): ResponseEntity<Account> =
        commandService
            .executeCreateAccount(provider.toDatabaseProvider(), request.validated())
            .let { created -> created.toCreatedResponseEntity(created.accountId) }

    @PutMapping("/accounts/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAccount(
        @PathVariable provider: String,
        @PathVariable id: UUID,
        @RequestBody request: AccountWriteRequest,
    ): ResponseEntity<Account> =
        commandService
            .executeUpdateAccount(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("Dynamics365 Account", id)

    @DeleteMapping("/accounts/{id}")
    fun deleteAccount(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteAccount(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("Dynamics365 Account", id)

    @PostMapping("/contacts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createContact(
        @PathVariable provider: String,
        @RequestBody request: ContactWriteRequest,
    ): ResponseEntity<Contact> =
        commandService
            .executeCreateContact(provider.toDatabaseProvider(), request.validated())
            .let { created -> created.toCreatedResponseEntity(created.contactId) }

    @PutMapping("/contacts/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateContact(
        @PathVariable provider: String,
        @PathVariable id: UUID,
        @RequestBody request: ContactWriteRequest,
    ): ResponseEntity<Contact> =
        commandService
            .executeUpdateContact(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("Dynamics365 Contact", id)

    @DeleteMapping("/contacts/{id}")
    fun deleteContact(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteContact(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("Dynamics365 Contact", id)

    @PostMapping("/opportunities", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createOpportunity(
        @PathVariable provider: String,
        @RequestBody request: OpportunityWriteRequest,
    ): ResponseEntity<Opportunity> =
        commandService
            .executeCreateOpportunity(provider.toDatabaseProvider(), request.validated())
            .let { created -> created.toCreatedResponseEntity(created.opportunityId) }

    @PutMapping("/opportunities/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateOpportunity(
        @PathVariable provider: String,
        @PathVariable id: UUID,
        @RequestBody request: OpportunityWriteRequest,
    ): ResponseEntity<Opportunity> =
        commandService
            .executeUpdateOpportunity(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("Dynamics365 Opportunity", id)

    @DeleteMapping("/opportunities/{id}")
    fun deleteOpportunity(
        @PathVariable provider: String,
        @PathVariable id: UUID,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteOpportunity(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("Dynamics365 Opportunity", id)
}
