package com.jorgemonteiro.apps.finance.expense.service

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.ExpensesRecord
import com.jorgemonteiro.apps.finance.category.repository.CategoryRepository
import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import com.jorgemonteiro.apps.finance.exception.ValidationException
import com.jorgemonteiro.apps.finance.expense.dto.*
import com.jorgemonteiro.apps.finance.expense.repository.ExpenseRepository
import com.jorgemonteiro.apps.finance.financialmonth.repository.FinancialMonthRepository
import com.jorgemonteiro.apps.finance.payee.repository.PayeeRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Service for managing expenses.
 */
@Service
class ExpenseService(
    private val repository: ExpenseRepository,
    private val monthRepository: FinancialMonthRepository,
    private val payeeRepository: PayeeRepository,
    private val categoryRepository: CategoryRepository,
) {

    /** Lists expenses for a financial month. */
    fun listByMonth(monthId: UUID): List<ExpenseResponse> {
        monthRepository.findById(monthId)
            ?: throw ExpenseMonthNotFoundException(monthId)
        return repository.findByMonthId(monthId).map { toResponse(it) }
    }

    /** Creates an expense, assigning it to the correct quarter by due date. */
    fun create(monthId: UUID, request: CreateExpenseRequest): ExpenseResponse {
        val month = monthRepository.findById(monthId)
            ?: throw ExpenseMonthNotFoundException(monthId)

        val payee = payeeRepository.findById(request.payeeId)
            ?: throw ExpenseValidationException(listOf("Payee not found: ${request.payeeId}"))
        val category = categoryRepository.findById(request.categoryId)
            ?: throw ExpenseValidationException(listOf("Category not found: ${request.categoryId}"))

        val quarters = monthRepository.findQuartersByMonthId(monthId)
        val quarter = quarters.firstOrNull { request.dueDate >= it.startDate && request.dueDate <= it.endDate }
            ?: throw ExpenseValidationException(listOf("Due date ${request.dueDate} does not fall within any quarter"))

        val record = repository.insert(
            id = UuidV7.generate(),
            quarterId = quarter.id!!,
            payeeId = request.payeeId,
            categoryId = request.categoryId,
            title = request.title,
            description = request.description,
            expectedValue = request.expectedValue,
            dueDate = request.dueDate,
            now = OffsetDateTime.now(),
        )
        return toResponse(record)
    }

    /** Updates an expense. */
    fun update(id: UUID, request: UpdateExpenseRequest): ExpenseResponse {
        val existing = repository.findById(id) ?: throw ExpenseNotFoundException(id)

        val title = request.title ?: existing.title!!
        val description = request.description ?: existing.description
        val expectedValue = request.expectedValue ?: existing.expectedValue!!
        val actualValue = request.actualValue ?: existing.actualValue
        val dueDate = request.dueDate ?: existing.dueDate!!
        val paymentDate = request.paymentDate ?: existing.paymentDate
        val isOverride = request.expectedValue != null || existing.isOverride!!

        val record = repository.update(
            id = id, title = title, description = description,
            expectedValue = expectedValue, actualValue = actualValue,
            dueDate = dueDate, paymentDate = paymentDate,
            isOverride = isOverride, now = OffsetDateTime.now(),
        )
        return toResponse(record)
    }

    /** Deletes an expense. */
    fun delete(id: UUID) {
        if (!repository.deleteById(id)) throw ExpenseNotFoundException(id)
    }

    private fun toResponse(record: ExpensesRecord): ExpenseResponse {
        val payee = payeeRepository.findById(record.payeeId!!)!!
        val category = categoryRepository.findById(record.categoryId!!)!!
        val quarter = monthRepository.findQuartersByMonthId(
            // find quarter's month
            record.quarterId!!.let { qId ->
                monthRepository.findAll().first { m ->
                    monthRepository.findQuartersByMonthId(m.id!!).any { it.id == qId }
                }.id!!
            }
        ).first { it.id == record.quarterId }

        return ExpenseResponse(
            id = record.id!!,
            quarterId = record.quarterId!!,
            quarterNumber = quarter.quarterNumber!!,
            payee = PayeeSummary(payee.id!!, payee.name!!),
            category = CategorySummary(category.id!!, category.name!!, category.icon!!),
            title = record.title!!,
            description = record.description,
            expectedValue = record.expectedValue!!,
            actualValue = record.actualValue,
            dueDate = record.dueDate!!,
            paymentDate = record.paymentDate,
            status = deriveStatus(record),
            isOverride = record.isOverride!!,
            createdAt = record.createdAt!!,
        )
    }

    private fun deriveStatus(record: ExpensesRecord): ExpenseStatus = when {
        record.paymentDate != null && record.actualValue != null -> ExpenseStatus.PAID
        record.dueDate!! < LocalDate.now() && record.paymentDate == null -> ExpenseStatus.OVERDUE
        else -> ExpenseStatus.PENDING
    }
}

class ExpenseNotFoundException(id: UUID) :
    EntityNotFoundException("Expense not found: $id")

class ExpenseMonthNotFoundException(id: UUID) :
    EntityNotFoundException("Financial month not found: $id")

class ExpenseValidationException(errors: List<String>) :
    ValidationException("Expense validation failed", errors)
