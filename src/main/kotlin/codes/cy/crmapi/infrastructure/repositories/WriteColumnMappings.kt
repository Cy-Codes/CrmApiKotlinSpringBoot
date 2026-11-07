package codes.cy.crmapi.infrastructure.repositories

import codes.cy.crmapi.domain.dynamics365.AccountWriteRequest as DynamicsAccountWriteRequest
import codes.cy.crmapi.domain.dynamics365.ContactWriteRequest as DynamicsContactWriteRequest
import codes.cy.crmapi.domain.dynamics365.OpportunityWriteRequest as DynamicsOpportunityWriteRequest
import codes.cy.crmapi.domain.hubspot.CompanyWriteRequest
import codes.cy.crmapi.domain.hubspot.ContactWriteRequest as HubSpotContactWriteRequest
import codes.cy.crmapi.domain.hubspot.DealWriteRequest
import codes.cy.crmapi.domain.salesforce.AccountWriteRequest as SalesforceAccountWriteRequest
import codes.cy.crmapi.domain.salesforce.ContactWriteRequest as SalesforceContactWriteRequest
import codes.cy.crmapi.domain.salesforce.OpportunityWriteRequest as SalesforceOpportunityWriteRequest
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID

/*
 * Pure request -> column mappings. Column names equal the physical DDL names (case-insensitive on SQL Server, quoted on PostgreSQL).
 * Generated columns (Salesforce Contact.Name, Opportunity.ExpectedRevenue) are never emitted.
 * Optional DTO fields backing NOT NULL-with-default columns are omitted when null, so the database default applies on create and the
 * current value is kept on replace. All other nullable columns are written as-is (null clears the column on replace).
 */

internal const val SALESFORCE_ACCOUNT_TABLE = "Salesforce.Account"
internal const val SALESFORCE_CONTACT_TABLE = "Salesforce.Contact"
internal const val SALESFORCE_OPPORTUNITY_TABLE = "Salesforce.Opportunity"
internal const val SALESFORCE_ID_COLUMN = "Id"

internal const val HUBSPOT_COMPANY_TABLE = "HubSpot.Company"
internal const val HUBSPOT_CONTACT_TABLE = "HubSpot.Contact"
internal const val HUBSPOT_DEAL_TABLE = "HubSpot.Deal"
internal const val HUBSPOT_COMPANY_ID_COLUMN = "companyId"
internal const val HUBSPOT_CONTACT_ID_COLUMN = "contactId"
internal const val HUBSPOT_DEAL_ID_COLUMN = "dealId"

internal const val DYNAMICS365_ACCOUNT_TABLE = "Dynamics365.Account"
internal const val DYNAMICS365_CONTACT_TABLE = "Dynamics365.Contact"
internal const val DYNAMICS365_OPPORTUNITY_TABLE = "Dynamics365.Opportunity"
internal const val DYNAMICS365_ACCOUNT_ID_COLUMN = "accountid"
internal const val DYNAMICS365_CONTACT_ID_COLUMN = "contactid"
internal const val DYNAMICS365_OPPORTUNITY_ID_COLUMN = "opportunityid"

/**
 * Pure helper keeping only the pairs whose value is non-null (used for columns that have a database default).
 */
internal fun columnsOfNonNull(vararg columns: Pair<String, Any?>): Map<String, Any?> =
    columns.filter { (_, value) -> value != null }.toMap()

/**
 * Pure conversion of a UTC wall-clock [LocalDateTime] into an absolute [OffsetDateTime] (binds correctly to TIMESTAMPTZ and DATETIME2).
 */
internal fun LocalDateTime.toUtcOffsetDateTime(): OffsetDateTime = atOffset(ZoneOffset.UTC)

/**
 * Pure computation of the Dynamics 365 Contact `fullname` column: `"$firstName $lastName"`, trimmed.
 */
fun getDynamicsContactFullName(firstName: String?, lastName: String): String =
    "${firstName.orEmpty()} $lastName".trim()

// ---------------------------------------------------------------- Salesforce

