package com.jorgemonteiro.apps.finance.configuration.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.AppConfigurationRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.APP_CONFIGURATION
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Repository for app_configuration table access.
 */
@Repository
class ConfigurationRepository(private val dsl: DSLContext) {

    /**
     * Fetches the single configuration record, or null if not configured.
     */
    fun find(): AppConfigurationRecord? {
        return dsl.selectFrom(APP_CONFIGURATION).fetchOne()
    }

    /**
     * Inserts a new configuration record and returns the persisted row.
     */
    fun insert(id: UUID, currency: String, monthStartDay: Int, now: OffsetDateTime): AppConfigurationRecord {
        return dsl.insertInto(APP_CONFIGURATION)
            .set(APP_CONFIGURATION.ID, id)
            .set(APP_CONFIGURATION.CURRENCY, currency)
            .set(APP_CONFIGURATION.MONTH_START_DAY, monthStartDay)
            .set(APP_CONFIGURATION.CREATED_AT, now)
            .set(APP_CONFIGURATION.UPDATED_AT, now)
            .returning()
            .fetchOne()!!
    }

    /**
     * Updates an existing configuration record and returns the persisted row.
     */
    fun update(id: UUID, currency: String, monthStartDay: Int, now: OffsetDateTime): AppConfigurationRecord {
        return dsl.update(APP_CONFIGURATION)
            .set(APP_CONFIGURATION.CURRENCY, currency)
            .set(APP_CONFIGURATION.MONTH_START_DAY, monthStartDay)
            .set(APP_CONFIGURATION.UPDATED_AT, now)
            .where(APP_CONFIGURATION.ID.eq(id))
            .returning()
            .fetchOne()!!
    }
}
