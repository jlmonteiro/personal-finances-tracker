package com.jorgemonteiro.apps.finance.category.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.CategoriesRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.CATEGORIES
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

/**
 * Repository for category persistence operations.
 */
@Repository
class CategoryRepository(private val dsl: DSLContext) {

    /** Retrieves all categories ordered by name. */
    fun findAll(): List<CategoriesRecord> =
        dsl.selectFrom(CATEGORIES)
            .orderBy(CATEGORIES.NAME)
            .fetch()

    /** Finds a category by ID. */
    fun findById(id: UUID): CategoriesRecord? =
        dsl.selectFrom(CATEGORIES)
            .where(CATEGORIES.ID.eq(id))
            .fetchOne()

    /** Inserts a new category and returns the record. */
    fun insert(id: UUID, name: String, icon: String, now: OffsetDateTime): CategoriesRecord =
        dsl.insertInto(CATEGORIES)
            .set(CATEGORIES.ID, id)
            .set(CATEGORIES.NAME, name)
            .set(CATEGORIES.ICON, icon)
            .set(CATEGORIES.CREATED_AT, now)
            .set(CATEGORIES.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

    /** Updates a category and returns the record. */
    fun update(id: UUID, name: String, icon: String, now: OffsetDateTime): CategoriesRecord =
        dsl.update(CATEGORIES)
            .set(CATEGORIES.NAME, name)
            .set(CATEGORIES.ICON, icon)
            .set(CATEGORIES.UPDATED_AT, now)
            .where(CATEGORIES.ID.eq(id))
            .returning()
            .fetchOne()!!

    /** Deletes a category by ID. Returns true if deleted. */
    fun deleteById(id: UUID): Boolean =
        dsl.deleteFrom(CATEGORIES)
            .where(CATEGORIES.ID.eq(id))
            .execute() > 0

    /** Checks if a category name already exists (excluding given ID). */
    fun existsByName(name: String, excludeId: UUID? = null): Boolean {
        val condition = CATEGORIES.NAME.equalIgnoreCase(name)
        val query = if (excludeId != null) {
            condition.and(CATEGORIES.ID.ne(excludeId))
        } else {
            condition
        }
        return dsl.fetchExists(dsl.selectFrom(CATEGORIES).where(query))
    }
}
