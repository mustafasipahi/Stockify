package com.stockify.project.service;

import com.stockify.project.converter.SalesConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.exception.InsufficientInventoryException;
import com.stockify.project.exception.InventoryCountException;
import com.stockify.project.exception.ProductNotFoundException;
import com.stockify.project.model.dto.*;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.request.SalesProductRequest;
import com.stockify.project.model.request.SalesRequest;
import com.stockify.project.model.response.DocumentResponse;
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
import static com.stockify.project.converter.SalesConverter.*;
import static com.stockify.project.validator.SalesValidator.validate;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final SalesItemRepository salesItemRepository;
    private final InventoryService inventoryService;
    private final BrokerService brokerService;
    private final TransactionService transactionService;
    private final DocumentService documentService;

    @Transactional
    public SalesResponse salesCalculate(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request);
        return SalesConverter.toResponse(prepareDto.getSales(), prepareDto.getSalesItems(), null);
    }

    @Transactional
    public SalesResponse salesConfirm(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request);
        SalesEntity salesEntity = toSalesEntity(prepareDto.getSales());
        String documentId = uploadDocument(salesEntity);
        SalesEntity savedSalesEntity = saveSalesEntity(salesEntity);
        saveSalesItemEntity(prepareDto.getSalesItems(), savedSalesEntity.getId());
        decreaseProductInventory(prepareDto.getSalesItems());
        evictBrokerCache(request.getBrokerId());
        saveTransaction(savedSalesEntity);
        return SalesConverter.toResponse(prepareDto.getSales(), prepareDto.getSalesItems(), documentId);
    }

    private SalesPrepareDto prepareSalesFlow(SalesRequest request) {
        validate(request);
        List<SalesProductDto> availableProducts = getProducts();
        List<SalesProductRequest> requestedProducts = request.getProducts();
        BigDecimal discountRate = getDiscountRate(request.getBrokerId());
        List<SalesItemDto> salesItems = validateAndProcessProducts(requestedProducts, availableProducts);
        SalesPriceDto salesPriceDto = calculateTaxAndDiscount(salesItems, discountRate);
        SalesDto sales = SalesConverter.toSalesDto(request.getBrokerId(), salesPriceDto);
        return SalesConverter.toPrepareDto(sales, salesItems);
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

    private List<SalesItemDto> validateAndProcessProducts(List<SalesProductRequest> requestedProducts,
                                                          List<SalesProductDto> availableProducts) {
        Map<Long, SalesProductDto> productMap = availableProducts.stream()
                .collect(Collectors.toMap(SalesProductDto::getProductId, Function.identity()));
        List<SalesItemDto> salesItems = new ArrayList<>();

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
            Long productId = availableProduct.getProductId();
            Integer productCount = requestedProduct.getProductCount();
            BigDecimal unitPrice = availableProduct.getPrice();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(productCount));
            BigDecimal taxRate = availableProduct.getTaxRate();
            BigDecimal taxPrice = totalPrice.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal totalPriceWithTax = totalPrice.add(taxPrice);
            SalesItemDto salesItem = toSalesItemDto(productId, productCount, unitPrice, totalPrice, taxRate, taxPrice, totalPriceWithTax, availableProduct.getProductName());
            salesItems.add(salesItem);
        }
        return salesItems;
    }

    private SalesPriceDto calculateTaxAndDiscount(List<SalesItemDto> salesItems, BigDecimal discountRate) {
        BigDecimal subtotalPrice = salesItems.stream()
                .map(SalesItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal originalTotalTaxPrice = salesItems.stream()
                .map(SalesItemDto::getTaxPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountPrice = subtotalPrice.multiply(discountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
        BigDecimal subtotalPriceWithDiscount = subtotalPrice.subtract(discountPrice);
        BigDecimal taxDiscountAmount = originalTotalTaxPrice.multiply(discountRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN);
        BigDecimal totalTaxPrice = originalTotalTaxPrice.subtract(taxDiscountAmount);
        BigDecimal totalPriceWithTax = subtotalPriceWithDiscount.add(totalTaxPrice);
        return SalesPriceDto.builder()
                .subtotalPrice(subtotalPrice)              // Brüt tutar (KDV hariç)
                .discountRate(discountRate)                // İskonto oranı %
                .discountPrice(discountPrice)              // İskonto tutarı
                .totalPrice(subtotalPriceWithDiscount)     // Net tutar (KDV hariç)
                .totalTaxPrice(totalTaxPrice)              // İskonto sonrası KDV tutarı
                .totalPriceWithTax(totalPriceWithTax)      // Genel toplam (KDV dahil)
                .build();
    }

    private SalesEntity saveSalesEntity(SalesEntity salesEntity) {
        return salesRepository.save(salesEntity);
    }

    private void saveSalesItemEntity(List<SalesItemDto> salesItems, Long salesId) {
        List<SalesItemDto> salesItemDtoList = salesItems.stream()
                .peek(salesItem -> salesItem.setSalesId(salesId))
                .toList();
        salesItemRepository.saveAll(toSalesItemEntity(salesItemDtoList));
    }

    private void decreaseProductInventory(List<SalesItemDto> salesItems) {
        Map<Long, Integer> productDecreaseProductCountMap = salesItems.stream()
                .collect(Collectors.toMap(SalesItemDto::getProductId, SalesItemDto::getProductCount));
        inventoryService.decreaseInventory(productDecreaseProductCountMap);
    }

    private void evictBrokerCache(Long brokerId) {
        brokerService.evictBrokerCache(brokerId);
    }

    private String uploadDocument(SalesEntity savedSalesEntity) {
        DocumentResponse documentResponse = documentService.uploadSalesFile();
        String documentId = documentResponse.getId();
        savedSalesEntity.setDocumentId(documentId);
        savedSalesEntity.setDocumentNumber(getDocumentNumber());
        return documentId;
    }

    private void saveTransaction(SalesEntity salesEntity) {
        transactionService.createSalesTransaction(salesEntity);
    }

    private String getDocumentNumber() {
        return Optional.ofNullable(salesRepository.findMaxDocumentNumberNumeric())
                .map(lastDocumentNumber -> SALES_PREFIX + (lastDocumentNumber + 1))
                .orElse(SALES_PREFIX + SALES_DEFAULT);
    }
}
