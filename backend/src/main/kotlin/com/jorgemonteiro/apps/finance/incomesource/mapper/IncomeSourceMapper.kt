package com.jorgemonteiro.apps.finance.incomesource.mapper

import com.jorgemonteiro.apps.finance.common.Money
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.IncomeSourcesRecord
import com.jorgemonteiro.apps.finance.incomesource.dto.Frequency
import com.jorgemonteiro.apps.finance.incomesource.dto.IncomeSourceResponse
import com.jorgemonteiro.apps.finance.incomesource.dto.PaymentDateType
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.Named

/**
 * MapStruct mapper for income source record to DTO conversion.
 */
@Mapper(componentModel = "spring")
abstract class IncomeSourceMapper {

    /**
     * Maps a JOOQ IncomeSourcesRecord to an IncomeSourceResponse DTO.
     */
    @Mapping(target = "amount", source = ".", qualifiedByName = ["toMoney"])
    @Mapping(target = "frequency", source = "frequency", qualifiedByName = ["toFrequency"])
    @Mapping(target = "paymentDateType", source = "paymentDateType", qualifiedByName = ["toPaymentDateType"])
    abstract fun toResponse(record: IncomeSourcesRecord): IncomeSourceResponse

    @Named("toMoney")
    fun toMoney(record: IncomeSourcesRecord): Money {
        return Money(
            value = record.amount?.toPlainString() ?: "",
            currency = record.currency ?: "",
        )
    }

    @Named("toFrequency")
    fun toFrequency(value: String?): Frequency {
        return Frequency.valueOf(value!!)
    }

    @Named("toPaymentDateType")
    fun toPaymentDateType(value: String?): PaymentDateType {
        return PaymentDateType.valueOf(value!!)
    }
}
