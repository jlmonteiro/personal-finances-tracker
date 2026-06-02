package com.jorgemonteiro.apps.finance.category.controller

import com.jorgemonteiro.apps.finance.category.dto.CategoryResponse
import com.jorgemonteiro.apps.finance.category.dto.CreateCategoryRequest
import com.jorgemonteiro.apps.finance.category.dto.UpdateCategoryRequest
import com.jorgemonteiro.apps.finance.category.service.CategoryService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.UUID

/**
 * REST controller for category management.
 */
@RestController
@RequestMapping("/api/v1/categories")
class CategoryController(private val service: CategoryService) {

    /** Lists all categories. */
    @GetMapping
    fun list(): ResponseEntity<List<CategoryResponse>> =
        ResponseEntity.ok(service.list())

    /** Creates a new category. */
    @PostMapping
    fun create(@Valid @RequestBody request: CreateCategoryRequest): ResponseEntity<CategoryResponse> =
        ResponseEntity.status(HttpStatus.CREATED).body(service.create(request))

    /** Updates an existing category. */
    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateCategoryRequest,
    ): ResponseEntity<CategoryResponse> =
        ResponseEntity.ok(service.update(id, request))

    /** Deletes a category. */
    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity.noContent().build()
    }
}
