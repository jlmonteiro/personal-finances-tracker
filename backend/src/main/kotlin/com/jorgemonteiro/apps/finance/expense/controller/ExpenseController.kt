package com.jorgemonteiro.apps.finance.expense.controller

import com.jorgemonteiro.apps.finance.expense.dto.CreateExpenseRequest
import com.jorgemonteiro.apps.finance.expense.dto.ExpenseResponse
import com.jorgemonteiro.apps.finance.expense.dto.UpdateExpenseRequest
import com.jorgemonteiro.apps.finance.expense.service.ExpenseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for expenses.
 */
@RestController
class ExpenseController(private val service: ExpenseService) {

    /** Lists expenses for a financial month. */
    @GetMapping("/api/v1/financial-months/{monthId}/expenses")
    fun listByMonth(@PathVariable monthId: UUID): ResponseEntity<List<ExpenseResponse>> =
        ResponseEntity.ok(service.listByMonth(monthId))

    /** Creates an expense within a financial month. */
    @PostMapping("/api/v1/financial-months/{monthId}/expenses")
    fun create(
        @PathVariable monthId: UUID,
        @Valid @RequestBody request: CreateExpenseRequest,
    ): ResponseEntity<ExpenseResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(monthId, request))

    /** Updates an expense. */
    @PatchMapping("/api/v1/expenses/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateExpenseRequest,
    ): ResponseEntity<ExpenseResponse> =
        ResponseEntity.ok(service.update(id, request))

    /** Deletes an expense. */
    @DeleteMapping("/api/v1/expenses/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
