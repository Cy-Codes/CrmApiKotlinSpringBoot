package codes.cy.crmapi.api.controllers

import codes.cy.crmapi.api.extensions.toQueryOptions
import codes.cy.crmapi.domain.common.DatabaseProvider
import codes.cy.crmapi.domain.common.QueryOptions
import codes.cy.crmapi.domain.common.toDatabaseProvider

abstract class BaseCrmController {
    protected fun parseProvider(provider: String): DatabaseProvider =
        provider.toDatabaseProvider()

    protected fun createQueryOptions(
        allParams: Map<String, String>,
        page: Int = QueryOptions.DEFAULT_PAGE,
        pageSize: Int = QueryOptions.DEFAULT_PAGE_SIZE,
        sortBy: String? = null,
        sortDirection: String = QueryOptions.DEFAULT_SORT_DIRECTION,
    ): QueryOptions =
        allParams.toQueryOptions(page, pageSize, sortBy, sortDirection)
}
