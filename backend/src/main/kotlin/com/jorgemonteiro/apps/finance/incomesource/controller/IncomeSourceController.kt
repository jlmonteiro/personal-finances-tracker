package com.jorgemonteiro.apps.finance.incomesource.controller

import com.jorgemonteiro.apps.finance.common.PaginatedResponse
import com.jorgemonteiro.apps.finance.incomesource.dto.CreateIncomeSourceRequest
import com.jorgemonteiro.apps.finance.incomesource.dto.IncomeSourceResponse
import com.jorgemonteiro.apps.finance.incomesource.dto.UpdateIncomeSourceRequest
import com.jorgemonteiro.apps.finance.incomesource.service.IncomeSourceService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for income sources.
 */
@RestController
@RequestMapping("/api/v1/income-sources")
class IncomeSourceController(private val service: IncomeSourceService) {

    /**
     * Lists all income sources with pagination.
     */
    @GetMapping
    fun list(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "20") size: Int,
    ): ResponseEntity<PaginatedResponse<IncomeSourceResponse>> {
        return ResponseEntity.ok(service.list(page, size))
    }

    /**
     * Creates a new income source.
     */
    @PostMapping
    fun create(@Valid @RequestBody request: CreateIncomeSourceRequest): ResponseEntity<IncomeSourceResponse> {
        val response = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    /**
     * Updates an existing income source.
     */
    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateIncomeSourceRequest,
    ): ResponseEntity<IncomeSourceResponse> {
        return ResponseEntity.ok(service.update(id, request))
    }

    /**
     * Deletes an income source.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: UUID) {
        service.delete(id)
    }
}
