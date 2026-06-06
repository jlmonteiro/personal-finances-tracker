package com.jorgemonteiro.apps.finance.bankaccount.repository

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.BankAccountsRecord
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.references.BANK_ACCOUNTS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.UUID

@Repository
class BankAccountRepository(private val dsl: DSLContext) {

    fun findAll(): List<BankAccountsRecord> =
        dsl.selectFrom(BANK_ACCOUNTS).orderBy(BANK_ACCOUNTS.NAME).fetch()

    fun findById(id: UUID): BankAccountsRecord? =
        dsl.selectFrom(BANK_ACCOUNTS).where(BANK_ACCOUNTS.ID.eq(id)).fetchOne()

    fun insert(id: UUID, name: String, description: String?, now: OffsetDateTime): BankAccountsRecord =
        dsl.insertInto(BANK_ACCOUNTS)
            .set(BANK_ACCOUNTS.ID, id)
            .set(BANK_ACCOUNTS.NAME, name)
            .set(BANK_ACCOUNTS.DESCRIPTION, description)
            .set(BANK_ACCOUNTS.CREATED_AT, now)
            .set(BANK_ACCOUNTS.UPDATED_AT, now)
            .returning()
            .fetchOne()!!

    fun update(id: UUID, name: String, description: String?, now: OffsetDateTime): BankAccountsRecord =
        dsl.update(BANK_ACCOUNTS)
            .set(BANK_ACCOUNTS.NAME, name)
            .set(BANK_ACCOUNTS.DESCRIPTION, description)
            .set(BANK_ACCOUNTS.UPDATED_AT, now)
            .where(BANK_ACCOUNTS.ID.eq(id))
            .returning()
            .fetchOne()!!

    fun updateLogo(id: UUID, logo: ByteArray, contentType: String, now: OffsetDateTime) {
        dsl.update(BANK_ACCOUNTS)
            .set(BANK_ACCOUNTS.LOGO, logo)
            .set(BANK_ACCOUNTS.LOGO_CONTENT_TYPE, contentType)
            .set(BANK_ACCOUNTS.UPDATED_AT, now)
            .where(BANK_ACCOUNTS.ID.eq(id))
            .execute()
    }

    fun getLogo(id: UUID): Pair<ByteArray, String>? {
        val record = dsl.select(BANK_ACCOUNTS.LOGO, BANK_ACCOUNTS.LOGO_CONTENT_TYPE)
            .from(BANK_ACCOUNTS)
            .where(BANK_ACCOUNTS.ID.eq(id))
            .fetchOne() ?: return null
        val logo = record.get(BANK_ACCOUNTS.LOGO) ?: return null
        val contentType = record.get(BANK_ACCOUNTS.LOGO_CONTENT_TYPE) ?: "image/png"
        return logo to contentType
    }

    fun deleteById(id: UUID): Boolean =
        dsl.deleteFrom(BANK_ACCOUNTS).where(BANK_ACCOUNTS.ID.eq(id)).execute() > 0
}
