package com.stockify.project.service;

import com.stockify.project.converter.InventoryConverter;
import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.exception.InventoryNotFoundException;
import com.stockify.project.model.dto.InventoryDto;
import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.dto.SalesProductDto;
import com.stockify.project.model.entity.InventoryEntity;
import com.stockify.project.model.request.InventorySearchRequest;
import com.stockify.project.repository.InventoryRepository;
import com.stockify.project.specification.InventorySpecification;
import com.stockify.project.util.InventoryStatusUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.stockify.project.util.InventoryStatusUtil.isValid;

@Service
@RequiredArgsConstructor
public class InventoryGetService {

    private final InventoryRepository inventoryRepository;
    private final ProductGetService productGetService;

    public InventoryDto detail(Long inventoryId) {
        InventoryEntity inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
        ProductDto productDto = getProductDto(inventory.getProductId());
        InventoryDto inventoryDto = InventoryConverter.toDto(inventory, productDto);
        if (!isValid(inventoryDto)) {
            throw new InventoryNotFoundException(inventoryId);
        }
        return inventoryDto;
    }

    public List<InventoryDto> getAllInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(Collections.emptyList());
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return prepareInventoryList(specification);
    }

    public List<InventoryDto> getAvailableInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.AVAILABLE, InventoryStatus.CRITICAL));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return prepareInventoryList(specification);
    }

    public List<InventoryDto> getCriticalInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.CRITICAL));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return prepareInventoryList(specification);
    }

    public List<InventoryDto> getOutOfInventory() {
        InventorySearchRequest searchRequest = getInventorySearchRequest(List.of(InventoryStatus.OUT_OF_INVENTORY));
        Specification<InventoryEntity> specification = InventorySpecification.filter(searchRequest);
        return prepareInventoryList(specification);
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

    public boolean hasAvailableInventory(Long productId) {
        return getAvailableInventory().stream()
                .anyMatch(inventory -> inventory.getProduct().getProductId().equals(productId));
    }

    private List<InventoryDto> prepareInventoryList(Specification<InventoryEntity> specification) {
        List<InventoryEntity> inventoryList = inventoryRepository.findAll(specification);
        if (CollectionUtils.isEmpty(inventoryList)) {
            return Collections.emptyList();
        }
        Map<Long, ProductDto> productDtoMap = getProductDtoMap(inventoryList);
        return inventoryList.stream()
                .map(inventory -> InventoryConverter.toDto(inventory, productDtoMap.get(inventory.getProductId())))
                .filter(InventoryStatusUtil::isValid)
                .toList();
    }

    private InventorySearchRequest getInventorySearchRequest(List<InventoryStatus> statusList) {
        return InventorySearchRequest.builder()
                .statusList(statusList)
                .build();
    }
    
    private ProductDto getProductDto(Long productId) {
        return productGetService.detail(productId);
    }
    
    private Map<Long, ProductDto> getProductDtoMap(List<InventoryEntity> inventoryList) {
        List<Long> productIdList = inventoryList.stream()
                .map(InventoryEntity::getProductId)
                .distinct()
                .toList();
        return productGetService.detailMap(productIdList);
    }
}
