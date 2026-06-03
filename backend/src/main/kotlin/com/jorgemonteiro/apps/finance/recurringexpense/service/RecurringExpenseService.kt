package com.jorgemonteiro.apps.finance.recurringexpense.service

import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import com.jorgemonteiro.apps.finance.recurringexpense.dto.CreateRecurringExpenseRequest
import com.jorgemonteiro.apps.finance.recurringexpense.dto.RecurringExpenseResponse
import com.jorgemonteiro.apps.finance.recurringexpense.dto.UpdateRecurringExpenseRequest
import com.jorgemonteiro.apps.finance.recurringexpense.repository.RecurringExpenseRepository
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.RecurringExpensesRecord
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class RecurringExpenseService(private val repository: RecurringExpenseRepository) {

    fun list(): List<RecurringExpenseResponse> =
        repository.findAll().map { toResponse(it) }

    fun create(request: CreateRecurringExpenseRequest): RecurringExpenseResponse {
        val record = repository.insert(
            id = UuidV7.generate(),
            payeeId = request.payeeId,
            categoryId = request.categoryId,
            title = request.title,
            description = request.description,
            expectedValue = request.expectedValue,
            frequency = request.frequency,
            startDate = request.startDate,
            endDate = request.endDate,
            now = OffsetDateTime.now(),
        )
        return toResponse(record)
    }

    fun update(id: UUID, request: UpdateRecurringExpenseRequest): RecurringExpenseResponse {
        val existing = repository.findById(id) ?: throw RecurringExpenseNotFoundException(id)
        val record = repository.update(
            id = id,
            title = request.title ?: existing.title!!,
            description = request.description ?: existing.description,
            expectedValue = request.expectedValue ?: existing.expectedValue!!,
            frequency = request.frequency ?: existing.frequency!!,
            endDate = request.endDate ?: existing.endDate,
            isActive = request.isActive ?: existing.isActive!!,
            now = OffsetDateTime.now(),
        )
        return toResponse(record)
    }

    fun delete(id: UUID) {
        if (!repository.deleteById(id)) throw RecurringExpenseNotFoundException(id)
    }

    private fun toResponse(record: RecurringExpensesRecord) = RecurringExpenseResponse(
        id = record.id!!,
        payeeId = record.payeeId!!,
        categoryId = record.categoryId!!,
        title = record.title!!,
        description = record.description,
        expectedValue = record.expectedValue!!,
        frequency = record.frequency!!,
        startDate = record.startDate!!,
        endDate = record.endDate,
        isActive = record.isActive!!,
        createdAt = record.createdAt!!,
    )
}

class RecurringExpenseNotFoundException(id: UUID) :
    EntityNotFoundException("Recurring expense not found: $id")
