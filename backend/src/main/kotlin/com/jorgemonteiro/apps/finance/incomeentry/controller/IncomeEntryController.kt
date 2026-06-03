package com.jorgemonteiro.apps.finance.incomeentry.controller

import com.jorgemonteiro.apps.finance.incomeentry.dto.CreateIncomeEntryRequest
import com.jorgemonteiro.apps.finance.incomeentry.dto.IncomeEntryResponse
import com.jorgemonteiro.apps.finance.incomeentry.dto.UpdateIncomeEntryRequest
import com.jorgemonteiro.apps.finance.incomeentry.service.IncomeEntryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
class IncomeEntryController(private val service: IncomeEntryService) {

    @GetMapping("/api/v1/financial-months/{monthId}/income-entries")
    fun list(@PathVariable monthId: UUID): ResponseEntity<List<IncomeEntryResponse>> =
        ResponseEntity.ok(service.listByMonth(monthId))

    @PostMapping("/api/v1/financial-months/{monthId}/income-entries/generate")
    fun generate(@PathVariable monthId: UUID): ResponseEntity<Void> {
        service.generateForMonth(monthId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/api/v1/financial-months/{monthId}/income-entries")
    fun create(@PathVariable monthId: UUID, @Valid @RequestBody request: CreateIncomeEntryRequest): ResponseEntity<IncomeEntryResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.createAdhoc(monthId, request))

    @PatchMapping("/api/v1/income-entries/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: UpdateIncomeEntryRequest): ResponseEntity<IncomeEntryResponse> =
        ResponseEntity.ok(service.update(id, request))

    @DeleteMapping("/api/v1/income-entries/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
