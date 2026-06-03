package com.jorgemonteiro.apps.finance.recurringexpense.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

data class RecurringExpenseResponse(
    val id: UUID,
    val payeeId: UUID,
    val categoryId: UUID,
    val title: String,
    val description: String?,
    val expectedValue: BigDecimal,
    val frequency: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    @get:JsonProperty("isActive") val isActive: Boolean,
    val createdAt: OffsetDateTime,
)

data class CreateRecurringExpenseRequest(
    @field:NotNull val payeeId: UUID,
    @field:NotNull val categoryId: UUID,
    @field:NotBlank @field:Size(max = 255) val title: String,
    val description: String? = null,
    @field:NotNull val expectedValue: BigDecimal,
    @field:NotBlank val frequency: String,
    @field:NotNull val startDate: LocalDate,
    val endDate: LocalDate? = null,
)

data class UpdateRecurringExpenseRequest(
    val title: String? = null,
    val description: String? = null,
    val expectedValue: BigDecimal? = null,
    val frequency: String? = null,
    val endDate: LocalDate? = null,
    val isActive: Boolean? = null,
)
