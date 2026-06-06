package com.jorgemonteiro.apps.finance.incomeentry.service

import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import com.jorgemonteiro.apps.finance.incomeentry.dto.CreateIncomeEntryRequest
import com.jorgemonteiro.apps.finance.incomeentry.dto.IncomeEntryResponse
import com.jorgemonteiro.apps.finance.incomeentry.dto.UpdateIncomeEntryRequest
import com.jorgemonteiro.apps.finance.incomeentry.repository.IncomeEntryRepository
import com.jorgemonteiro.apps.finance.incomesource.repository.IncomeSourceRepository
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.IncomeEntriesRecord
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Service
class IncomeEntryService(
    private val repository: IncomeEntryRepository,
    private val incomeSourceRepository: IncomeSourceRepository,
) {

    fun listByMonth(monthId: UUID): List<IncomeEntryResponse> =
        repository.findByMonthId(monthId).map { toResponse(it) }

    /** Auto-generate income entries from active income sources for a month. */
    fun generateForMonth(monthId: UUID) {
        val existing = repository.findByMonthId(monthId)
        val sources = incomeSourceRepository.findAll(0, 100)
            .filter { it.isActive == true }

        for (source in sources) {
            // Skip if already generated for this source
            if (existing.any { it.incomeSourceId == source.id }) continue
            repository.insert(
                id = UuidV7.generate(),
                monthId = monthId,
                incomeSourceId = source.id,
                name = source.name!!,
                amount = source.amount!!,
                isAdhoc = false,
                now = OffsetDateTime.now(),
            )
        }
    }

    /** Add ad-hoc income (e.g., freelance). */
    fun createAdhoc(monthId: UUID, request: CreateIncomeEntryRequest): IncomeEntryResponse {
        val record = repository.insert(
            id = UuidV7.generate(),
            monthId = monthId,
            incomeSourceId = null,
            name = request.name,
            amount = request.amount,
            isAdhoc = true,
            now = OffsetDateTime.now(),
        )
        return toResponse(record)
    }

    /** Update amount or name (e.g., overtime adjustment). */
    fun update(id: UUID, request: UpdateIncomeEntryRequest): IncomeEntryResponse {
        val existing = repository.findById(id) ?: throw IncomeEntryNotFoundException(id)
        val record = repository.update(
            id = id,
            name = request.name ?: existing.name!!,
            amount = request.amount ?: existing.amount!!,
            now = OffsetDateTime.now(),
        )
        return toResponse(record)
    }

    /** Delete an ad-hoc income entry. */
    fun delete(id: UUID) {
        if (!repository.deleteById(id)) throw IncomeEntryNotFoundException(id)
    }

    private fun toResponse(record: IncomeEntriesRecord) = IncomeEntryResponse(
        id = record.id!!,
        financialMonthId = record.financialMonthId!!,
        incomeSourceId = record.incomeSourceId,
        name = record.name!!,
        amount = record.amount!!,
        isAdhoc = record.isAdhoc!!,
        createdAt = record.createdAt!!,
    )
}

class IncomeEntryNotFoundException(id: UUID) :
    EntityNotFoundException("Income entry not found: $id")
