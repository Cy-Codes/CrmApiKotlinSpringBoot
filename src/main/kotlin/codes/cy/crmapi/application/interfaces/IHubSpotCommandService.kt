package codes.cy.crmapi.application.interfaces

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.hubspot.Company
import codes.cy.crmapi.domain.hubspot.CompanyWriteRequest
import codes.cy.crmapi.domain.hubspot.Contact
import codes.cy.crmapi.domain.hubspot.ContactWriteRequest
import codes.cy.crmapi.domain.hubspot.Deal
import codes.cy.crmapi.domain.hubspot.DealWriteRequest

/**
 * Write-side (command) application service for HubSpot entities. Every method is a side-effecting command (CQS).
 */
interface IHubSpotCommandService {
    /** Command: creates a Company; ids and audit columns are server-owned. Returns the freshly re-read entity. */
    fun executeCreateCompany(provider: DatabaseProvider, request: CompanyWriteRequest): Company

    /** Command: fully replaces the writable fields of a Company. Returns `null` when the id does not exist. */
    fun executeUpdateCompany(provider: DatabaseProvider, id: Long, request: CompanyWriteRequest): Company?

    /** Command: hard-deletes a Company. Returns `false` when the id does not exist. */
    fun executeDeleteCompany(provider: DatabaseProvider, id: Long): Boolean

    /** Command: creates a Contact; ids and audit columns are server-owned. Returns the freshly re-read entity. */
    fun executeCreateContact(provider: DatabaseProvider, request: ContactWriteRequest): Contact

    /** Command: fully replaces the writable fields of a Contact. Returns `null` when the id does not exist. */
    fun executeUpdateContact(provider: DatabaseProvider, id: Long, request: ContactWriteRequest): Contact?

    /** Command: hard-deletes a Contact. Returns `false` when the id does not exist. */
    fun executeDeleteContact(provider: DatabaseProvider, id: Long): Boolean

    /** Command: creates a Deal; ids and audit columns are server-owned. Returns the freshly re-read entity. */
    fun executeCreateDeal(provider: DatabaseProvider, request: DealWriteRequest): Deal

    /** Command: fully replaces the writable fields of a Deal. Returns `null` when the id does not exist. */
    fun executeUpdateDeal(provider: DatabaseProvider, id: Long, request: DealWriteRequest): Deal?

    /** Command: hard-deletes a Deal. Returns `false` when the id does not exist. */
    fun executeDeleteDeal(provider: DatabaseProvider, id: Long): Boolean
}
