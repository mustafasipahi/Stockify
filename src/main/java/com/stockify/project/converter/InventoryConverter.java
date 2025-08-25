package com.stockify.project.converter;

import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.tenant.InventoryEntity;
import com.stockify.project.service.ProductService;
import com.stockify.project.util.FinanceUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.stockify.project.util.InventoryStatusUtil.getInventoryStatus;

@Component
@AllArgsConstructor
public class InventoryConverter {

    private final ProductService productService;

    public InventoryDto toDto(InventoryEntity inventoryEntity) {
        return InventoryDto.builder()
                .inventoryId(inventoryEntity.getId())
                .product(getProductDto(inventoryEntity.getProductId()))
                .price(inventoryEntity.getPrice())
                .totalPrice(FinanceUtil.multiply(inventoryEntity.getPrice(), inventoryEntity.getProductCount()))
                .productCount(inventoryEntity.getProductCount())
                .criticalProductCount(inventoryEntity.getCriticalProductCount())
                .status(getInventoryStatus(inventoryEntity.getProductCount(), inventoryEntity.getCriticalProductCount()))
                .createdDate(inventoryEntity.getCreatedDate())
                .lastModifiedDate(inventoryEntity.getLastModifiedDate())
                .build();
    }

    public InventoryDto toIdDto(InventoryEntity inventoryEntity) {
        return InventoryDto.builder()
                .inventoryId(inventoryEntity.getId())
                .build();
    }

    private ProductDto getProductDto(Long productId) {
        return productService.detail(productId);
    }
}
