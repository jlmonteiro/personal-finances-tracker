package com.jorgemonteiro.apps.finance.incomesource.service

import com.jorgemonteiro.apps.finance.common.PaginatedResponse
import com.jorgemonteiro.apps.finance.common.Pagination
import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import com.jorgemonteiro.apps.finance.exception.ValidationException
import com.jorgemonteiro.apps.finance.incomesource.dto.CreateIncomeSourceRequest
import com.jorgemonteiro.apps.finance.incomesource.dto.IncomeSourceResponse
import com.jorgemonteiro.apps.finance.incomesource.dto.UpdateIncomeSourceRequest
import com.jorgemonteiro.apps.finance.incomesource.mapper.IncomeSourceMapper
import com.jorgemonteiro.apps.finance.incomesource.repository.IncomeSourceRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.math.ceil

/**
 * Service for managing income sources.
 */
@Service
class IncomeSourceService(
    private val repository: IncomeSourceRepository,
    private val mapper: IncomeSourceMapper,
) {

    /**
     * Retrieves a paginated list of income sources.
     */
    fun list(page: Int, size: Int): PaginatedResponse<IncomeSourceResponse> {
        val offset = (page - 1) * size
        val records = repository.findAll(offset, size)
        val total = repository.count()
        val totalPages = ceil(total.toDouble() / size).toInt()

        return PaginatedResponse(
            data = records.map { mapper.toResponse(it) },
            pagination = Pagination(
                page = page,
                size = size,
                totalElements = total,
                totalPages = totalPages,
            ),
        )
    }

    /**
     * Creates a new income source.
     */
    fun create(request: CreateIncomeSourceRequest): IncomeSourceResponse {
        validateAmount(request.amount.value)

        val record = repository.insert(
            id = UuidV7.generate(),
            name = request.name,
            description = request.description,
            amount = BigDecimal(request.amount.value),
            currency = request.amount.currency,
            frequency = request.frequency.name,
            paymentDateType = request.paymentDateType.name,
            paymentDateRule = request.paymentDateRule,
            startDate = request.startDate,
            endDate = request.endDate,
            now = OffsetDateTime.now(),
        )

        return mapper.toResponse(record)
    }

    /**
     * Updates an existing income source. Throws if not found.
     */
    fun update(id: UUID, request: UpdateIncomeSourceRequest): IncomeSourceResponse {
        val existing = repository.findById(id) ?: throw IncomeSourceNotFoundException(id)

        val amountValue = request.amount?.value ?: existing.amount!!.toPlainString()
        validateAmount(amountValue)

        val record = repository.update(
            id = id,
            name = request.name ?: existing.name!!,
            description = request.description ?: existing.description,
            amount = BigDecimal(amountValue),
            currency = request.amount?.currency ?: existing.currency!!,
            frequency = request.frequency?.name ?: existing.frequency!!,
            paymentDateType = request.paymentDateType?.name ?: existing.paymentDateType!!,
            paymentDateRule = request.paymentDateRule ?: existing.paymentDateRule!!,
            startDate = request.startDate ?: existing.startDate!!,
            endDate = request.endDate ?: existing.endDate,
            isActive = request.isActive ?: existing.isActive!!,
            now = OffsetDateTime.now(),
        )

        return mapper.toResponse(record)
    }

    /**
     * Deletes an income source. Throws if not found.
     */
    fun delete(id: UUID) {
        if (!repository.deleteById(id)) {
            throw IncomeSourceNotFoundException(id)
        }
    }

    private fun validateAmount(amountValue: String) {
        if (BigDecimal(amountValue) <= BigDecimal.ZERO) {
            throw IncomeSourceValidationException(listOf("amount must be greater than 0"))
        }
    }
}

/**
 * Thrown when an income source is not found.
 */
class IncomeSourceNotFoundException(id: UUID) :
    EntityNotFoundException("Income source not found: $id")

/**
 * Thrown when income source validation fails.
 */
class IncomeSourceValidationException(errors: List<String>) :
    ValidationException("Validation failed", errors)
