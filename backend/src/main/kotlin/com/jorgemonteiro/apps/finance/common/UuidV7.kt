package com.jorgemonteiro.apps.finance.common

import com.github.f4b6a3.uuid.UuidCreator
import java.util.UUID

/**
 * Utility for generating UUIDv7 identifiers.
 */
object UuidV7 {

    /**
     * Generates a new time-ordered UUIDv7.
     */
    fun generate(): UUID = UuidCreator.getTimeOrderedEpoch()
}
