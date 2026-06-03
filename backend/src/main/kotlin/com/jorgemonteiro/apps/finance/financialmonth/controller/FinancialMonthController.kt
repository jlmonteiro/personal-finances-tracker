package com.jorgemonteiro.apps.finance.financialmonth.controller

import com.jorgemonteiro.apps.finance.financialmonth.dto.CreateFinancialMonthRequest
import com.jorgemonteiro.apps.finance.financialmonth.dto.FinancialMonthResponse
import com.jorgemonteiro.apps.finance.financialmonth.dto.QuarterResponse
import com.jorgemonteiro.apps.finance.financialmonth.service.FinancialMonthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for financial months.
 */
@RestController
@RequestMapping("/api/v1/financial-months")
class FinancialMonthController(private val service: FinancialMonthService) {

    /** Lists all financial months. */
    @GetMapping
    fun list(): ResponseEntity<List<FinancialMonthResponse>> =
        ResponseEntity.ok(service.list())

    /** Gets a financial month by ID. */
    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): ResponseEntity<FinancialMonthResponse> =
        ResponseEntity.ok(service.get(id))

    /** Gets quarters for a financial month. */
    @GetMapping("/{id}/quarters")
    fun getQuarters(@PathVariable id: UUID): ResponseEntity<List<QuarterResponse>> =
        ResponseEntity.ok(service.getQuarters(id))

    /** Creates a new financial month. */
    @PostMapping
    fun create(@RequestBody request: CreateFinancialMonthRequest): ResponseEntity<FinancialMonthResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))
}
