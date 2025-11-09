package com.project.envantra.converter;

import com.project.envantra.enums.InventoryStatus;
import com.project.envantra.model.dto.InventoryDto;
import com.project.envantra.model.dto.ProductDto;
import com.project.envantra.model.entity.InventoryEntity;
import com.project.envantra.model.request.InventoryCreateRequest;
import com.project.envantra.util.FinanceUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.project.envantra.util.DateUtil.getTime;
import static com.project.envantra.util.InventoryStatusUtil.getInventoryStatus;
import static com.project.envantra.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryConverter {

    public static InventoryEntity toDefaultEntity(Long productId) {
        return InventoryEntity.builder()
                .productId(productId)
                .creatorUserId(getUserId())
                .active(true)
                .price(BigDecimal.ZERO)
                .productCount(0)
                .criticalProductCount(0)
                .status(InventoryStatus.OUT_OF_INVENTORY)
                .build();
    }

    public static InventoryEntity toEntity(InventoryCreateRequest request) {
        return InventoryEntity.builder()
                .productId(request.getProductId())
                .creatorUserId(getUserId())
                .price(request.getPrice())
                .active(true)
                .productCount(request.getProductCount())
                .criticalProductCount(request.getCriticalProductCount())
                .status(getInventoryStatus(request.getProductCount(), request.getCriticalProductCount()))
                .build();
    }

    public static InventoryDto toDto(InventoryEntity inventoryEntity, ProductDto productDto) {
        return InventoryDto.builder()
                .inventoryId(inventoryEntity.getId())
                .product(productDto)
                .price(inventoryEntity.getPrice())
                .totalPrice(FinanceUtil.multiply(inventoryEntity.getPrice(), inventoryEntity.getProductCount()))
                .productCount(inventoryEntity.getProductCount())
                .criticalProductCount(inventoryEntity.getCriticalProductCount())
                .active(inventoryEntity.isActive())
                .status(getInventoryStatus(inventoryEntity.getProductCount(), inventoryEntity.getCriticalProductCount()))
                .createdDate(getTime(inventoryEntity.getCreatedDate()))
                .lastModifiedDate(getTime(inventoryEntity.getLastModifiedDate()))
                .build();
    }

    public static InventoryDto toIdDto(InventoryEntity inventoryEntity) {
        return InventoryDto.builder()
                .inventoryId(inventoryEntity.getId())
                .active(inventoryEntity.isActive())
                .build();
    }
}
