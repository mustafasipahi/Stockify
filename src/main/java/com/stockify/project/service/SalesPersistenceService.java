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
import com.stockify.project.model.entity.InvoiceEntity;
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

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.validator.SalesValidator.validate;

@Service
@RequiredArgsConstructor
public class SalesPersistenceService {

    private final SalesRepository salesRepository;
    private final SalesItemRepository salesItemRepository;
    private final InventoryService inventoryService;
    private final BrokerService brokerService;

    @Transactional
    public SalesResponse salesPreview(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request, null);
        return SalesConverter.toResponse(prepareDto.getSalesEntity(), prepareDto.getSalesItems(), null);
    }

    @Transactional
    public SalesResponse salesConfirm(SalesRequest request) {
        Long tenantId = getTenantId();
        SalesPrepareDto prepareDto = prepareSalesFlow(request, tenantId);
        SalesEntity savedSalesEntity = salesRepository.save(prepareDto.getSalesEntity());
        saveSalesItemEntity(prepareDto.getSalesItems(), savedSalesEntity.getId(), tenantId);
        decreaseProductInventory(prepareDto.getSalesItems(), tenantId);
        updateBrokerDebt(request.getBrokerId(), savedSalesEntity.getTotalPrice(), tenantId);
        InvoiceEntity invoiceEntity = createInvoice(request.isCreateInvoice(), savedSalesEntity, prepareDto.getSalesItems());
        return SalesConverter.toResponse(savedSalesEntity, prepareDto.getSalesItems(), invoiceEntity);
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
            SalesItemEntity salesItem = SalesItemEntity.builder()
                    .productId(requestedProduct.getProductId())
                    .unitPrice(availableProduct.getPrice())
                    .totalPrice(availableProduct.getPrice().multiply(BigDecimal.valueOf(requestedProduct.getProductCount())))
                    .productCount(requestedProduct.getProductCount())
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
        BigDecimal discountPrice = subtotalPrice.multiply(discountRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
        return SalesPriceDto.builder()
                .subtotalPrice(subtotalPrice)
                .discountRate(discountRate)
                .discountPrice(discountPrice)
                .totalPrice(subtotalPrice.subtract(discountPrice))
                .build();
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

    private void updateBrokerDebt(Long brokerId, BigDecimal totalPrice, Long tenantId) {
        brokerService.increaseDebtPrice(brokerId, totalPrice, tenantId);
    }

    private InvoiceEntity createInvoice(boolean createInvoice, SalesEntity salesEntity, List<SalesItemEntity> salesItems) {
        InvoiceEntity invoiceEntity = null;
        if (createInvoice) {
            //invoiceEntity = invoiceService.createInvoice(salesEntity);
        }
        return invoiceEntity;
    }
}
