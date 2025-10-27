package com.stockify.project.service;

import com.stockify.project.converter.InventoryConverter;
import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.exception.InventoryIdException;
import com.stockify.project.exception.InventoryNotFoundException;
import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.dto.SalesItemDto;
import com.stockify.project.model.dto.SalesPrepareDto;
import com.stockify.project.model.entity.InventoryEntity;
import com.stockify.project.model.request.InventoryCreateRequest;
import com.stockify.project.model.request.InventoryUpdateRequest;
import com.stockify.project.repository.InventoryRepository;
import com.stockify.project.validator.InventoryCreateValidator;
import com.stockify.project.validator.InventoryUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.stockify.project.util.InventoryStatusUtil.getInventoryStatus;
import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@Service
@RequiredArgsConstructor
public class InventoryPostService {

    private final InventoryRepository inventoryRepository;
    private final InventoryCreateValidator inventoryCreateValidator;
    private final InventoryUpdateValidator inventoryUpdateValidator;
    private final InventoryConverter inventoryConverter;

    @Transactional
    public void saveDefault(Long productId) {
        Long userId = getUserId();
        Long tenantId = getTenantId();
        Optional<InventoryEntity> optionalInventory = inventoryRepository.findByIdAndOwnerUserIdAndTenantId(productId, userId, tenantId);
        InventoryEntity inventoryEntity;
        if (optionalInventory.isPresent()) {
            inventoryEntity = optionalInventory.get();
            inventoryEntity.setActive(true);
        } else {
            inventoryEntity = inventoryConverter.toDefaultEntity(productId);
        }
        inventoryRepository.save(inventoryEntity);
    }

    @Transactional
    public InventoryDto save(InventoryCreateRequest request) {
        inventoryCreateValidator.validate(request);
        InventoryEntity inventoryEntity = inventoryConverter.toEntity(request);
        InventoryEntity savedInventoryEntity = inventoryRepository.save(inventoryEntity);
        return inventoryConverter.toIdDto(savedInventoryEntity);
    }

    @Transactional
    public InventoryDto update(InventoryUpdateRequest request) {
        if (request.getInventoryId() == null) {
            throw new InventoryIdException();
        }
        Long userId = getUserId();
        Long tenantId = getTenantId();
        InventoryEntity inventoryEntity = inventoryRepository.findByIdAndOwnerUserIdAndTenantId(request.getInventoryId(), userId, tenantId)
                .orElseThrow(() -> new InventoryNotFoundException(request.getInventoryId()));
        if (request.getPrice() != null) {
            inventoryUpdateValidator.validatePrice(request.getPrice());
            inventoryEntity.setPrice(request.getPrice());
        }
        if (request.getProductCount() != null) {
            inventoryUpdateValidator.validateProductCount(request.getProductCount());
            inventoryEntity.setProductCount(request.getProductCount());
        }
        if (request.getCriticalProductCount() != null) {
            inventoryUpdateValidator.validateCriticalProductCount(request.getCriticalProductCount());
            inventoryEntity.setCriticalProductCount(request.getCriticalProductCount());
        }
        if (request.getActive() != null) {
            inventoryEntity.setActive(request.getActive());
        }
        inventoryEntity.setStatus(getInventoryStatus(inventoryEntity.getProductCount(), inventoryEntity.getCriticalProductCount()));
        InventoryEntity updatedInventoryEntity = inventoryRepository.save(inventoryEntity);
        return inventoryConverter.toIdDto(updatedInventoryEntity);
    }

    @Transactional
    public void decreaseAndCreateInventory(SalesPrepareDto prepareDto) {
        decreaseInventory(prepareDto);
    }

    private void decreaseInventory(SalesPrepareDto prepareDto) {
        Map<Long, Integer> productDecreaseProductCountMap = prepareDto.getSalesItems().stream()
                .collect(Collectors.toMap(SalesItemDto::getProductId, SalesItemDto::getProductCount));
        for (Map.Entry<Long, Integer> entry : productDecreaseProductCountMap.entrySet()) {
            Long productId = entry.getKey();
            Integer decreaseProductCount = entry.getValue();
            Long userId = getUserId();
            Long tenantId = getTenantId();
            InventoryEntity inventoryEntity = inventoryRepository.findByProductIdAndOwnerUserIdAndTenantId(productId, userId, tenantId)
                    .orElseThrow(() -> new InventoryNotFoundException(productId));
            Integer newProductCount = inventoryEntity.getProductCount() - decreaseProductCount;
            InventoryStatus newStatus = getInventoryStatus(newProductCount, inventoryEntity.getCriticalProductCount());
            inventoryEntity.setProductCount(newProductCount);
            inventoryEntity.setStatus(newStatus);
            inventoryRepository.save(inventoryEntity);
            createBrokerInventory(productId, prepareDto.getBroker().getBrokerUserId(), inventoryEntity.getPrice(), decreaseProductCount);
        }
    }

    private void createBrokerInventory(Long productId, Long ownerUserId, BigDecimal price, Integer productCount) {
        InventoryCreateRequest request = inventoryConverter.toRequest(productId, ownerUserId, price, productCount);
        InventoryEntity inventoryEntity = inventoryConverter.toEntity(request);
        inventoryRepository.save(inventoryEntity);
    }
}
