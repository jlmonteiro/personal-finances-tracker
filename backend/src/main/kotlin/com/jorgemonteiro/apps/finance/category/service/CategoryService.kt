package com.jorgemonteiro.apps.finance.category.service

import com.jorgemonteiro.apps.finance.category.dto.CategoryResponse
import com.jorgemonteiro.apps.finance.category.dto.CreateCategoryRequest
import com.jorgemonteiro.apps.finance.category.dto.UpdateCategoryRequest
import com.jorgemonteiro.apps.finance.category.mapper.CategoryMapper
import com.jorgemonteiro.apps.finance.category.repository.CategoryRepository
import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.exception.EntityConflictException
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import org.springframework.stereotype.Service
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Service for managing expense categories.
 */
@Service
class CategoryService(
    private val repository: CategoryRepository,
    private val mapper: CategoryMapper,
) {

    /** Lists all categories. */
    fun list(): List<CategoryResponse> =
        repository.findAll().map { mapper.toResponse(it) }

    /** Creates a new category. Throws on duplicate name. */
    fun create(request: CreateCategoryRequest): CategoryResponse {
        if (repository.existsByName(request.name)) {
            throw CategoryNameConflictException(request.name)
        }
        val record = repository.insert(
            id = UuidV7.generate(),
            name = request.name,
            icon = request.icon,
            now = OffsetDateTime.now(),
        )
        return mapper.toResponse(record)
    }

    /** Updates an existing category. Throws if not found or name conflicts. */
    fun update(id: UUID, request: UpdateCategoryRequest): CategoryResponse {
        val existing = repository.findById(id) ?: throw CategoryNotFoundException(id)

        val name = request.name ?: existing.name!!
        val icon = request.icon ?: existing.icon!!

        if (request.name != null && repository.existsByName(name, excludeId = id)) {
            throw CategoryNameConflictException(name)
        }

        val record = repository.update(id = id, name = name, icon = icon, now = OffsetDateTime.now())
        return mapper.toResponse(record)
    }

    /** Deletes a category. Throws if not found. */
    fun delete(id: UUID) {
        if (!repository.deleteById(id)) {
            throw CategoryNotFoundException(id)
        }
    }
}

/** Thrown when a category is not found. */
class CategoryNotFoundException(id: UUID) :
    EntityNotFoundException("Category not found: $id")

/** Thrown when a category name already exists. */
class CategoryNameConflictException(name: String) :
    EntityConflictException("Category with name '$name' already exists")
