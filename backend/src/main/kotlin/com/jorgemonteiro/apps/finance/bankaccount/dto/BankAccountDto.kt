package com.jorgemonteiro.apps.finance.bankaccount.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.UUID

data class BankAccountResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val hasLogo: Boolean,
    val createdAt: OffsetDateTime,
)

data class CreateBankAccountRequest(
    @field:NotBlank @field:Size(max = 100) val name: String,
    val description: String? = null,
)

data class UpdateBankAccountRequest(
    @field:Size(max = 100) val name: String? = null,
    val description: String? = null,
)
