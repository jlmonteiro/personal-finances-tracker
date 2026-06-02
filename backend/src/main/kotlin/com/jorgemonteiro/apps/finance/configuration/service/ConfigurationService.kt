package com.jorgemonteiro.apps.finance.configuration.service

import com.jorgemonteiro.apps.finance.common.UuidV7
import com.jorgemonteiro.apps.finance.configuration.dto.ConfigurationResponse
import com.jorgemonteiro.apps.finance.configuration.dto.CreateConfigurationRequest
import com.jorgemonteiro.apps.finance.configuration.dto.UpdateConfigurationRequest
import com.jorgemonteiro.apps.finance.configuration.mapper.ConfigurationMapper
import com.jorgemonteiro.apps.finance.exception.EntityConflictException
import com.jorgemonteiro.apps.finance.exception.EntityNotFoundException
import com.jorgemonteiro.apps.finance.configuration.repository.ConfigurationRepository
import org.springframework.stereotype.Service
import java.time.OffsetDateTime

/**
 * Service for managing application configuration.
 */
@Service
class ConfigurationService(
    private val repository: ConfigurationRepository,
    private val mapper: ConfigurationMapper,
) {

    /**
     * Retrieves the application configuration, or null if not yet configured.
     */
    fun get(): ConfigurationResponse? {
        return repository.find()?.let { mapper.toResponse(it) }
    }

    /**
     * Creates the application configuration. Throws if already exists.
     */
    fun create(request: CreateConfigurationRequest): ConfigurationResponse {
        if (repository.find() != null) {
            throw ConfigurationAlreadyExistsException()
        }

        val record = repository.insert(
            id = UuidV7.generate(),
            currency = request.currency,
            monthStartDay = request.monthStartDay,
            now = OffsetDateTime.now(),
        )

        return mapper.toResponse(record)
    }

    /**
     * Updates the application configuration. Throws if not found.
     */
    fun update(request: UpdateConfigurationRequest): ConfigurationResponse {
        val existing = repository.find() ?: throw ConfigurationNotFoundException()

        val currency = request.currency ?: existing.currency
        val monthStartDay = request.monthStartDay ?: existing.monthStartDay!!

        val record = repository.update(
            id = existing.id,
            currency = currency,
            monthStartDay = monthStartDay,
            now = OffsetDateTime.now(),
        )

        return mapper.toResponse(record)
    }
}

/**
 * Thrown when attempting to create configuration that already exists.
 */
class ConfigurationAlreadyExistsException :
    EntityConflictException("Configuration already exists")

/**
 * Thrown when configuration is not found.
 */
class ConfigurationNotFoundException :
    EntityNotFoundException("Configuration not found")
