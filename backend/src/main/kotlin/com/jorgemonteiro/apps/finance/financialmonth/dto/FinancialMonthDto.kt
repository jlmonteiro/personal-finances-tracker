package com.jorgemonteiro.apps.finance.financialmonth.dto

import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Response DTO for a financial month.
 */
data class FinancialMonthResponse(
    val id: UUID,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val createdAt: OffsetDateTime,
)

/**
 * Response DTO for a quarter within a financial month.
 */
data class QuarterResponse(
    val id: UUID,
    val quarterNumber: Int,
    val startDate: LocalDate,
    val endDate: LocalDate,
)

/**
 * Request DTO for creating a financial month.
 */
data class CreateFinancialMonthRequest(
    val year: Int,
    val month: Int,
)
