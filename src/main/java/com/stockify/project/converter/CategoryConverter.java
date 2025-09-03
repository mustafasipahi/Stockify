package com.stockify.project.converter;

import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.entity.CategoryEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.stockify.project.util.DateUtil.getTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryConverter {

    public static CategoryDto toDto(CategoryEntity categoryEntity) {
        return CategoryDto.builder()
                .categoryId(categoryEntity.getId())
                .name(categoryEntity.getName())
                .taxRate(categoryEntity.getTaxRate())
                .createdDate(getTime(categoryEntity.getCreatedDate()))
                .build();
    }
}
