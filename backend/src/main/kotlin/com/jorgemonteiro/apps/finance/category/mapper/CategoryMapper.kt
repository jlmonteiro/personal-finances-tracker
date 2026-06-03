package com.jorgemonteiro.apps.finance.category.mapper

import com.jorgemonteiro.apps.finance.category.dto.CategoryResponse
import com.jorgemonteiro.apps.finance.`data`.jooq.tables.records.CategoriesRecord
import org.mapstruct.Mapper

/**
 * MapStruct mapper for category entities.
 */
@Mapper(componentModel = "spring")
interface CategoryMapper {
    fun toResponse(record: CategoriesRecord): CategoryResponse
}
