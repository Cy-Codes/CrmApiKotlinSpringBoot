package codes.cy.crmapi.domain.common

/**
 * Contract for immutable request bodies of create/replace operations.
 */
interface WriteRequest {
    /**
     * Pure validation of required text fields; throws [IllegalArgumentException] (mapped to `400 Bad Request`) on violation.
     */
    fun validate()
}

/**
 * Pure helper asserting that every named text field is non-blank.
 */
fun requireNotBlankFields(vararg fields: Pair<String, String>) {
    val blankFieldNames = fields.filter { (_, value) -> value.isBlank() }.map { (name, _) -> name }
    require(blankFieldNames.isEmpty()) { "Required field(s) must not be blank: ${blankFieldNames.joinToString(", ")}." }
}
