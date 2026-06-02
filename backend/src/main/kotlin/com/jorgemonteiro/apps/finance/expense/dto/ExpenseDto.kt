package com.jorgemonteiro.apps.finance.expense.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

/** Derived expense status. */
enum class ExpenseStatus { PENDING, PAID, OVERDUE }

/**
 * Response DTO for an expense.
 */
data class ExpenseResponse(
    val id: UUID,
    val quarterId: UUID,
    val quarterNumber: Int,
    val payee: PayeeSummary,
    val category: CategorySummary,
    val title: String,
    val description: String?,
    val expectedValue: BigDecimal,
    val actualValue: BigDecimal?,
    val dueDate: LocalDate,
    val paymentDate: LocalDate?,
    val status: ExpenseStatus,
    val isOverride: Boolean,
    val bankAccountId: UUID?,
    val createdAt: OffsetDateTime,
)

data class PayeeSummary(val id: UUID, val name: String)
data class CategorySummary(val id: UUID, val name: String, val icon: String)

/**
 * Request DTO for creating an expense.
 */
data class CreateExpenseRequest(
    @field:NotNull val payeeId: UUID,
    @field:NotNull val categoryId: UUID,
    @field:NotBlank @field:Size(max = 255) val title: String,
    val description: String? = null,
    @field:NotNull val expectedValue: BigDecimal,
    @field:NotNull val dueDate: LocalDate,
    @field:NotNull val bankAccountId: UUID,
)

/**
 * Request DTO for updating an expense.
 */
data class UpdateExpenseRequest(
    val title: String? = null,
    val description: String? = null,
    val expectedValue: BigDecimal? = null,
    val actualValue: BigDecimal? = null,
    val dueDate: LocalDate? = null,
    val paymentDate: LocalDate? = null,
    val clearPayment: Boolean? = null,
    val bankAccountId: UUID? = null,
)
