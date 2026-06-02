package com.jorgemonteiro.apps.finance.common

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

/**
 * Monetary value with currency, following API guidelines.
 */
data class Money(
    @field:NotBlank
    val value: String = "",

    @field:NotBlank
    @field:Size(min = 3, max = 3)
    val currency: String = "",
)
