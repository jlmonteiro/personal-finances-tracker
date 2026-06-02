package com.jorgemonteiro.apps.finance.expense.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.ExpensesRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.EXPENSES
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.QUARTERS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Repository for expense persistence.
 */
@Repository
class ExpenseRepository(private val dsl: DSLContext) {

    /** Finds expenses for a financial month (via quarter join). */
    fun findByMonthId(monthId: UUID): List<ExpensesRecord> =
        dsl.selectFrom(EXPENSES)
            .where(EXPENSES.QUARTER_ID.`in`(
                dsl.select(QUARTERS.ID).from(QUARTERS).where(QUARTERS.FINANCIAL_MONTH_ID.eq(monthId))
            ))
            .orderBy(EXPENSES.DUE_DATE)
            .fetch()

    fun findById(id: UUID): ExpensesRecord? =
        dsl.selectFrom(EXPENSES).where(EXPENSES.ID.eq(id)).fetchOne()

    fun insert(
        id: UUID, quarterId: UUID, payeeId: UUID, categoryId: UUID,
        title: String, description: String?, expectedValue: BigDecimal,
        dueDate: LocalDate, now: OffsetDateTime,
    ): ExpensesRecord =
        dsl.insertInto(EXPENSES)
            .set(EXPENSES.ID, id)
            .set(EXPENSES.QUARTER_ID, quarterId)
            .set(EXPENSES.PAYEE_ID, payeeId)
            .set(EXPENSES.CATEGORY_ID, categoryId)
            .set(EXPENSES.TITLE, title)
            .set(EXPENSES.DESCRIPTION, description)
            .set(EXPENSES.EXPECTED_VALUE, expectedValue)
            .set(EXPENSES.DUE_DATE, dueDate)
            .set(EXPENSES.CREATED_AT, now)
            .set(EXPENSES.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

    fun update(
        id: UUID, title: String, description: String?, expectedValue: BigDecimal,
        actualValue: BigDecimal?, dueDate: LocalDate, paymentDate: LocalDate?,
        isOverride: Boolean, now: OffsetDateTime,
    ): ExpensesRecord =
        dsl.update(EXPENSES)
            .set(EXPENSES.TITLE, title)
            .set(EXPENSES.DESCRIPTION, description)
            .set(EXPENSES.EXPECTED_VALUE, expectedValue)
            .set(EXPENSES.ACTUAL_VALUE, actualValue)
            .set(EXPENSES.DUE_DATE, dueDate)
            .set(EXPENSES.PAYMENT_DATE, paymentDate)
            .set(EXPENSES.IS_OVERRIDE, isOverride)
            .set(EXPENSES.UPDATED_AT, now)
            .where(EXPENSES.ID.eq(id))
            .returning()
            .fetchOne()!!

    fun deleteById(id: UUID): Boolean =
        dsl.deleteFrom(EXPENSES).where(EXPENSES.ID.eq(id)).execute() > 0

    fun existsByPayeeId(payeeId: UUID): Boolean =
        dsl.fetchExists(dsl.selectFrom(EXPENSES).where(EXPENSES.PAYEE_ID.eq(payeeId)))

    fun existsByCategoryId(categoryId: UUID): Boolean =
        dsl.fetchExists(dsl.selectFrom(EXPENSES).where(EXPENSES.CATEGORY_ID.eq(categoryId)))
}
