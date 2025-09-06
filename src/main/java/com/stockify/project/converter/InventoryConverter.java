package com.stockify.project.converter;

import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.InventoryEntity;
import com.stockify.project.model.request.InventoryCreateRequest;
import com.stockify.project.service.ProductGetService;
import com.stockify.project.util.FinanceUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.InventoryStatusUtil.getInventoryStatus;
import static com.stockify.project.util.TenantContext.getTenantId;

@Component
@AllArgsConstructor
public class InventoryConverter {

    private final ProductGetService productGetService;

    public InventoryEntity toDefaultEntity(Long productId) {
        return InventoryEntity.builder()
                .productId(productId)
                .active(true)
                .price(BigDecimal.ZERO)
                .productCount(0)
                .criticalProductCount(0)
                .status(InventoryStatus.OUT_OF_INVENTORY)
                .tenantId(getTenantId())
                .build();
    }

    public InventoryEntity toEntity(InventoryCreateRequest request) {
        return InventoryEntity.builder()
                .productId(request.getProductId())
                .price(request.getPrice())
                .active(true)
                .productCount(request.getProductCount())
                .criticalProductCount(request.getCriticalProductCount())
                .status(getInventoryStatus(request.getProductCount(), request.getCriticalProductCount()))
                .tenantId(getTenantId())
                .build();
    }

    public InventoryDto toDto(InventoryEntity inventoryEntity) {
        return InventoryDto.builder()
                .inventoryId(inventoryEntity.getId())
                .product(getProductDto(inventoryEntity.getProductId()))
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

    public InventoryDto toIdDto(InventoryEntity inventoryEntity) {
        return InventoryDto.builder()
                .inventoryId(inventoryEntity.getId())
                .build();
    }

    private ProductDto getProductDto(Long productId) {
        return productGetService.detail(productId);
    }
}
