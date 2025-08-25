package com.stockify.project.converter;

import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.entity.tenant.InventoryEntity;
import com.stockify.project.util.FinanceUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.stockify.project.util.InventoryStatusUtil.getInventoryStatus;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryConverter {

    public static InventoryDto toDto(InventoryEntity inventoryEntity) {
        return InventoryDto.builder()
                .inventoryId(inventoryEntity.getId())
                .product(ProductConverter.toDto(inventoryEntity.getProductEntity()))
                .price(inventoryEntity.getPrice())
                .totalPrice(FinanceUtil.multiply(inventoryEntity.getPrice(), inventoryEntity.getProductCount()))
                .productCount(inventoryEntity.getProductCount())
                .criticalProductCount(inventoryEntity.getCriticalProductCount())
                .status(getInventoryStatus(inventoryEntity.getProductCount(), inventoryEntity.getCriticalProductCount()))
                .createdDate(inventoryEntity.getCreatedDate())
                .lastModifiedDate(inventoryEntity.getLastModifiedDate())
                .build();
    }

    public static InventoryDto toIdDto(InventoryEntity inventoryEntity) {
        return InventoryDto.builder()
                .inventoryId(inventoryEntity.getId())
                .build();
    }
}
