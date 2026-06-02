package com.jorgemonteiro.apps.finance.payee.dto

import com.jorgemonteiro.apps.finance.category.dto.CategoryResponse
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Response DTO for a payee with associated categories.
 */
data class PayeeResponse(
    val id: UUID,
    val name: String,
    val categories: List<CategoryResponse>,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
)

/**
 * Request DTO for creating a payee.
 */
data class CreatePayeeRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val name: String,

    @field:NotEmpty
    val categoryIds: List<UUID>,
)

/**
 * Request DTO for updating a payee.
 */
data class UpdatePayeeRequest(
    @field:Size(max = 255)
    val name: String? = null,

    val categoryIds: List<UUID>? = null,
)
