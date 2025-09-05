package com.stockify.project.service;

import com.stockify.project.converter.InventoryConverter;
import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.exception.InventoryIdException;
import com.stockify.project.exception.InventoryNotFoundException;
import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.entity.InventoryEntity;
import com.stockify.project.model.request.InventoryCreateRequest;
import com.stockify.project.model.request.InventorySearchRequest;
import com.stockify.project.model.request.InventoryUpdateRequest;
import com.stockify.project.repository.InventoryRepository;
import com.stockify.project.specification.InventorySpecification;
import com.stockify.project.validator.InventoryCreateValidator;
import com.stockify.project.validator.InventoryUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.stockify.project.constant.CacheConstants.*;
import static com.stockify.project.util.InventoryStatusUtil.getInventoryStatus;
import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InventoryCreateValidator inventoryCreateValidator;
    private final InventoryUpdateValidator inventoryUpdateValidator;
    private final InventoryConverter inventoryConverter;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = INVENTORY_ALL, allEntries = true),
            @CacheEvict(value = INVENTORY_AVAILABLE, allEntries = true),
            @CacheEvict(value = INVENTORY_CRITICAL, allEntries = true),
            @CacheEvict(value = INVENTORY_OUT_OF, allEntries = true)
    })
    public InventoryDto save(InventoryCreateRequest request) {
        inventoryCreateValidator.validate(request);
        InventoryEntity inventoryEntity = inventoryConverter.toEntity(request);
        InventoryEntity savedInventoryEntity = inventoryRepository.save(inventoryEntity);
        return inventoryConverter.toIdDto(savedInventoryEntity);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = INVENTORY_ALL, allEntries = true),
            @CacheEvict(value = INVENTORY_AVAILABLE, allEntries = true),
            @CacheEvict(value = INVENTORY_CRITICAL, allEntries = true),
            @CacheEvict(value = INVENTORY_OUT_OF, allEntries = true)
    })
    public InventoryDto update(InventoryUpdateRequest request) {
        if (request.getInventoryId() == null) {
            throw new InventoryIdException();
        }
        InventoryEntity inventoryEntity = inventoryRepository.findByIdAndActiveTrueAndTenantId(request.getInventoryId(), getTenantId())
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

    public InventoryDto detail(Long inventoryId) {
        return inventoryRepository.findByIdAndActiveTrueAndTenantId(inventoryId, getTenantId())
                .map(inventoryConverter::toDto)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
    }

    @Cacheable(value = INVENTORY_ALL)
    public List<InventoryDto> getAllInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(Collections.emptyList());
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return inventoryRepository.findAll(specification).stream()
                .map(inventoryConverter::toDto)
                .toList();
    }

    @Cacheable(value = INVENTORY_AVAILABLE)
    public List<InventoryDto> getAvailableInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.AVAILABLE, InventoryStatus.CRITICAL));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return inventoryRepository.findAll(specification).stream()
                .map(inventoryConverter::toDto)
                .toList();
    }

    @Cacheable(value = INVENTORY_CRITICAL)
    public List<InventoryDto> getCriticalInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.CRITICAL));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return inventoryRepository.findAll(specification).stream()
                .map(inventoryConverter::toDto)
                .toList();
    }

    @Cacheable(value = INVENTORY_OUT_OF)
    public List<InventoryDto> getOutOfInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.OUT_OF_INVENTORY));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return inventoryRepository.findAll(specification).stream()
                .map(inventoryConverter::toDto)
                .toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = INVENTORY_ALL, allEntries = true),
            @CacheEvict(value = INVENTORY_AVAILABLE, allEntries = true),
            @CacheEvict(value = INVENTORY_CRITICAL, allEntries = true),
            @CacheEvict(value = INVENTORY_OUT_OF, allEntries = true)
    })
    public void decreaseInventory(Map<Long, Integer> productDecreaseProductCountMap) {
        for (Map.Entry<Long, Integer> entry : productDecreaseProductCountMap.entrySet()) {
            Long productId = entry.getKey();
            Integer decreaseProductCount = entry.getValue();
            InventoryEntity inventoryEntity = inventoryRepository.findByProductIdAndTenantId(productId, getTenantId())
                    .orElseThrow(() -> new InventoryNotFoundException(productId));
            Integer newProductCount = inventoryEntity.getProductCount() - decreaseProductCount;
            InventoryStatus newStatus = getInventoryStatus(newProductCount, inventoryEntity.getCriticalProductCount());
            inventoryEntity.setProductCount(newProductCount);
            inventoryEntity.setStatus(newStatus);
            inventoryRepository.save(inventoryEntity);
        }
    }

    private InventorySearchRequest getInventorySearchRequest(List<InventoryStatus> statusList) {
        return InventorySearchRequest.builder()
                .statusList(statusList)
                .build();
    }
}
