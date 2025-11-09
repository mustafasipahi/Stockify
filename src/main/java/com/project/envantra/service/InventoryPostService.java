package com.project.envantra.service;

import com.project.envantra.converter.InventoryConverter;
import com.project.envantra.enums.InventoryStatus;
import com.project.envantra.exception.InventoryIdException;
import com.project.envantra.exception.InventoryNotFoundException;
import com.project.envantra.model.dto.InventoryDto;
import com.project.envantra.model.dto.SalesItemDto;
import com.project.envantra.model.dto.SalesPrepareDto;
import com.project.envantra.model.entity.InventoryEntity;
import com.project.envantra.model.request.InventoryCreateRequest;
import com.project.envantra.model.request.InventoryUpdateRequest;
import com.project.envantra.repository.InventoryRepository;
import com.project.envantra.validator.InventoryCreateValidator;
import com.project.envantra.validator.InventoryUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.project.envantra.util.InventoryStatusUtil.getInventoryStatus;
import static com.project.envantra.util.LoginContext.getUserId;

@Service
@RequiredArgsConstructor
public class InventoryPostService {

    private final InventoryRepository inventoryRepository;
    private final InventoryCreateValidator inventoryCreateValidator;
    private final InventoryUpdateValidator inventoryUpdateValidator;

    @Transactional
    public void saveDefault(Long productId) {
        Long userId = getUserId();
        Optional<InventoryEntity> optionalInventory = inventoryRepository.findByProductIdAndCreatorUserId(productId, userId);
        InventoryEntity inventoryEntity;
        if (optionalInventory.isPresent()) {
            inventoryEntity = optionalInventory.get();
            inventoryEntity.setActive(true);
        } else {
            inventoryEntity = InventoryConverter.toDefaultEntity(productId);
        }
        inventoryRepository.save(inventoryEntity);
    }

    @Transactional
    public InventoryDto save(InventoryCreateRequest request) {
        inventoryCreateValidator.validate(request);
        InventoryEntity inventoryEntity = InventoryConverter.toEntity(request);
        InventoryEntity savedInventoryEntity = inventoryRepository.save(inventoryEntity);
        return InventoryConverter.toIdDto(savedInventoryEntity);
    }

    @Transactional
    public InventoryDto update(InventoryUpdateRequest request) {
        if (request.getInventoryId() == null) {
            throw new InventoryIdException();
        }
        InventoryEntity inventoryEntity = inventoryRepository.findById(request.getInventoryId())
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
        return InventoryConverter.toIdDto(updatedInventoryEntity);
    }

    @Transactional
    public void decreaseInventory(SalesPrepareDto prepareDto) {
        Map<Long, Integer> productDecreaseProductCountMap = prepareDto.getSalesItems().stream()
                .collect(Collectors.toMap(SalesItemDto::getProductId, SalesItemDto::getProductCount));
        for (Map.Entry<Long, Integer> entry : productDecreaseProductCountMap.entrySet()) {
            Long productId = entry.getKey();
            Integer decreaseProductCount = entry.getValue();
            Long userId = getUserId();
            InventoryEntity inventoryEntity = inventoryRepository.findByProductIdAndCreatorUserId(productId, userId)
                    .orElseThrow(() -> new InventoryNotFoundException(productId));
            Integer newProductCount = inventoryEntity.getProductCount() - decreaseProductCount;
            InventoryStatus newStatus = getInventoryStatus(newProductCount, inventoryEntity.getCriticalProductCount());
            inventoryEntity.setProductCount(newProductCount);
            inventoryEntity.setStatus(newStatus);
            inventoryRepository.save(inventoryEntity);
        }
    }
}
