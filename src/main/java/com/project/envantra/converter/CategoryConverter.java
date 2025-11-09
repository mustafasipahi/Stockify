package com.project.envantra.converter;

import com.project.envantra.model.dto.CategoryDto;
import com.project.envantra.model.entity.CategoryEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.project.envantra.util.DateUtil.getTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CategoryConverter {

    public static CategoryDto toDto(CategoryEntity categoryEntity) {
        return CategoryDto.builder()
                .categoryId(categoryEntity.getId())
                .name(categoryEntity.getName())
                .status(categoryEntity.getStatus())
                .taxRate(categoryEntity.getTaxRate())
                .createdDate(getTime(categoryEntity.getCreatedDate()))
                .build();
    }
}
