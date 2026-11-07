package codes.cy.crmapi.application.services

import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneOffset

/**
 * Reads the current instant from this [Clock] as a UTC [OffsetDateTime] (used for audit timestamps).
 */
fun Clock.getUtcNow(): OffsetDateTime = OffsetDateTime.now(this).withOffsetSameInstant(ZoneOffset.UTC)
