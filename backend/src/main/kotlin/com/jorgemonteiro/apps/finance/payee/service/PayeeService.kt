package com.jorgemonteiro.apps.finance.payee.service

import com.jorgemonteiro.apps.finance.category.dto.CategoryResponse
import com.jorgemonteiro.apps.finance.category.mapper.CategoryMapper
import com.jorgemonteiro.apps.finance.category.repository.CategoryRepository
import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.exception.EntityConflictException
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import com.jorgemonteiro.apps.finance.exception.ValidationException
import com.jorgemonteiro.apps.finance.payee.dto.CreatePayeeRequest
import com.jorgemonteiro.apps.finance.payee.dto.PayeeResponse
import com.jorgemonteiro.apps.finance.payee.dto.UpdatePayeeRequest
import com.jorgemonteiro.apps.finance.payee.repository.PayeeRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Service for managing payees with category associations.
 */
@Service
class PayeeService(
    private val repository: PayeeRepository,
    private val categoryRepository: CategoryRepository,
    private val categoryMapper: CategoryMapper,
) {

    /** Lists all payees with their associated categories. */
    fun list(): List<PayeeResponse> =
        repository.findAll().map { record ->
            val categoryIds = repository.findCategoryIds(record.id!!)
            val categories = categoryIds.mapNotNull { categoryRepository.findById(it) }
                .map { categoryMapper.toResponse(it) }
            PayeeResponse(
                id = record.id!!,
                name = record.name!!,
                categories = categories,
                createdAt = record.createdAt!!,
                updatedAt = record.updatedAt!!,
            )
        }

    /** Creates a new payee with category associations. */
    fun create(request: CreatePayeeRequest): PayeeResponse {
        if (repository.existsByName(request.name)) {
            throw PayeeNameConflictException(request.name)
        }
        validateCategoryIds(request.categoryIds)

        val id = UuidV7.generate()
        val record = repository.insert(id = id, name = request.name, now = OffsetDateTime.now())
        repository.replaceCategoryAssociations(id, request.categoryIds)

        val categories = request.categoryIds.mapNotNull { categoryRepository.findById(it) }
            .map { categoryMapper.toResponse(it) }

        return PayeeResponse(
            id = record.id!!,
            name = record.name!!,
            categories = categories,
            createdAt = record.createdAt!!,
            updatedAt = record.updatedAt!!,
        )
    }

    /** Updates a payee. */
    fun update(id: UUID, request: UpdatePayeeRequest): PayeeResponse {
        val existing = repository.findById(id) ?: throw PayeeNotFoundException(id)

        val name = request.name ?: existing.name!!

        if (request.name != null && repository.existsByName(name, excludeId = id)) {
            throw PayeeNameConflictException(name)
        }

        if (request.categoryIds != null) {
            validateCategoryIds(request.categoryIds)
            repository.replaceCategoryAssociations(id, request.categoryIds)
        }

        val record = repository.update(id = id, name = name, now = OffsetDateTime.now())
        val categoryIds = repository.findCategoryIds(id)
        val categories = categoryIds.mapNotNull { categoryRepository.findById(it) }
            .map { categoryMapper.toResponse(it) }

        return PayeeResponse(
            id = record.id!!,
            name = record.name!!,
            categories = categories,
            createdAt = record.createdAt!!,
            updatedAt = record.updatedAt!!,
        )
    }

    /** Deletes a payee. */
    fun delete(id: UUID) {
        repository.findById(id) ?: throw PayeeNotFoundException(id)
        repository.deleteCategoryAssociations(id)
        repository.deleteById(id)
    }

    private fun validateCategoryIds(categoryIds: List<UUID>) {
        val missing = categoryIds.filter { categoryRepository.findById(it) == null }
        if (missing.isNotEmpty()) {
            throw PayeeCategoryValidationException(
                missing.map { "Category not found: $it" }
            )
        }
    }
}

class PayeeNotFoundException(id: UUID) :
    EntityNotFoundException("Payee not found: $id")

class PayeeNameConflictException(name: String) :
    EntityConflictException("Payee with name '$name' already exists")

class PayeeCategoryValidationException(errors: List<String>) :
    ValidationException("Invalid category associations", errors)
