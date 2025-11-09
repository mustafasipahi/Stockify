package com.project.envantra.validator;

import com.project.envantra.exception.InventoryCountException;
import com.project.envantra.exception.InventoryCriticalCountException;
import com.project.envantra.exception.ProductIdException;
import com.project.envantra.exception.InventoryPriceException;
import com.project.envantra.model.request.InventoryCreateRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@AllArgsConstructor
public class InventoryCreateValidator {

    public void validate(InventoryCreateRequest request) {
        validateProductId(request);
        validatePrice(request);
        validateProductCount(request);
        validateCriticalProductCount(request);
    }

    private void validateProductId(InventoryCreateRequest request) {
        if (request.getProductId() == null) {
            throw new ProductIdException();
        }
    }

    private void validatePrice(InventoryCreateRequest request) {
        if (request.getPrice() == null) {
            throw new InventoryPriceException();
        }
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InventoryPriceException();
        }
    }

    private void validateProductCount(InventoryCreateRequest request) {
        if (request.getProductCount() == null) {
            throw new InventoryCountException();
        }
        if (request.getProductCount() < 0) {
            throw new InventoryCountException();
        }
    }

    private void validateCriticalProductCount(InventoryCreateRequest request) {
        if (request.getCriticalProductCount() == null) {
            return;
        }
        if (request.getCriticalProductCount() < 0) {
            throw new InventoryCriticalCountException();
        }
    }
}
