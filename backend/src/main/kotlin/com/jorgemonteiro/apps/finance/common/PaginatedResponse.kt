package com.jorgemonteiro.apps.finance.common

/**
 * Pagination metadata for list responses.
 */
data class Pagination(
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
)

/**
 * Paginated response wrapper.
 */
data class PaginatedResponse<T>(
    val data: List<T>,
    val pagination: Pagination,
)
