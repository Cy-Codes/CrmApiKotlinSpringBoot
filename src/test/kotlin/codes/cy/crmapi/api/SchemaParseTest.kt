package codes.cy.crmapi.api

import graphql.schema.idl.SchemaParser
import java.io.InputStreamReader
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SchemaParseTest {
    @Test
    fun testParseSchema() {
        val resource = javaClass.getResourceAsStream("/graphql/schema.graphqls")
            ?: throw IllegalStateException("Resource /graphql/schema.graphqls not found")
        val typeRegistry = SchemaParser().parse(InputStreamReader(resource))
        val typeNames = typeRegistry.types().keys

        assertTrue(typeNames.contains("Query"))
        assertTrue(typeNames.contains("DatabaseProvider"))
        assertTrue(typeNames.contains("SalesforceAccount"))
        assertTrue(typeNames.contains("SalesforceContact"))
        assertTrue(typeNames.contains("HubSpotCompany"))
        assertTrue(typeNames.contains("HubSpotContact"))
        assertTrue(typeNames.contains("Dynamics365Account"))
        assertTrue(typeNames.contains("Dynamics365Contact"))
    }
}