internal fun SalesforceAccountWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "ParentId" to parentId,
        "Name" to name,
        "Type" to type,
        "BillingStreet" to billingStreet,
        "BillingCity" to billingCity,
        "BillingState" to billingState,
        "BillingPostalCode" to billingPostalCode,
        "BillingCountry" to billingCountry,
        "ShippingStreet" to shippingStreet,
        "ShippingCity" to shippingCity,
        "ShippingState" to shippingState,
        "ShippingPostalCode" to shippingPostalCode,
        "ShippingCountry" to shippingCountry,
        "Phone" to phone,
        "Fax" to fax,
        "Website" to website,
        "Industry" to industry,
        "AnnualRevenue" to annualRevenue,
        "NumberOfEmployees" to numberOfEmployees,
        "Description" to description,
        "OwnerId" to ownerId,
    ) + columnsOfNonNull("IsDeleted" to isDeleted)

internal fun SalesforceContactWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "AccountId" to accountId,
        "LastName" to lastName,
        "FirstName" to firstName,
        "Salutation" to salutation,
        "ReportsToId" to reportsToId,
        "Title" to title,
        "Department" to department,
        "Email" to email,
        "Phone" to phone,
        "MobilePhone" to mobilePhone,
        "MailingStreet" to mailingStreet,
        "MailingCity" to mailingCity,
        "MailingState" to mailingState,
        "MailingPostalCode" to mailingPostalCode,
        "MailingCountry" to mailingCountry,
        "LeadSource" to leadSource,
        "Description" to description,
        "OwnerId" to ownerId,
    ) + columnsOfNonNull("IsDeleted" to isDeleted)

internal fun SalesforceOpportunityWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "AccountId" to accountId,
        "Name" to name,
        "Description" to description,
        "StageName" to stageName,
        "Amount" to amount,
        "Probability" to probability,
        "CloseDate" to closeDate.toLocalDate(),
        "Type" to type,
        "LeadSource" to leadSource,
        "CampaignId" to campaignId,
        "Pricebook2Id" to pricebook2Id,
        "OwnerId" to ownerId,
    ) + columnsOfNonNull(
        "IsDeleted" to isDeleted,
        "IsPrivate" to isPrivate,
        "IsClosed" to isClosed,
        "IsWon" to isWon,
        "ForecastCategoryName" to forecastCategoryName,
    )

/**
 * Server-owned Salesforce columns for a create: id, both audit timestamps, and `CreatedById = LastModifiedById = OwnerId`.
 * (JWT subjects do not map to CRM users, so the record owner stands in for the acting user.)
 */
internal fun getSalesforceCreateAuditColumns(id: String, ownerId: String, now: OffsetDateTime): Map<String, Any?> =
    mapOf(
        SALESFORCE_ID_COLUMN to id,
        "CreatedDate" to now,
        "CreatedById" to ownerId,
        "LastModifiedDate" to now,
        "LastModifiedById" to ownerId,
    )

internal fun getSalesforceUpdateAuditColumns(ownerId: String, now: OffsetDateTime): Map<String, Any?> =
    mapOf("LastModifiedDate" to now, "LastModifiedById" to ownerId)

internal fun SalesforceAccountWriteRequest.toInsertColumns(id: String, now: OffsetDateTime) =
    getSalesforceCreateAuditColumns(id, ownerId, now) + toWriteColumns()

internal fun SalesforceAccountWriteRequest.toUpdateColumns(now: OffsetDateTime) =
    toWriteColumns() + getSalesforceUpdateAuditColumns(ownerId, now)

internal fun SalesforceContactWriteRequest.toInsertColumns(id: String, now: OffsetDateTime) =
    getSalesforceCreateAuditColumns(id, ownerId, now) + toWriteColumns()

internal fun SalesforceContactWriteRequest.toUpdateColumns(now: OffsetDateTime) =
    toWriteColumns() + getSalesforceUpdateAuditColumns(ownerId, now)

