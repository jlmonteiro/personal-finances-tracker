package com.jorgemonteiro.apps.finance.recurringexpense.controller

import com.jorgemonteiro.apps.finance.recurringexpense.dto.CreateRecurringExpenseRequest
import com.jorgemonteiro.apps.finance.recurringexpense.dto.RecurringExpenseResponse
import com.jorgemonteiro.apps.finance.recurringexpense.dto.UpdateRecurringExpenseRequest
import com.jorgemonteiro.apps.finance.recurringexpense.service.RecurringExpenseService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/v1/recurring-expenses")
class RecurringExpenseController(private val service: RecurringExpenseService) {

    @GetMapping
    fun list(): ResponseEntity<List<RecurringExpenseResponse>> =
        ResponseEntity.ok(service.list())

    @PostMapping
    fun create(@Valid @RequestBody request: CreateRecurringExpenseRequest): ResponseEntity<RecurringExpenseResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateRecurringExpenseRequest,
    ): ResponseEntity<RecurringExpenseResponse> =
        ResponseEntity.ok(service.update(id, request))

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
