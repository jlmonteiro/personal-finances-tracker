package com.jorgemonteiro.apps.finance.categorybudget.controller

import com.jorgemonteiro.apps.finance.categorybudget.dto.CategoryBudgetResponse
import com.jorgemonteiro.apps.finance.categorybudget.dto.UpdateCategoryBudgetRequest
import com.jorgemonteiro.apps.finance.categorybudget.service.CategoryBudgetService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/quarters/{quarterId}/budgets")
class CategoryBudgetController(private val service: CategoryBudgetService) {

    @GetMapping
    fun list(@PathVariable quarterId: UUID): ResponseEntity<List<CategoryBudgetResponse>> =
        ResponseEntity.ok(service.listByQuarter(quarterId))

    @PutMapping("/{categoryId}")
    fun upsert(
        @PathVariable quarterId: UUID,
        @PathVariable categoryId: UUID,
        @RequestBody request: UpdateCategoryBudgetRequest,
    ): ResponseEntity<CategoryBudgetResponse> =
        ResponseEntity.ok(service.upsert(quarterId, categoryId, request))
}
