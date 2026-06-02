package com.jorgemonteiro.apps.finance.financialmonth.service

import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.configuration.repository.ConfigurationRepository
import com.jorgemonteiro.apps.finance.exception.EntityConflictException
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import com.jorgemonteiro.apps.finance.financialmonth.dto.CreateFinancialMonthRequest
import com.jorgemonteiro.apps.finance.financialmonth.dto.FinancialMonthResponse
import com.jorgemonteiro.apps.finance.financialmonth.dto.QuarterResponse
import com.jorgemonteiro.apps.finance.financialmonth.repository.FinancialMonthRepository
import com.jorgemonteiro.apps.finance.incomeentry.service.IncomeEntryService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID

/**
 * Service for managing financial months and auto-generating quarters.
 */
@Service
class FinancialMonthService(
    private val repository: FinancialMonthRepository,
    private val configurationRepository: ConfigurationRepository,
    private val incomeEntryService: IncomeEntryService,
) {

    /** Lists all financial months. */
    fun list(): List<FinancialMonthResponse> =
        repository.findAll().map { toResponse(it) }

    /** Gets a financial month by ID. */
    fun get(id: UUID): FinancialMonthResponse {
        val record = repository.findById(id) ?: throw FinancialMonthNotFoundException(id)
        return toResponse(record)
    }

    /** Gets quarters for a financial month. */
    fun getQuarters(monthId: UUID): List<QuarterResponse> {
        repository.findById(monthId) ?: throw FinancialMonthNotFoundException(monthId)
        return repository.findQuartersByMonthId(monthId).map {
            QuarterResponse(
                id = it.id!!,
                quarterNumber = it.quarterNumber!!,
                startDate = it.startDate!!,
                endDate = it.endDate!!,
            )
        }
    }

    /** Creates a financial month with auto-generated quarters. */
    fun create(request: CreateFinancialMonthRequest): FinancialMonthResponse {
        val config = configurationRepository.find()
            ?: throw IllegalStateException("Configuration not set up")

        val monthStartDay = config.monthStartDay!!
        val startDate = LocalDate.of(request.year, request.month, monthStartDay)
        val endDate = startDate.plusMonths(1).minusDays(1)

        if (repository.findByStartDate(startDate) != null) {
            throw FinancialMonthAlreadyExistsException(startDate)
        }

        val now = OffsetDateTime.now()
        val monthId = UuidV7.generate()
        val record = repository.insert(monthId, startDate, endDate, now)

        generateQuarters(monthId, startDate, endDate)
        incomeEntryService.generateForMonth(monthId)

        return toResponse(record)
    }

    private fun generateQuarters(monthId: UUID, monthStart: LocalDate, monthEnd: LocalDate) {
        val totalDays = ChronoUnit.DAYS.between(monthStart, monthEnd.plusDays(1)).toInt()
        val baseDays = totalDays / 4
        val remainder = totalDays % 4

        var current = monthStart
        for (q in 1..4) {
            val days = baseDays + if (q <= remainder) 1 else 0
            val qStart = current
            val qEnd = current.plusDays(days.toLong() - 1)
            repository.insertQuarter(UuidV7.generate(), monthId, q, qStart, qEnd)
            current = qEnd.plusDays(1)
        }
    }

    private fun toResponse(record: com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.FinancialMonthsRecord) =
        FinancialMonthResponse(
            id = record.id!!,
            startDate = record.startDate!!,
            endDate = record.endDate!!,
            createdAt = record.createdAt!!,
        )
}

class FinancialMonthNotFoundException(id: UUID) :
    EntityNotFoundException("Financial month not found: $id")

class FinancialMonthAlreadyExistsException(startDate: LocalDate) :
    EntityConflictException("Financial month starting $startDate already exists")
