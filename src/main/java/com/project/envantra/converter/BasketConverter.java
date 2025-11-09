package com.project.envantra.converter;

import com.project.envantra.model.dto.BasketDto;
import com.project.envantra.model.entity.BasketEntity;
import com.project.envantra.model.request.BasketAddRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.project.envantra.util.LoginContext.getUserId;

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
