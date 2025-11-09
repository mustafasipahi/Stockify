package com.project.envantra.validator;

import com.project.envantra.exception.InventoryCountException;
import com.project.envantra.exception.InventoryCriticalCountException;
import com.project.envantra.exception.InventoryPriceException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class InventoryUpdateValidator {

    public void validatePrice(BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InventoryPriceException();
        }
    }

    public void validateProductCount(Integer productCount) {
        if (productCount < 0) {
            throw new InventoryCountException();
        }
    }

    public void validateCriticalProductCount(Integer criticalProductCount) {
        if (criticalProductCount < 0) {
            throw new InventoryCriticalCountException();
        }
    }
}
