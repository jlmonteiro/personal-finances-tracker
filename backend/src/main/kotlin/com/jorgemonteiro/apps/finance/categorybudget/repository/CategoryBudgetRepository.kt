package com.jorgemonteiro.apps.finance.categorybudget.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.CategoryBudgetsRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.CATEGORY_BUDGETS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class CategoryBudgetRepository(private val dsl: DSLContext) {

    fun findByQuarterId(quarterId: UUID): List<CategoryBudgetsRecord> =
        dsl.selectFrom(CATEGORY_BUDGETS)
            .where(CATEGORY_BUDGETS.QUARTER_ID.eq(quarterId))
            .fetch()

    fun findByQuarterAndCategory(quarterId: UUID, categoryId: UUID): CategoryBudgetsRecord? =
        dsl.selectFrom(CATEGORY_BUDGETS)
            .where(CATEGORY_BUDGETS.QUARTER_ID.eq(quarterId).and(CATEGORY_BUDGETS.CATEGORY_ID.eq(categoryId)))
            .fetchOne()

    fun insert(id: UUID, quarterId: UUID, categoryId: UUID, amount: BigDecimal, now: OffsetDateTime): CategoryBudgetsRecord =
        dsl.insertInto(CATEGORY_BUDGETS)
            .set(CATEGORY_BUDGETS.ID, id)
            .set(CATEGORY_BUDGETS.QUARTER_ID, quarterId)
            .set(CATEGORY_BUDGETS.CATEGORY_ID, categoryId)
            .set(CATEGORY_BUDGETS.AMOUNT, amount)
            .set(CATEGORY_BUDGETS.CREATED_AT, now)
            .set(CATEGORY_BUDGETS.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

    fun update(id: UUID, amount: BigDecimal, now: OffsetDateTime): CategoryBudgetsRecord =
        dsl.update(CATEGORY_BUDGETS)
            .set(CATEGORY_BUDGETS.AMOUNT, amount)
            .set(CATEGORY_BUDGETS.UPDATED_AT, now)
            .where(CATEGORY_BUDGETS.ID.eq(id))
            .returning()
            .fetchOne()!!
}
