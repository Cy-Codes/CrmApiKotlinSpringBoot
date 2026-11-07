package codes.cy.crmapi.api.controllers

import codes.cy.crmapi.api.extensions.toCreatedResponseEntity
import codes.cy.crmapi.api.extensions.toNoContentResponseEntityOrNotFound
import codes.cy.crmapi.api.extensions.toOkResponseEntityOrNotFound
import codes.cy.crmapi.api.extensions.validated
import codes.cy.crmapi.application.interfaces.IHubSpotCommandService
import codes.cy.crmapi.domain.common.toDatabaseProvider
import codes.cy.crmapi.domain.hubspot.Company
import codes.cy.crmapi.domain.hubspot.CompanyWriteRequest
import codes.cy.crmapi.domain.hubspot.Contact
import codes.cy.crmapi.domain.hubspot.ContactWriteRequest
import codes.cy.crmapi.domain.hubspot.Deal
import codes.cy.crmapi.domain.hubspot.DealWriteRequest
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
 * Write endpoints (create / full replace / hard delete) for HubSpot. Authorization is enforced in `SecurityConfig`:
 * POST/PUT require role `admin` or `user`, DELETE requires `admin`.
 */
@RestController
@RequestMapping("/api/{provider:^(?:ms|pg)$}/v1/hubspot", produces = [MediaType.APPLICATION_JSON_VALUE])
class HubSpotWriteController(
    private val commandService: IHubSpotCommandService,
) {
    @PostMapping("/companies", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createCompany(
        @PathVariable provider: String,
        @RequestBody request: CompanyWriteRequest,
    ): ResponseEntity<Company> =
        commandService
            .executeCreateCompany(provider.toDatabaseProvider(), request.validated())
            .let { created -> created.toCreatedResponseEntity(created.companyId) }

    @PutMapping("/companies/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateCompany(
        @PathVariable provider: String,
        @PathVariable id: Long,
        @RequestBody request: CompanyWriteRequest,
    ): ResponseEntity<Company> =
        commandService
            .executeUpdateCompany(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("HubSpot Company", id)

    @DeleteMapping("/companies/{id}")
    fun deleteCompany(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteCompany(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("HubSpot Company", id)

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
        @PathVariable id: Long,
        @RequestBody request: ContactWriteRequest,
    ): ResponseEntity<Contact> =
        commandService
            .executeUpdateContact(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("HubSpot Contact", id)

    @DeleteMapping("/contacts/{id}")
    fun deleteContact(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteContact(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("HubSpot Contact", id)

    @PostMapping("/deals", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun createDeal(
        @PathVariable provider: String,
        @RequestBody request: DealWriteRequest,
    ): ResponseEntity<Deal> =
        commandService
            .executeCreateDeal(provider.toDatabaseProvider(), request.validated())
            .let { created -> created.toCreatedResponseEntity(created.dealId) }

    @PutMapping("/deals/{id}", consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun updateDeal(
        @PathVariable provider: String,
        @PathVariable id: Long,
        @RequestBody request: DealWriteRequest,
    ): ResponseEntity<Deal> =
        commandService
            .executeUpdateDeal(provider.toDatabaseProvider(), id, request.validated())
            .toOkResponseEntityOrNotFound("HubSpot Deal", id)

    @DeleteMapping("/deals/{id}")
    fun deleteDeal(
        @PathVariable provider: String,
        @PathVariable id: Long,
    ): ResponseEntity<Void> =
        commandService
            .executeDeleteDeal(provider.toDatabaseProvider(), id)
            .toNoContentResponseEntityOrNotFound("HubSpot Deal", id)
}
