package com.jorgemonteiro.apps.finance.incomeentry.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.IncomeEntriesRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.INCOME_ENTRIES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class IncomeEntryRepository(private val dsl: DSLContext) {

    fun findByMonthId(monthId: UUID): List<IncomeEntriesRecord> =
        dsl.selectFrom(INCOME_ENTRIES)
            .where(INCOME_ENTRIES.FINANCIAL_MONTH_ID.eq(monthId))
            .orderBy(INCOME_ENTRIES.CREATED_AT)
            .fetch()

    fun findById(id: UUID): IncomeEntriesRecord? =
        dsl.selectFrom(INCOME_ENTRIES).where(INCOME_ENTRIES.ID.eq(id)).fetchOne()

    fun insert(id: UUID, monthId: UUID, incomeSourceId: UUID?, name: String, amount: BigDecimal, isAdhoc: Boolean, now: OffsetDateTime): IncomeEntriesRecord =
        dsl.insertInto(INCOME_ENTRIES)
            .set(INCOME_ENTRIES.ID, id)
            .set(INCOME_ENTRIES.FINANCIAL_MONTH_ID, monthId)
            .set(INCOME_ENTRIES.INCOME_SOURCE_ID, incomeSourceId)
            .set(INCOME_ENTRIES.NAME, name)
            .set(INCOME_ENTRIES.AMOUNT, amount)
            .set(INCOME_ENTRIES.IS_ADHOC, isAdhoc)
            .set(INCOME_ENTRIES.CREATED_AT, now)
            .set(INCOME_ENTRIES.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

    fun update(id: UUID, name: String, amount: BigDecimal, now: OffsetDateTime): IncomeEntriesRecord =
        dsl.update(INCOME_ENTRIES)
            .set(INCOME_ENTRIES.NAME, name)
            .set(INCOME_ENTRIES.AMOUNT, amount)
            .set(INCOME_ENTRIES.UPDATED_AT, now)
            .where(INCOME_ENTRIES.ID.eq(id))
            .returning()
            .fetchOne()!!

    fun deleteById(id: UUID): Boolean =
        dsl.deleteFrom(INCOME_ENTRIES).where(INCOME_ENTRIES.ID.eq(id)).execute() > 0
}
