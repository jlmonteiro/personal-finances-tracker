package com.jorgemonteiro.apps.finance.bankaccount.service

import com.jorgemonteiro.apps.finance.bankaccount.dto.BankAccountResponse
import com.jorgemonteiro.apps.finance.bankaccount.dto.CreateBankAccountRequest
import com.jorgemonteiro.apps.finance.bankaccount.dto.UpdateBankAccountRequest
import com.jorgemonteiro.apps.finance.bankaccount.repository.BankAccountRepository
import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.exception.EntityConflictException
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.BankAccountsRecord
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

@Service
class BankAccountService(private val repository: BankAccountRepository) {

    fun list(): List<BankAccountResponse> =
        repository.findAll().map { toResponse(it) }

    fun create(request: CreateBankAccountRequest): BankAccountResponse {
        val record = repository.insert(
            id = UuidV7.generate(),
            name = request.name,
            description = request.description,
            now = OffsetDateTime.now(),
        )
        return toResponse(record)
    }

    fun update(id: UUID, request: UpdateBankAccountRequest): BankAccountResponse {
        val existing = repository.findById(id) ?: throw BankAccountNotFoundException(id)
        val record = repository.update(
            id = id,
            name = request.name ?: existing.name!!,
            description = request.description ?: existing.description,
            now = OffsetDateTime.now(),
        )
        return toResponse(record)
    }

    fun uploadLogo(id: UUID, logo: ByteArray, contentType: String) {
        repository.findById(id) ?: throw BankAccountNotFoundException(id)
        repository.updateLogo(id, logo, contentType, OffsetDateTime.now())
    }

    fun getLogo(id: UUID): Pair<ByteArray, String> {
        return repository.getLogo(id) ?: throw BankAccountNotFoundException(id)
    }

    fun delete(id: UUID) {
        if (!repository.deleteById(id)) throw BankAccountNotFoundException(id)
    }

    private fun toResponse(record: BankAccountsRecord) = BankAccountResponse(
        id = record.id!!,
        name = record.name!!,
        description = record.description,
        hasLogo = record.logo != null,
        createdAt = record.createdAt!!,
    )
}

class BankAccountNotFoundException(id: UUID) :
    EntityNotFoundException("Bank account not found: $id")
