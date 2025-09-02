package com.stockify.project.service;

import com.stockify.project.converter.SalesConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.exception.InsufficientInventoryException;
import com.stockify.project.exception.InventoryCountException;
import com.stockify.project.exception.ProductNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.SalesPrepareDto;
import com.stockify.project.model.dto.SalesPriceDto;
import com.stockify.project.model.dto.SalesProductDto;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.entity.SalesItemEntity;
import com.stockify.project.model.request.SalesProductRequest;
import com.stockify.project.model.request.SalesRequest;
import com.stockify.project.model.response.SalesResponse;
import com.stockify.project.repository.SalesItemRepository;
import com.stockify.project.repository.SalesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.stockify.project.constant.DocumentNumberConstants.*;
import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.validator.SalesValidator.validate;

@Service
@RequiredArgsConstructor
public class SalesPersistenceService {

    private final SalesRepository salesRepository;
    private final SalesItemRepository salesItemRepository;
    private final InventoryService inventoryService;
    private final BrokerService brokerService;
    private final TransactionService transactionService;

    @Transactional
    public SalesResponse salesPreview(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request, null);
        return SalesConverter.toResponse(prepareDto.getSalesEntity(), prepareDto.getSalesItems());
    }

    @Transactional
    public SalesResponse salesConfirm(SalesRequest request) {
        Long tenantId = getTenantId();
        SalesPrepareDto prepareDto = prepareSalesFlow(request, tenantId);
        setDocumentNumber(prepareDto.getSalesEntity());
        SalesEntity savedSalesEntity = saveSalesEntity(prepareDto.getSalesEntity());
        saveSalesItemEntity(prepareDto.getSalesItems(), savedSalesEntity.getId(), tenantId);
        decreaseProductInventory(prepareDto.getSalesItems(), tenantId);
        evictBrokerCache(request.getBrokerId());
        saveTransaction(savedSalesEntity);
        return SalesConverter.toResponse(savedSalesEntity, prepareDto.getSalesItems());
    }

    private SalesPrepareDto prepareSalesFlow(SalesRequest request, Long tenantId) {
        validate(request);
        List<SalesProductDto> availableProducts = getProducts();
        List<SalesProductRequest> requestedProducts = request.getProducts();
        BigDecimal discountRate = getDiscountRate(request.getBrokerId());
        List<SalesItemEntity> salesItems = validateAndProcessProducts(requestedProducts, availableProducts, tenantId);
        SalesPriceDto salesPriceDto = calculateSalesPrice(salesItems, discountRate);
        SalesEntity salesEntity = SalesConverter.toEntity(request.getBrokerId(), salesPriceDto);
        return SalesConverter.toDto(salesEntity, salesItems);
    }

    public List<SalesProductDto> getProducts() {
        return inventoryService.getAvailableInventory().stream()
                .map(inventory -> SalesProductDto.builder()
                        .productId(inventory.getProduct().getProductId())
                        .productName(inventory.getProduct().getName())
                        .productCount(inventory.getProductCount())
                        .price(inventory.getPrice())
                        .taxRate(inventory.getProduct().getTaxRate())
                        .build())
                .toList();
    }

    private BigDecimal getDiscountRate(Long brokerId) {
        BrokerDto broker = brokerService.detail(brokerId);
        if (broker.getStatus() != BrokerStatus.ACTIVE) {
            throw new BrokerNotFoundException(brokerId);
        }
        return Optional.ofNullable(broker.getDiscountRate())
                .orElse(BigDecimal.ZERO);
    }

    private List<SalesItemEntity> validateAndProcessProducts(List<SalesProductRequest> requestedProducts,
                                                             List<SalesProductDto> availableProducts,
                                                             Long tenantId) {
        Map<Long, SalesProductDto> productMap = availableProducts.stream()
                .collect(Collectors.toMap(SalesProductDto::getProductId, Function.identity()));
        List<SalesItemEntity> salesItems = new ArrayList<>();

        for (SalesProductRequest requestedProduct : requestedProducts) {
            SalesProductDto availableProduct = productMap.get(requestedProduct.getProductId());
            if (availableProduct == null) {
                throw new ProductNotFoundException(requestedProduct.getProductId());
            }
            if (requestedProduct.getProductCount() == null || requestedProduct.getProductCount() <= 0) {
                throw new InventoryCountException();
            }
            if (availableProduct.getProductCount() < requestedProduct.getProductCount()) {
                throw new InsufficientInventoryException(
                        availableProduct.getProductName(),
                        availableProduct.getProductCount(),
                        requestedProduct.getProductCount());
            }

            BigDecimal unitPrice = availableProduct.getPrice();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(requestedProduct.getProductCount()));
            BigDecimal taxRate = availableProduct.getTaxRate();
            BigDecimal taxPrice = totalPrice.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal totalPriceWithTax = totalPrice.add(taxPrice);

            SalesItemEntity salesItem = SalesItemEntity.builder()
                    .productId(requestedProduct.getProductId())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .totalPriceWithTax(totalPriceWithTax)
                    .productCount(requestedProduct.getProductCount())
                    .taxRate(taxRate)
                    .taxPrice(taxPrice)
                    .tenantId(tenantId)
                    .build();
            salesItems.add(salesItem);
        }
        return salesItems;
    }

    private SalesPriceDto calculateSalesPrice(List<SalesItemEntity> salesItems, BigDecimal discountRate) {
        BigDecimal subtotalPrice = salesItems.stream()
                .map(SalesItemEntity::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountPrice = subtotalPrice.multiply(discountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);

        BigDecimal totalPriceAfterDiscount = subtotalPrice.subtract(discountPrice);
        BigDecimal totalTaxPrice = salesItems.stream()
                .map(item -> {
                    BigDecimal itemPriceAfterDiscount = item.getTotalPrice()
                            .subtract(item.getTotalPrice().multiply(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN));
                    return itemPriceAfterDiscount.multiply(item.getTaxRate())
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SalesPriceDto.builder()
                .totalPriceWithTax(totalPriceAfterDiscount.add(totalTaxPrice))
                .subtotalPrice(subtotalPrice)
                .discountRate(discountRate)
                .discountPrice(discountPrice)
                .totalPrice(totalPriceAfterDiscount)
                .totalTaxPrice(totalTaxPrice)
                .build();
    }

    private void setDocumentNumber(SalesEntity salesEntity) {
        String documentNumber = Optional.ofNullable(salesRepository.findMaxDocumentNumberNumeric())
                .map(lastDocumentNumber -> SALES_PREFIX + (lastDocumentNumber + 1))
                .orElse(SALES_PREFIX + SALES_DEFAULT);
        salesEntity.setDocumentNumber(documentNumber);
    }

    private SalesEntity saveSalesEntity(SalesEntity salesEntity) {
        return salesRepository.save(salesEntity);
    }

    private void saveSalesItemEntity(List<SalesItemEntity> salesItems, Long salesId, Long tenantId) {
        List<SalesItemEntity> salesItemEntityList = salesItems.stream()
                .peek(salesItem -> {
                    salesItem.setSalesId(salesId);
                    salesItem.setTenantId(tenantId);
                })
                .toList();
        salesItemRepository.saveAll(salesItemEntityList);
    }

    private void decreaseProductInventory(List<SalesItemEntity> salesItems, Long tenantId) {
        Map<Long, Integer> productDecreaseProductCountMap = salesItems.stream()
                .collect(Collectors.toMap(SalesItemEntity::getProductId, SalesItemEntity::getProductCount));
        inventoryService.decreaseInventory(productDecreaseProductCountMap, tenantId);
    }

    private void evictBrokerCache(Long brokerId) {
        brokerService.evictBrokerCache(brokerId);
    }

    private void saveTransaction(SalesEntity salesEntity) {
        transactionService.createSalesTransaction(salesEntity);
    }
}
