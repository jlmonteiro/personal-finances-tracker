package com.jorgemonteiro.apps.finance.recurringexpense.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.RecurringExpensesRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.RECURRING_EXPENSES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class RecurringExpenseRepository(private val dsl: DSLContext) {

    fun findAll(): List<RecurringExpensesRecord> =
        dsl.selectFrom(RECURRING_EXPENSES).orderBy(RECURRING_EXPENSES.TITLE).fetch()

    fun findById(id: UUID): RecurringExpensesRecord? =
        dsl.selectFrom(RECURRING_EXPENSES).where(RECURRING_EXPENSES.ID.eq(id)).fetchOne()

    fun insert(
        id: UUID, payeeId: UUID, categoryId: UUID, title: String, description: String?,
        expectedValue: BigDecimal, frequency: String, startDate: LocalDate, endDate: LocalDate?,
        now: OffsetDateTime,
    ): RecurringExpensesRecord =
        dsl.insertInto(RECURRING_EXPENSES)
            .set(RECURRING_EXPENSES.ID, id)
            .set(RECURRING_EXPENSES.PAYEE_ID, payeeId)
            .set(RECURRING_EXPENSES.CATEGORY_ID, categoryId)
            .set(RECURRING_EXPENSES.TITLE, title)
            .set(RECURRING_EXPENSES.DESCRIPTION, description)
            .set(RECURRING_EXPENSES.EXPECTED_VALUE, expectedValue)
            .set(RECURRING_EXPENSES.FREQUENCY, frequency)
            .set(RECURRING_EXPENSES.START_DATE, startDate)
            .set(RECURRING_EXPENSES.END_DATE, endDate)
            .set(RECURRING_EXPENSES.CREATED_AT, now)
            .set(RECURRING_EXPENSES.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

    fun update(
        id: UUID, title: String, description: String?, expectedValue: BigDecimal,
        frequency: String, endDate: LocalDate?, isActive: Boolean, now: OffsetDateTime,
    ): RecurringExpensesRecord =
        dsl.update(RECURRING_EXPENSES)
            .set(RECURRING_EXPENSES.TITLE, title)
            .set(RECURRING_EXPENSES.DESCRIPTION, description)
            .set(RECURRING_EXPENSES.EXPECTED_VALUE, expectedValue)
            .set(RECURRING_EXPENSES.FREQUENCY, frequency)
            .set(RECURRING_EXPENSES.END_DATE, endDate)
            .set(RECURRING_EXPENSES.IS_ACTIVE, isActive)
            .set(RECURRING_EXPENSES.UPDATED_AT, now)
            .where(RECURRING_EXPENSES.ID.eq(id))
            .returning()
            .fetchOne()!!

    fun deleteById(id: UUID): Boolean =
        dsl.deleteFrom(RECURRING_EXPENSES).where(RECURRING_EXPENSES.ID.eq(id)).execute() > 0
}
