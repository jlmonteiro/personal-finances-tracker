package com.jorgemonteiro.apps.finance.configuration.controller

import com.jorgemonteiro.apps.finance.configuration.dto.ConfigurationResponse
import com.jorgemonteiro.apps.finance.configuration.dto.CreateConfigurationRequest
import com.jorgemonteiro.apps.finance.configuration.dto.UpdateConfigurationRequest
import com.jorgemonteiro.apps.finance.configuration.service.ConfigurationService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for application configuration.
 */
@RestController
@RequestMapping("/api/v1/configuration")
class ConfigurationController(private val service: ConfigurationService) {

    /**
     * Retrieves the current application configuration.
     * Returns 404 if not yet configured.
     */
    @GetMapping
    fun get(): ResponseEntity<ConfigurationResponse> {
        val config = service.get() ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(config)
    }

    /**
     * Creates the initial application configuration.
     * Returns 409 if already configured.
     */
    @PostMapping
    fun create(@Valid @RequestBody request: CreateConfigurationRequest): ResponseEntity<ConfigurationResponse> {
        val config = service.create(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(config)
    }

    /**
     * Updates the existing application configuration.
     * Returns 404 if not yet configured.
     */
    @PatchMapping
    fun update(@Valid @RequestBody request: UpdateConfigurationRequest): ResponseEntity<ConfigurationResponse> {
        val config = service.update(request)
        return ResponseEntity.ok(config)
    }
}
