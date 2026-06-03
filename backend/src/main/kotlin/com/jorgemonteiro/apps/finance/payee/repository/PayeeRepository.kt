package com.jorgemonteiro.apps.finance.payee.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.PayeeCategoriesRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.PayeesRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.PAYEE_CATEGORIES
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.PAYEES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Repository for payee persistence operations.
 */
@Repository
class PayeeRepository(private val dsl: DSLContext) {

    /** Retrieves all payees ordered by name. */
    fun findAll(): List<PayeesRecord> =
        dsl.selectFrom(PAYEES)
            .orderBy(PAYEES.NAME)
            .fetch()

    /** Finds a payee by ID. */
    fun findById(id: UUID): PayeesRecord? =
        dsl.selectFrom(PAYEES)
            .where(PAYEES.ID.eq(id))
            .fetchOne()

    /** Inserts a new payee. */
    fun insert(id: UUID, name: String, now: OffsetDateTime): PayeesRecord =
        dsl.insertInto(PAYEES)
            .set(PAYEES.ID, id)
            .set(PAYEES.NAME, name)
            .set(PAYEES.CREATED_AT, now)
            .set(PAYEES.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

    /** Updates a payee. */
    fun update(id: UUID, name: String, now: OffsetDateTime): PayeesRecord =
        dsl.update(PAYEES)
            .set(PAYEES.NAME, name)
            .set(PAYEES.UPDATED_AT, now)
            .where(PAYEES.ID.eq(id))
            .returning()
            .fetchOne()!!

    /** Deletes a payee. Returns true if deleted. */
    fun deleteById(id: UUID): Boolean =
        dsl.deleteFrom(PAYEES)
            .where(PAYEES.ID.eq(id))
            .execute() > 0

    /** Checks if a payee name exists (excluding given ID). */
    fun existsByName(name: String, excludeId: UUID? = null): Boolean {
        val condition = PAYEES.NAME.equalIgnoreCase(name)
        val query = if (excludeId != null) condition.and(PAYEES.ID.ne(excludeId)) else condition
        return dsl.fetchExists(dsl.selectFrom(PAYEES).where(query))
    }

    /** Gets category IDs for a payee. */
    fun findCategoryIds(payeeId: UUID): List<UUID> =
        dsl.select(PAYEE_CATEGORIES.CATEGORY_ID)
            .from(PAYEE_CATEGORIES)
            .where(PAYEE_CATEGORIES.PAYEE_ID.eq(payeeId))
            .fetch(PAYEE_CATEGORIES.CATEGORY_ID)
            .filterNotNull()

    /** Replaces all category associations for a payee. */
    fun replaceCategoryAssociations(payeeId: UUID, categoryIds: List<UUID>) {
        deleteCategoryAssociations(payeeId)
        categoryIds.forEach { categoryId ->
            dsl.insertInto(PAYEE_CATEGORIES)
                .set(PAYEE_CATEGORIES.ID, UUID.randomUUID())
                .set(PAYEE_CATEGORIES.PAYEE_ID, payeeId)
                .set(PAYEE_CATEGORIES.CATEGORY_ID, categoryId)
                .execute()
        }
    }

    /** Deletes all category associations for a payee. */
    fun deleteCategoryAssociations(payeeId: UUID) {
        dsl.deleteFrom(PAYEE_CATEGORIES)
            .where(PAYEE_CATEGORIES.PAYEE_ID.eq(payeeId))
            .execute()
    }
}
