package com.jorgemonteiro.apps.finance.incomesource.dto

/**
 * Payment frequency for an income source.
 */
enum class Frequency {
    MONTHLY,
    WEEKLY,
    FORTNIGHTLY,
    FOUR_WEEKLY,
}

/**
 * Type of payment date rule.
 */
enum class PaymentDateType {
    FIXED,
    RELATIVE,
}
