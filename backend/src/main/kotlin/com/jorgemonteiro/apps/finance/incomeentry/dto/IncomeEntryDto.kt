package com.jorgemonteiro.apps.finance.incomeentry.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

data class IncomeEntryResponse(
    val id: UUID,
    val financialMonthId: UUID,
    val incomeSourceId: UUID?,
    val name: String,
    val amount: BigDecimal,
    @get:JsonProperty("isAdhoc") val isAdhoc: Boolean,
    val createdAt: OffsetDateTime,
)

data class CreateIncomeEntryRequest(
    @field:NotBlank val name: String,
    @field:NotNull val amount: BigDecimal,
)

data class UpdateIncomeEntryRequest(
    val name: String? = null,
    val amount: BigDecimal? = null,
)
