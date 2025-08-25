package com.stockify.project.service;

import com.stockify.project.converter.InventoryConverter;
import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.exception.InventoryIdException;
import com.stockify.project.exception.InventoryNotFoundException;
import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.entity.tenant.InventoryEntity;
import com.stockify.project.model.entity.tenant.ProductEntity;
import com.stockify.project.model.request.InventoryCreateRequest;
import com.stockify.project.model.request.InventoryUpdateRequest;
import com.stockify.project.repository.tenant.InventoryRepository;
import com.stockify.project.specification.InventorySpecification;
import com.stockify.project.validator.InventoryCreateValidator;
import com.stockify.project.validator.InventoryUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.stockify.project.util.InventoryStatusUtil.getInventoryStatus;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductService productService;
    private final InventoryCreateValidator inventoryCreateValidator;
    private final InventoryUpdateValidator inventoryUpdateValidator;

    @Transactional
    public InventoryDto save(InventoryCreateRequest request) {
        inventoryCreateValidator.validate(request);
        ProductEntity productEntity = productService.findById(request.getProductId());
        InventoryEntity inventoryEntity = InventoryEntity.builder()
                .productEntity(productEntity)
                .price(request.getPrice())
                .productCount(request.getProductCount())
                .criticalProductCount(request.getCriticalProductCount())
                .status(getInventoryStatus(request.getProductCount(), request.getCriticalProductCount()))
                .build();
        return InventoryConverter.toIdDto(inventoryRepository.save(inventoryEntity));
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
        return InventoryConverter.toIdDto(inventoryRepository.save(inventoryEntity));
    }

    public List<InventoryDto> getAllInventory() {
        Specification<InventoryEntity> specification = InventorySpecification.filter();
        return inventoryRepository.findAll(specification).stream()
                .map(InventoryConverter::toDto)
                .toList();
    }

    public List<InventoryDto> getCriticalInventory() {
        Specification<InventoryEntity> specification = InventorySpecification.filter();
        return inventoryRepository.findAll(specification).stream()
                .filter(inventoryEntity -> InventoryStatus.CRITICAL.equals(inventoryEntity.getStatus()))
                .map(InventoryConverter::toDto)
                .toList();
    }

    public List<InventoryDto> getOutOfInventory() {
        Specification<InventoryEntity> specification = InventorySpecification.filter();
        return inventoryRepository.findAll(specification).stream()
                .filter(inventoryEntity -> InventoryStatus.OUT_OF_STOCK.equals(inventoryEntity.getStatus()))
                .map(InventoryConverter::toDto)
                .toList();
    }
}
