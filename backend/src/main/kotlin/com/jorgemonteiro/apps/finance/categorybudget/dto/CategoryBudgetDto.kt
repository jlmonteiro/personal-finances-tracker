package com.jorgemonteiro.apps.finance.categorybudget.dto

import java.math.BigDecimal
import java.util.UUID

data class CategoryBudgetResponse(
    val id: UUID,
    val quarterId: UUID,
    val categoryId: UUID,
    val categoryName: String,
    val categoryIcon: String,
    val amount: BigDecimal,
)

data class UpdateCategoryBudgetRequest(
    val amount: BigDecimal,
)
