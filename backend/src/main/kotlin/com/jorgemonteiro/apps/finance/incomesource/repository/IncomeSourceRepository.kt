package com.jorgemonteiro.apps.finance.incomesource.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.IncomeSourcesRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.INCOME_SOURCES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Repository for income_sources table access.
 */
@Repository
class IncomeSourceRepository(private val dsl: DSLContext) {

    /**
     * Fetches a paginated list of income sources.
     */
    fun findAll(offset: Int, limit: Int): List<IncomeSourcesRecord> {
        return dsl.selectFrom(INCOME_SOURCES)
            .orderBy(INCOME_SOURCES.CREATED_AT.desc())
            .limit(limit)
            .offset(offset)
            .fetch()
    }

    /**
     * Counts all income sources.
     */
    fun count(): Long {
        return dsl.selectCount().from(INCOME_SOURCES).fetchOne(0, Long::class.java) ?: 0
    }

    /**
     * Fetches an income source by ID, or null if not found.
     */
    fun findById(id: UUID): IncomeSourcesRecord? {
        return dsl.selectFrom(INCOME_SOURCES)
            .where(INCOME_SOURCES.ID.eq(id))
            .fetchOne()
    }

    /**
     * Inserts a new income source and returns the persisted row.
     */
    fun insert(
        id: UUID,
        name: String,
        description: String?,
        amount: BigDecimal,
        currency: String,
        frequency: String,
        paymentDateType: String,
        paymentDateRule: String,
        startDate: LocalDate,
        endDate: LocalDate?,
        now: OffsetDateTime,
    ): IncomeSourcesRecord {
        return dsl.insertInto(INCOME_SOURCES)
            .set(INCOME_SOURCES.ID, id)
            .set(INCOME_SOURCES.NAME, name)
            .set(INCOME_SOURCES.DESCRIPTION, description)
            .set(INCOME_SOURCES.AMOUNT, amount)
            .set(INCOME_SOURCES.CURRENCY, currency)
            .set(INCOME_SOURCES.FREQUENCY, frequency)
            .set(INCOME_SOURCES.PAYMENT_DATE_TYPE, paymentDateType)
            .set(INCOME_SOURCES.PAYMENT_DATE_RULE, paymentDateRule)
            .set(INCOME_SOURCES.START_DATE, startDate)
            .set(INCOME_SOURCES.END_DATE, endDate)
            .set(INCOME_SOURCES.IS_ACTIVE, true)
            .set(INCOME_SOURCES.CREATED_AT, now)
            .set(INCOME_SOURCES.UPDATED_AT, now)
            .returning()
            .fetchOne()!!
    }

    /**
     * Updates an income source and returns the persisted row.
     */
    fun update(
        id: UUID,
        name: String,
        description: String?,
        amount: BigDecimal,
        currency: String,
        frequency: String,
        paymentDateType: String,
        paymentDateRule: String,
        startDate: LocalDate,
        endDate: LocalDate?,
        isActive: Boolean,
        now: OffsetDateTime,
    ): IncomeSourcesRecord {
        return dsl.update(INCOME_SOURCES)
            .set(INCOME_SOURCES.NAME, name)
            .set(INCOME_SOURCES.DESCRIPTION, description)
            .set(INCOME_SOURCES.AMOUNT, amount)
            .set(INCOME_SOURCES.CURRENCY, currency)
            .set(INCOME_SOURCES.FREQUENCY, frequency)
            .set(INCOME_SOURCES.PAYMENT_DATE_TYPE, paymentDateType)
            .set(INCOME_SOURCES.PAYMENT_DATE_RULE, paymentDateRule)
            .set(INCOME_SOURCES.START_DATE, startDate)
            .set(INCOME_SOURCES.END_DATE, endDate)
            .set(INCOME_SOURCES.IS_ACTIVE, isActive)
            .set(INCOME_SOURCES.UPDATED_AT, now)
            .where(INCOME_SOURCES.ID.eq(id))
            .returning()
            .fetchOne()!!
    }

    /**
     * Deletes an income source by ID. Returns true if deleted.
     */
    fun deleteById(id: UUID): Boolean {
        return dsl.deleteFrom(INCOME_SOURCES)
            .where(INCOME_SOURCES.ID.eq(id))
            .execute() > 0
    }
}