internal fun SalesforceOpportunityWriteRequest.toInsertColumns(id: String, now: OffsetDateTime) =
    getSalesforceCreateAuditColumns(id, ownerId, now) + toWriteColumns()

internal fun SalesforceOpportunityWriteRequest.toUpdateColumns(now: OffsetDateTime) =
    toWriteColumns() + getSalesforceUpdateAuditColumns(ownerId, now)

// ------------------------------------------------------------------- HubSpot

internal fun CompanyWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "name" to name,
        "domain" to domain,
        "industry" to industry,
        "phone" to phone,
        "address" to address,
        "city" to city,
        "state" to state,
        "zip" to zip,
        "country" to country,
        "annualRevenue" to annualRevenue,
        "numberOfEmployees" to numberOfEmployees,
        "description" to description,
        "ownerId" to ownerId,
    ) + columnsOfNonNull("lifecyclestage" to lifecyclestage, "archived" to archived)

internal fun HubSpotContactWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "email" to email,
        "firstName" to firstName,
        "lastName" to lastName,
        "phone" to phone,
        "mobilePhone" to mobilePhone,
        "jobTitle" to jobTitle,
        "associatedCompanyId" to associatedCompanyId,
        "ownerId" to ownerId,
        "leadStatus" to leadStatus,
        "address" to address,
        "city" to city,
        "state" to state,
        "zip" to zip,
        "country" to country,
    ) + columnsOfNonNull("lifecyclestage" to lifecyclestage, "archived" to archived)

internal fun DealWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "dealName" to dealName,
        "pipelineId" to pipelineId,
        "dealStageId" to dealStageId,
        "amount" to amount,
        "closedate" to closedate?.toUtcOffsetDateTime(),
        "dealType" to dealType,
        "ownerId" to ownerId,
    ) + columnsOfNonNull("archived" to archived)

internal fun getHubSpotCreateAuditColumns(idColumn: String, id: Long, now: OffsetDateTime): Map<String, Any?> =
    mapOf(idColumn to id, "createdAt" to now, "updatedAt" to now)

internal fun getHubSpotUpdateAuditColumns(now: OffsetDateTime): Map<String, Any?> =
    mapOf("updatedAt" to now)

internal fun CompanyWriteRequest.toInsertColumns(id: Long, now: OffsetDateTime) =
    getHubSpotCreateAuditColumns(HUBSPOT_COMPANY_ID_COLUMN, id, now) + toWriteColumns()

internal fun CompanyWriteRequest.toUpdateColumns(now: OffsetDateTime) = toWriteColumns() + getHubSpotUpdateAuditColumns(now)

internal fun HubSpotContactWriteRequest.toInsertColumns(id: Long, now: OffsetDateTime) =
    getHubSpotCreateAuditColumns(HUBSPOT_CONTACT_ID_COLUMN, id, now) + toWriteColumns()

internal fun HubSpotContactWriteRequest.toUpdateColumns(now: OffsetDateTime) = toWriteColumns() + getHubSpotUpdateAuditColumns(now)

internal fun DealWriteRequest.toInsertColumns(id: Long, now: OffsetDateTime) =
    getHubSpotCreateAuditColumns(HUBSPOT_DEAL_ID_COLUMN, id, now) + toWriteColumns()

internal fun DealWriteRequest.toUpdateColumns(now: OffsetDateTime) = toWriteColumns() + getHubSpotUpdateAuditColumns(now)

// -------------------------------------------------------------- Dynamics 365

internal fun DynamicsAccountWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "name" to name,
        "accountnumber" to accountNumber,
        "parentaccountid" to parentAccountId,
        "telephone1" to telephone1,
        "emailaddress1" to emailAddress1,
        "websiteurl" to websiteurl,
        "address1_line1" to address1_Line1,
        "address1_city" to address1_City,
        "address1_stateorprovince" to address1_StateOrProvince,
        "address1_postalcode" to address1_PostalCode,
        "address1_country" to address1_Country,
        "revenue" to revenue,
        "numberofemployees" to numberOfEmployees,
        "industrycode" to industrycode,
        "description" to description,
        "ownerid" to ownerId,
        "owningbusinessunit" to owningBusinessUnit,
    ) + columnsOfNonNull("statecode" to stateCode, "statuscode" to statusCode)

