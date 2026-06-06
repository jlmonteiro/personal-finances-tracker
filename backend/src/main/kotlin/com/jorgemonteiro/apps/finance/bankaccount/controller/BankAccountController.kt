package com.jorgemonteiro.apps.finance.bankaccount.controller

import com.jorgemonteiro.apps.finance.bankaccount.dto.BankAccountResponse
import com.jorgemonteiro.apps.finance.bankaccount.dto.CreateBankAccountRequest
import com.jorgemonteiro.apps.finance.bankaccount.dto.UpdateBankAccountRequest
import com.jorgemonteiro.apps.finance.bankaccount.service.BankAccountService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/api/v1/bank-accounts")
class BankAccountController(private val service: BankAccountService) {

    @GetMapping
    fun list(): ResponseEntity<List<BankAccountResponse>> =
        ResponseEntity.ok(service.list())

    @PostMapping
    fun create(@Valid @RequestBody request: CreateBankAccountRequest): ResponseEntity<BankAccountResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    @PatchMapping("/{id}")
    fun update(@PathVariable id: UUID, @Valid @RequestBody request: UpdateBankAccountRequest): ResponseEntity<BankAccountResponse> =
        ResponseEntity.ok(service.update(id, request))

    @PostMapping("/{id}/logo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadLogo(@PathVariable id: UUID, @RequestParam("file") file: MultipartFile): ResponseEntity<Void> {
        service.uploadLogo(id, file.bytes, file.contentType ?: "image/png")
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/logo", produces = [MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE])
    fun getLogo(@PathVariable id: UUID): ResponseEntity<ByteArray> {
        val (logo, contentType) = service.getLogo(id)
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType)).body(logo)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
