package com.stockify.project.converter;

import com.stockify.project.model.dto.BasketDto;
import com.stockify.project.model.entity.BasketEntity;
import com.stockify.project.model.request.BasketAddRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.stockify.project.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasketConverter {

    public static BasketDto toDto(BasketEntity basketEntity) {
        return BasketDto.builder()
                .id(basketEntity.getId())
                .creatorUserId(basketEntity.getCreatorUserId())
                .brokerId(basketEntity.getBrokerId())
                .productId(basketEntity.getProductId())
                .productCount(basketEntity.getProductCount())
                .createdDate(basketEntity.getCreatedDate())
                .build();
    }

    public static BasketEntity toEntity(BasketAddRequest request) {
        return BasketEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(request.getBrokerId())
                .productId(request.getProductId())
                .productCount(request.getProductCount())
                .build();
    }
}