internal fun DynamicsContactWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "parentcustomerid" to parentCustomerId,
        "firstname" to firstName,
        "lastname" to lastName,
        "fullname" to getDynamicsContactFullName(firstName, lastName),
        "jobtitle" to jobTitle,
        "emailaddress1" to emailAddress1,
        "telephone1" to telephone1,
        "mobilephone" to mobilephone,
        "address1_line1" to address1_Line1,
        "address1_city" to address1_City,
        "address1_stateorprovince" to address1_StateOrProvince,
        "address1_postalcode" to address1_PostalCode,
        "address1_country" to address1_Country,
        "leadsourcecode" to leadsourcecode,
        "description" to description,
        "ownerid" to ownerId,
        "owningbusinessunit" to owningBusinessUnit,
    ) + columnsOfNonNull("statecode" to stateCode, "statuscode" to statusCode)

internal fun DynamicsOpportunityWriteRequest.toWriteColumns(): Map<String, Any?> =
    mapOf(
        "name" to name,
        "parentaccountid" to parentAccountId,
        "parentcontactid" to parentcontactid,
        "pricelevelid" to priceLevelId,
        "originatingleadid" to originatingLeadId,
        "estimatedvalue" to estimatedValue,
        "actualvalue" to actualValue,
        "estimatedclosedate" to estimatedCloseDate?.toLocalDate(),
        "actualclosedate" to actualCloseDate?.toLocalDate(),
        "closeprobability" to closeProbability,
        "stepname" to stepname,
        "salesstage" to salesstage,
        "description" to description,
        "ownerid" to ownerId,
        "owningbusinessunit" to owningBusinessUnit,
    ) + columnsOfNonNull("statecode" to stateCode, "statuscode" to statusCode)

/**
 * Server-owned Dynamics 365 columns for a create: id, both audit timestamps, and `createdby = modifiedby = ownerid`.
 */
internal fun getDynamicsCreateAuditColumns(idColumn: String, id: UUID, ownerId: UUID, now: OffsetDateTime): Map<String, Any?> =
    mapOf(idColumn to id, "createdon" to now, "createdby" to ownerId, "modifiedon" to now, "modifiedby" to ownerId)

internal fun getDynamicsUpdateAuditColumns(ownerId: UUID, now: OffsetDateTime): Map<String, Any?> =
    mapOf("modifiedon" to now, "modifiedby" to ownerId)

internal fun DynamicsAccountWriteRequest.toInsertColumns(id: UUID, now: OffsetDateTime) =
    getDynamicsCreateAuditColumns(DYNAMICS365_ACCOUNT_ID_COLUMN, id, ownerId, now) + toWriteColumns()

internal fun DynamicsAccountWriteRequest.toUpdateColumns(now: OffsetDateTime) =
    toWriteColumns() + getDynamicsUpdateAuditColumns(ownerId, now)

internal fun DynamicsContactWriteRequest.toInsertColumns(id: UUID, now: OffsetDateTime) =
    getDynamicsCreateAuditColumns(DYNAMICS365_CONTACT_ID_COLUMN, id, ownerId, now) + toWriteColumns()

internal fun DynamicsContactWriteRequest.toUpdateColumns(now: OffsetDateTime) =
    toWriteColumns() + getDynamicsUpdateAuditColumns(ownerId, now)

internal fun DynamicsOpportunityWriteRequest.toInsertColumns(id: UUID, now: OffsetDateTime) =
    getDynamicsCreateAuditColumns(DYNAMICS365_OPPORTUNITY_ID_COLUMN, id, ownerId, now) + toWriteColumns()

internal fun DynamicsOpportunityWriteRequest.toUpdateColumns(now: OffsetDateTime) =
    toWriteColumns() + getDynamicsUpdateAuditColumns(ownerId, now)
