package com.jorgemonteiro.apps.finance.category.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Response DTO for a category.
 */
data class CategoryResponse(
    val id: UUID,
    val name: String,
    val icon: String,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)

/**
 * Request DTO for creating a category.
 */
data class CreateCategoryRequest(
    @field:NotBlank
    @field:Size(max = 100)
    val name: String,

    @field:NotBlank
    @field:Size(max = 100)
    val icon: String,
)

/**
 * Request DTO for updating a category.
 */
data class UpdateCategoryRequest(
    @field:Size(max = 100)
    val name: String? = null,

    @field:Size(max = 100)
    val icon: String? = null,
)
