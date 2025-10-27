package com.stockify.project.service;

import com.stockify.project.converter.InventoryConverter;
import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.enums.ProductStatus;
import com.stockify.project.exception.InventoryNotFoundException;
import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.dto.SalesProductDto;
import com.stockify.project.model.entity.InventoryEntity;
import com.stockify.project.model.request.InventorySearchRequest;
import com.stockify.project.repository.InventoryRepository;
import com.stockify.project.specification.InventorySpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@Service
@RequiredArgsConstructor
public class InventoryGetService {

    private final InventoryRepository inventoryRepository;
    private final InventoryConverter inventoryConverter;

    public InventoryDto detail(Long inventoryId) {
        Long userId = getUserId();
        Long tenantId = getTenantId();
        return inventoryRepository.findByIdAndOwnerUserIdAndTenantId(inventoryId, userId, tenantId)
                .map(inventoryConverter::toDto)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
    }

    public List<InventoryDto> getAllInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(Collections.emptyList());
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return inventoryRepository.findAll(specification).stream()
                .map(inventoryConverter::toDto)
                .filter(this::isValid)
                .toList();
    }

    public List<InventoryDto> getAvailableInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.AVAILABLE, InventoryStatus.CRITICAL));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return inventoryRepository.findAll(specification).stream()
                .map(inventoryConverter::toDto)
                .filter(this::isValid)
                .toList();
    }

    public List<InventoryDto> getCriticalInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.CRITICAL));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return inventoryRepository.findAll(specification).stream()
                .map(inventoryConverter::toDto)
                .filter(this::isValid)
                .toList();
    }

    public List<InventoryDto> getOutOfInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.OUT_OF_INVENTORY));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return inventoryRepository.findAll(specification).stream()
                .map(inventoryConverter::toDto)
                .filter(this::isValid)
                .toList();
    }

    public List<SalesProductDto> getSalesInventory() {
        return getAvailableInventory().stream()
                .map(inventory -> SalesProductDto.builder()
                        .productId(inventory.getProduct().getProductId())
                        .productName(inventory.getProduct().getName())
                        .productCount(inventory.getProductCount())
                        .price(inventory.getPrice())
                        .taxRate(inventory.getProduct().getTaxRate())
                        .build())
                .toList();
    }

    private InventorySearchRequest getInventorySearchRequest(List<InventoryStatus> statusList) {
        return InventorySearchRequest.builder()
                .statusList(statusList)
                .build();
    }

    private boolean isValid(InventoryDto inventoryDto) {
        if (!inventoryDto.isActive()) {
            return false;
        }
        if (!ProductStatus.ACTIVE.equals(inventoryDto.getProduct().getStatus())) {
           return false;
        }
       return true;
    }
}
