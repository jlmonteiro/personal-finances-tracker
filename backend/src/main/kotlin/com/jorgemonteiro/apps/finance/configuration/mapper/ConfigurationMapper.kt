package com.jorgemonteiro.apps.finance.configuration.mapper

import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.AppConfigurationRecord
import com.jorgemonteiro.apps.finance.configuration.dto.ConfigurationResponse
import org.mapstruct.Mapper

/**
 * MapStruct mapper for configuration record to DTO conversion.
 */
@Mapper(componentModel = "spring")
interface ConfigurationMapper {

    /**
     * Maps a JOOQ AppConfigurationRecord to a ConfigurationResponse DTO.
     */
    fun toResponse(record: AppConfigurationRecord): ConfigurationResponse
}
