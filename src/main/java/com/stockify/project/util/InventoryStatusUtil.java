package com.stockify.project.util;

import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.enums.ProductStatus;
import com.stockify.project.model.dto.InventoryDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventoryStatusUtil {

    public static InventoryStatus getInventoryStatus(Integer productCount, Integer criticalProductCount) {
        if (productCount == null) {
            return InventoryStatus.OUT_OF_INVENTORY;
        }
        if (productCount <= 0) {
            return InventoryStatus.OUT_OF_INVENTORY;
        }
        if (criticalProductCount == null) {
            return InventoryStatus.AVAILABLE;
        }
        if (productCount <= criticalProductCount) {
            return InventoryStatus.CRITICAL;
        } else {
            return InventoryStatus.AVAILABLE;
        }
    }

    public static boolean isValid(InventoryDto inventoryDto) {
        if(inventoryDto == null) {
            return false;
        }
        if (!inventoryDto.isActive()) {
            return false;
        }
        if (inventoryDto.getProduct() == null) {
            return false;
        }
        return ProductStatus.ACTIVE.equals(inventoryDto.getProduct().getStatus());
    }
}
