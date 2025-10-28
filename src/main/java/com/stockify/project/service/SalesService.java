package com.stockify.project.service;

import com.stockify.project.converter.SalesConverter;
import com.stockify.project.model.dto.*;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.request.SalesRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.model.response.SalesResponse;
import com.stockify.project.repository.SalesItemRepository;
import com.stockify.project.repository.SalesRepository;
import com.stockify.project.service.document.DocumentPostService;
import com.stockify.project.service.email.SalesEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

import static com.stockify.project.validator.BasketValidator.validateAndProcessProducts;
import static com.stockify.project.validator.SalesValidator.validate;
import static com.stockify.project.validator.SalesValidator.validateBasket;

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
    private final DocumentPostService documentPostService;
    private final BasketGetService basketGetService;
    private final BasketPostService basketPostService;
    private final SalesEmailService salesEmailService;
    private final CompanyGetService companyGetService;

    @Transactional
    public SalesResponse salesCalculate(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request);
        return salesConverter.toResponse(
                prepareDto.getSales(),
                prepareDto.getSalesItems(),
                null,
                null);
    }

    @Transactional
    public SalesResponse salesConfirm(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request);
        addCompanyInfo(prepareDto);
        SalesEntity salesEntity = salesConverter.toSalesEntity(prepareDto.getSales());
        DocumentResponse documentResponse = uploadDocument(prepareDto);
        DocumentResponse invoiceResponse = uploadInvoice(prepareDto, request.isCreateInvoice());
        salesEntity.setDocumentId(documentResponse.getDocumentId());
        salesEntity.setInvoiceId(invoiceResponse.getDocumentId());
        SalesEntity savedSalesEntity = saveSalesEntity(salesEntity);
        saveSalesItemEntity(prepareDto.getSalesItems(), savedSalesEntity.getId());
        decreaseAndCreateProductInventory(prepareDto);
        clearBasket(prepareDto.getBroker().getBrokerId());
        saveTransaction(savedSalesEntity, request.isCreateInvoice());
        sendEmail(prepareDto, documentResponse);
        return salesConverter.toResponse(
                prepareDto.getSales(),
                prepareDto.getSalesItems(),
                documentResponse.getDownloadUrl(),
                invoiceResponse.getDownloadUrl());
    }

    @Transactional
    public void salesCancel(SalesRequest request) {
        clearBasket(request.getBrokerId());
    }

    public List<SalesProductDto> getSalesInventory() {
        return inventoryGetService.getSalesInventory();
    }

    public List<SalesItemDto> getBrokerBasketDetail(Long brokerId) {
        List<BasketDto> basket = getBrokerBasket(brokerId);
        List<SalesProductDto> products = getSalesInventory();
        return validateAndProcessProducts(basket, products, false);
    }

    private SalesPrepareDto prepareSalesFlow(SalesRequest request) {
        validate(request);
        List<SalesProductDto> availableProducts = getSalesInventory();
        List<BasketDto> basket = getBrokerBasket(request.getBrokerId());
        validateBasket(basket);
        BrokerDto broker = getBroker(request.getBrokerId());
        BigDecimal discountRate = getDiscountRate(broker.getDiscountRate());
        List<SalesItemDto> salesItems = validateAndProcessProducts(basket, availableProducts, false);
        SalesPriceDto salesPriceDto = calculateTaxAndDiscount(salesItems, discountRate);
        SalesDto sales = salesConverter.toSalesDto(request.getBrokerId(), salesPriceDto);
        return salesConverter.toPrepareDto(sales, salesItems, broker);
    }

    private List<BasketDto> getBrokerBasket(Long brokerId) {
        return basketGetService.getBrokerAllBasket(brokerId);
    }

    private BrokerDto getBroker(Long brokerId) {
        return brokerGetService.getActiveBroker(brokerId);
    }

    private BigDecimal getDiscountRate(BigDecimal discountRate) {
        return Optional.ofNullable(discountRate)
                .orElse(BigDecimal.ZERO);
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

    private void decreaseAndCreateProductInventory(SalesPrepareDto prepareDto) {
        inventoryPostService.decreaseAndCreateInventory(prepareDto);
    }

    private void addCompanyInfo(SalesPrepareDto prepareDto) {
        CompanyInfoDto companyInfo = companyGetService.getCompanyInfo();
        prepareDto.setCompanyInfo(companyInfo);
    }

    private DocumentResponse uploadDocument(SalesPrepareDto prepareDto) {
        return documentPostService.uploadSalesFile(prepareDto);
    }

    private DocumentResponse uploadInvoice(SalesPrepareDto prepareDto, boolean createInvoice) {
        if (createInvoice) {

        }
        return new DocumentResponse();
    }

    private void clearBasket(Long brokerId) {
        basketPostService.clearBasket(brokerId);
    }

    private void saveTransaction(SalesEntity salesEntity, boolean createInvoice) {
        transactionPostService.createSalesTransaction(salesEntity, createInvoice);
    }

    private void sendEmail(SalesPrepareDto salesPrepareDto, DocumentResponse documentResponse) {
        salesEmailService.sendSalesNotifications(salesPrepareDto, documentResponse);
    }
}
