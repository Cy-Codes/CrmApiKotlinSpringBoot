package codes.cy.crmapi.application.interfaces

import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.dynamics365.Account
import codes.cy.crmapi.domain.dynamics365.AccountWriteRequest
import codes.cy.crmapi.domain.dynamics365.Contact
import codes.cy.crmapi.domain.dynamics365.ContactWriteRequest
import codes.cy.crmapi.domain.dynamics365.Opportunity
import codes.cy.crmapi.domain.dynamics365.OpportunityWriteRequest
import java.util.UUID

/**
 * Write-side (command) application service for Dynamics365 entities. Every method is a side-effecting command (CQS).
 */
interface IDynamics365CommandService {
    /** Command: creates a Account; ids and audit columns are server-owned. Returns the freshly re-read entity. */
    fun executeCreateAccount(provider: DatabaseProvider, request: AccountWriteRequest): Account

    /** Command: fully replaces the writable fields of a Account. Returns `null` when the id does not exist. */
    fun executeUpdateAccount(provider: DatabaseProvider, id: UUID, request: AccountWriteRequest): Account?

    /** Command: hard-deletes a Account. Returns `false` when the id does not exist. */
    fun executeDeleteAccount(provider: DatabaseProvider, id: UUID): Boolean

    /** Command: creates a Contact; ids and audit columns are server-owned. Returns the freshly re-read entity. */
    fun executeCreateContact(provider: DatabaseProvider, request: ContactWriteRequest): Contact

    /** Command: fully replaces the writable fields of a Contact. Returns `null` when the id does not exist. */
    fun executeUpdateContact(provider: DatabaseProvider, id: UUID, request: ContactWriteRequest): Contact?

    /** Command: hard-deletes a Contact. Returns `false` when the id does not exist. */
    fun executeDeleteContact(provider: DatabaseProvider, id: UUID): Boolean

    /** Command: creates a Opportunity; ids and audit columns are server-owned. Returns the freshly re-read entity. */
    fun executeCreateOpportunity(provider: DatabaseProvider, request: OpportunityWriteRequest): Opportunity

    /** Command: fully replaces the writable fields of a Opportunity. Returns `null` when the id does not exist. */
    fun executeUpdateOpportunity(provider: DatabaseProvider, id: UUID, request: OpportunityWriteRequest): Opportunity?

    /** Command: hard-deletes a Opportunity. Returns `false` when the id does not exist. */
    fun executeDeleteOpportunity(provider: DatabaseProvider, id: UUID): Boolean
}
