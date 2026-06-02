package com.jorgemonteiro.apps.finance.incomesource.dto

import com.jorgemonteiro.apps.finance.common.Money
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Response DTO for an income source.
 */
data class IncomeSourceResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val amount: Money,
    val frequency: Frequency,
    val paymentDateType: PaymentDateType,
    val paymentDateRule: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    @get:JsonProperty("isActive")
    val isActive: Boolean,
    val createdAt: OffsetDateTime,
)

/**
 * Request DTO for creating an income source.
 */
data class CreateIncomeSourceRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val name: String,

    @field:Size(max = 500)
    val description: String? = null,

    @field:NotNull
    @field:Valid
    val amount: Money,

    @field:NotNull
    val frequency: Frequency,

    @field:NotNull
    val paymentDateType: PaymentDateType,

    @field:NotBlank
    @field:Size(max = 100)
    val paymentDateRule: String,

    @field:NotNull
    val startDate: LocalDate,

    val endDate: LocalDate? = null,
)

/**
 * Request DTO for updating an income source.
 */
data class UpdateIncomeSourceRequest(
    @field:Size(max = 255)
    val name: String? = null,

    @field:Size(max = 500)
    val description: String? = null,

    @field:Valid
    val amount: Money? = null,

    val frequency: Frequency? = null,

    val paymentDateType: PaymentDateType? = null,

    @field:Size(max = 100)
    val paymentDateRule: String? = null,

    val startDate: LocalDate? = null,

    val endDate: LocalDate? = null,

    val isActive: Boolean? = null,
)
