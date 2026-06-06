package com.jorgemonteiro.apps.finance.payee.controller

import com.jorgemonteiro.apps.finance.payee.dto.CreatePayeeRequest
import com.jorgemonteiro.apps.finance.payee.dto.PayeeResponse
import com.jorgemonteiro.apps.finance.payee.dto.UpdatePayeeRequest
import com.jorgemonteiro.apps.finance.payee.service.PayeeService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for payee management.
 */
@RestController
@RequestMapping("/api/v1/payees")
class PayeeController(private val service: PayeeService) {

    /** Lists all payees with their categories. */
    @GetMapping
    fun list(): ResponseEntity<List<PayeeResponse>> =
        ResponseEntity.ok(service.list())

    /** Creates a new payee. */
    @PostMapping
    fun create(@Valid @RequestBody request: CreatePayeeRequest): ResponseEntity<PayeeResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    /** Updates a payee. */
    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdatePayeeRequest,
    ): ResponseEntity<PayeeResponse> =
        ResponseEntity.ok(service.update(id, request))

    /** Deletes a payee. */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
