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

import static com.stockify.project.validator.SalesValidator.validate;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final SalesItemRepository salesItemRepository;
    private final SalesConverter salesConverter;
    private final InventoryPostService inventoryPostService;
    private final InventoryGetService inventoryGetService;
    private final BrokerGetService brokerGetService;
    private final TransactionPostService transactionPostService;
    private final DocumentService documentService;

    @Transactional
    public SalesResponse salesCalculate(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request);
        return salesConverter.toResponse(prepareDto.getSales(), prepareDto.getSalesItems(), null);
    }

    @Transactional
    public SalesResponse salesConfirm(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request);
        SalesEntity salesEntity = salesConverter.toSalesEntity(prepareDto.getSales());
        String documentId = uploadDocument(prepareDto, salesEntity);
        SalesEntity savedSalesEntity = saveSalesEntity(salesEntity);
        saveSalesItemEntity(prepareDto.getSalesItems(), savedSalesEntity.getId());
        decreaseProductInventory(prepareDto.getSalesItems());
        saveTransaction(savedSalesEntity);
        return salesConverter.toResponse(prepareDto.getSales(), prepareDto.getSalesItems(), documentId);
    }

    private SalesPrepareDto prepareSalesFlow(SalesRequest request) {
        validate(request);
        List<SalesProductDto> availableProducts = getProducts();
        List<SalesProductRequest> requestedProducts = request.getProducts();
        BrokerDto broker = getBroker(request.getBrokerId());
        BigDecimal discountRate = getDiscountRate(broker);
        List<SalesItemDto> salesItems = validateAndProcessProducts(requestedProducts, availableProducts);
        SalesPriceDto salesPriceDto = calculateTaxAndDiscount(salesItems, discountRate);
        SalesDto sales = salesConverter.toSalesDto(request.getBrokerId(), salesPriceDto);
        return salesConverter.toPrepareDto(sales, salesItems, broker);
    }

    public List<SalesProductDto> getProducts() {
        return inventoryGetService.getAvailableInventory().stream()
                .map(inventory -> SalesProductDto.builder()
                        .productId(inventory.getProduct().getProductId())
                        .productName(inventory.getProduct().getName())
                        .productCount(inventory.getProductCount())
                        .price(inventory.getPrice())
                        .taxRate(inventory.getProduct().getTaxRate())
                        .build())
                .toList();
    }

    private BrokerDto getBroker(Long brokerId) {
        return brokerGetService.detail(brokerId);
    }

    private BigDecimal getDiscountRate(BrokerDto broker) {
        if (broker.getStatus() != BrokerStatus.ACTIVE) {
            throw new BrokerNotFoundException(broker.getBrokerId());
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
            SalesItemDto salesItem = salesConverter.toSalesItemDto(productId, productCount, unitPrice, totalPrice, taxRate,
                    taxPrice, totalPriceWithTax, availableProduct.getProductName());
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
        salesItemRepository.saveAll(salesConverter.toSalesItemEntity(salesItemDtoList));
    }

    private void decreaseProductInventory(List<SalesItemDto> salesItems) {
        Map<Long, Integer> productDecreaseProductCountMap = salesItems.stream()
                .collect(Collectors.toMap(SalesItemDto::getProductId, SalesItemDto::getProductCount));
        inventoryPostService.decreaseInventory(productDecreaseProductCountMap);
    }

    private String uploadDocument(SalesPrepareDto prepareDto, SalesEntity salesEntity) {
        DocumentResponse documentResponse = documentService.uploadSalesFile(prepareDto);
        String documentId = documentResponse.getId();
        salesEntity.setDocumentId(documentId);
        return documentId;
    }

    private void saveTransaction(SalesEntity salesEntity) {
        transactionPostService.createSalesTransaction(salesEntity);
    }
}
