package com.stockify.project.util;

import com.stockify.project.enums.InventoryStatus;
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
}
