package com.jorgemonteiro.apps.finance.categorybudget.service

import com.jorgemonteiro.apps.finance.category.repository.CategoryRepository
import com.jorgemonteiro.apps.finance.categorybudget.dto.CategoryBudgetResponse
import com.jorgemonteiro.apps.finance.categorybudget.dto.UpdateCategoryBudgetRequest
import com.jorgemonteiro.apps.finance.categorybudget.repository.CategoryBudgetRepository
import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID

@Service
class CategoryBudgetService(
    private val repository: CategoryBudgetRepository,
    private val categoryRepository: CategoryRepository,
) {

    /** Gets all budgets for a quarter. */
    fun listByQuarter(quarterId: UUID): List<CategoryBudgetResponse> =
        repository.findByQuarterId(quarterId).map { toResponse(it) }

    /** Updates (or creates) a budget for a category in a quarter. */
    fun upsert(quarterId: UUID, categoryId: UUID, request: UpdateCategoryBudgetRequest): CategoryBudgetResponse {
        val existing = repository.findByQuarterAndCategory(quarterId, categoryId)
        val record = if (existing != null) {
            repository.update(existing.id!!, request.amount, OffsetDateTime.now())
        } else {
            repository.insert(UuidV7.generate(), quarterId, categoryId, request.amount, OffsetDateTime.now())
        }
        return toResponse(record)
    }

    /** Auto-creates a budget at 0 if one doesn't exist. */
    fun ensureBudgetExists(quarterId: UUID, categoryId: UUID) {
        if (repository.findByQuarterAndCategory(quarterId, categoryId) == null) {
            repository.insert(UuidV7.generate(), quarterId, categoryId, BigDecimal.ZERO, OffsetDateTime.now())
        }
    }

    /** Deletes a category budget. */
    fun delete(quarterId: UUID, categoryId: UUID) {
        val record = repository.findByQuarterAndCategory(quarterId, categoryId) ?: return
        repository.deleteById(record.id!!)
    }

    private fun toResponse(record: com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.CategoryBudgetsRecord): CategoryBudgetResponse {
        val category = categoryRepository.findById(record.categoryId!!)!!
        return CategoryBudgetResponse(
            id = record.id!!,
            quarterId = record.quarterId!!,
            categoryId = record.categoryId!!,
            categoryName = category.name!!,
            categoryIcon = category.icon!!,
            amount = record.amount!!,
        )
    }
}
