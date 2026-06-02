package com.jorgemonteiro.apps.finance.configuration.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Response DTO for application configuration.
 */
data class ConfigurationResponse(
    val id: UUID,
    val currency: String,
    val monthStartDay: Int,
    val createdAt: OffsetDateTime,
)

/**
 * Request DTO for creating application configuration.
 */
data class CreateConfigurationRequest(
    @field:NotBlank
    @field:Size(min = 3, max = 3)
    val currency: String,

    @field:Min(1)
    @field:Max(28)
    val monthStartDay: Int = 1,
)

/**
 * Request DTO for updating application configuration.
 */
data class UpdateConfigurationRequest(
    @field:Size(min = 3, max = 3)
    val currency: String? = null,

    @field:Min(1)
    @field:Max(28)
    val monthStartDay: Int? = null,
)
