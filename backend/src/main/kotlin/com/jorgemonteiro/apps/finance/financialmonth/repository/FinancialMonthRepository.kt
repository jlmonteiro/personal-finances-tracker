package com.jorgemonteiro.apps.finance.financialmonth.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.FinancialMonthsRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.QuartersRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.FINANCIAL_MONTHS
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.QUARTERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Repository for financial months and quarters.
 */
@Repository
class FinancialMonthRepository(private val dsl: DSLContext) {

    fun findAll(): List<FinancialMonthsRecord> =
        dsl.selectFrom(FINANCIAL_MONTHS)
            .orderBy(FINANCIAL_MONTHS.START_DATE.desc())
            .fetch()

    fun findById(id: UUID): FinancialMonthsRecord? =
        dsl.selectFrom(FINANCIAL_MONTHS)
            .where(FINANCIAL_MONTHS.ID.eq(id))
            .fetchOne()

    fun findByStartDate(startDate: LocalDate): FinancialMonthsRecord? =
        dsl.selectFrom(FINANCIAL_MONTHS)
            .where(FINANCIAL_MONTHS.START_DATE.eq(startDate))
            .fetchOne()

    fun insert(id: UUID, startDate: LocalDate, endDate: LocalDate, now: OffsetDateTime): FinancialMonthsRecord =
        dsl.insertInto(FINANCIAL_MONTHS)
            .set(FINANCIAL_MONTHS.ID, id)
            .set(FINANCIAL_MONTHS.START_DATE, startDate)
            .set(FINANCIAL_MONTHS.END_DATE, endDate)
            .set(FINANCIAL_MONTHS.CREATED_AT, now)
            .set(FINANCIAL_MONTHS.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

    fun insertQuarter(id: UUID, monthId: UUID, number: Int, startDate: LocalDate, endDate: LocalDate): QuartersRecord =
        dsl.insertInto(QUARTERS)
            .set(QUARTERS.ID, id)
            .set(QUARTERS.FINANCIAL_MONTH_ID, monthId)
            .set(QUARTERS.QUARTER_NUMBER, number)
            .set(QUARTERS.START_DATE, startDate)
            .set(QUARTERS.END_DATE, endDate)
            .returning()
            .fetchOne()!!

    fun findQuartersByMonthId(monthId: UUID): List<QuartersRecord> =
        dsl.selectFrom(QUARTERS)
            .where(QUARTERS.FINANCIAL_MONTH_ID.eq(monthId))
            .orderBy(QUARTERS.QUARTER_NUMBER)
            .fetch()
}
