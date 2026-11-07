package codes.cy.crmapi.api.controllers

import codes.cy.crmapi.api.extensions.toCreatedResponseEntity
import codes.cy.crmapi.api.extensions.toNoContentResponseEntityOrNotFound
import codes.cy.crmapi.api.extensions.toOkResponseEntityOrNotFound
import codes.cy.crmapi.api.extensions.validated
import codes.cy.crmapi.application.interfaces.ISalesforceCommandService
import codes.cy.crmapi.domain.common.toDatabaseProvider
import codes.cy.crmapi.domain.salesforce.Account
import codes.cy.crmapi.domain.salesforce.AccountWriteRequest
import codes.cy.crmapi.domain.salesforce.Contact
import codes.cy.crmapi.domain.salesforce.ContactWriteRequest
import codes.cy.crmapi.domain.salesforce.Opportunity
import codes.cy.crmapi.domain.salesforce.OpportunityWriteRequest
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
 * Write endpoints (create / full replace / hard delete) for Salesforce. Authorization is enforced in `SecurityConfig`:
 * POST/PUT require role `admin` or `user`, DELETE requires `admin`.
 */
@RestController
@RequestMapping("/api/{provider:^(?:ms|pg)$}/v1/salesforce", produces = [MediaType.APPLICATION_JSON_VALUE])
class SalesforceWriteController(
    private val commandService: ISalesforceCommandService,
) {
    @PostMapping("/accounts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createAccount(
        @PathVariable provider: String,
        @RequestBody request: AccountWriteRequest,
    ): ResponseEntity<Account> =
        commandService
            .executeCreateAccount(provider.toDatabaseProvider(), request.validated())
            .let { created -> created.toCreatedResponseEntity(created.id) }

    @PutMapping("/accounts/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateAccount(
        @PathVariable provider: String,
        @PathVariable id: String,
        @RequestBody request: AccountWriteRequest,
    ): ResponseEntity<Account> =
        commandService
            .executeUpdateAccount(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("Salesforce Account", id)

    @DeleteMapping("/accounts/{id}")
    fun deleteAccount(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteAccount(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("Salesforce Account", id)

    @PostMapping("/contacts", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createContact(
        @PathVariable provider: String,
        @RequestBody request: ContactWriteRequest,
    ): ResponseEntity<Contact> =
        commandService
            .executeCreateContact(provider.toDatabaseProvider(), request.validated())
            .let { created -> created.toCreatedResponseEntity(created.id) }

    @PutMapping("/contacts/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateContact(
        @PathVariable provider: String,
        @PathVariable id: String,
        @RequestBody request: ContactWriteRequest,
    ): ResponseEntity<Contact> =
        commandService
            .executeUpdateContact(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("Salesforce Contact", id)

    @DeleteMapping("/contacts/{id}")
    fun deleteContact(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteContact(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("Salesforce Contact", id)

    @PostMapping("/opportunities", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createOpportunity(
        @PathVariable provider: String,
        @RequestBody request: OpportunityWriteRequest,
    ): ResponseEntity<Opportunity> =
        commandService
            .executeCreateOpportunity(provider.toDatabaseProvider(), request.validated())
            .let { created -> created.toCreatedResponseEntity(created.id) }

    @PutMapping("/opportunities/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateOpportunity(
        @PathVariable provider: String,
        @PathVariable id: String,
        @RequestBody request: OpportunityWriteRequest,
    ): ResponseEntity<Opportunity> =
        commandService
            .executeUpdateOpportunity(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("Salesforce Opportunity", id)

    @DeleteMapping("/opportunities/{id}")
    fun deleteOpportunity(
        @PathVariable provider: String,
        @PathVariable id: String,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteOpportunity(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("Salesforce Opportunity", id)
}
