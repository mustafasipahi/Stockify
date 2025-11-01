package com.stockify.project.service;

import com.stockify.project.converter.SalesConverter;
import com.stockify.project.model.dto.*;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.request.SalesRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.model.response.InvoiceCreateResponse;
import com.stockify.project.model.response.SalesResponse;
import com.stockify.project.repository.SalesItemRepository;
import com.stockify.project.repository.SalesRepository;
import com.stockify.project.service.document.DocumentPostService;
import com.stockify.project.service.email.SalesEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.stockify.project.converter.SalesItemConverter.validateAndPrepareProducts;
import static com.stockify.project.converter.SalesPriceCalculatorConverter.calculateTaxAndDiscount;
import static com.stockify.project.validator.SalesValidator.validate;
import static com.stockify.project.validator.SalesValidator.validateBasket;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final SalesItemRepository salesItemRepository;
    private final InventoryPostService inventoryPostService;
    private final InventoryGetService inventoryGetService;
    private final BrokerGetService brokerGetService;
    private final TransactionPostService transactionPostService;
    private final DocumentPostService documentPostService;
    private final BasketGetService basketGetService;
    private final BasketPostService basketPostService;
    private final SalesEmailService salesEmailService;
    private final CompanyGetService companyGetService;
    private final InvoiceCreateService invoiceCreateService;

    @Transactional
    public SalesResponse salesCalculate(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request);
        return SalesConverter.toResponse(
                prepareDto.getSales(),
                prepareDto.getSalesItems(),
                null,
                null);
    }

    @Transactional
    public SalesResponse salesConfirm(SalesRequest request) {
        SalesPrepareDto prepareDto = prepareSalesFlow(request);
        addCompanyInfo(prepareDto);
        SalesEntity salesEntity = SalesConverter.toSalesEntity(prepareDto.getSales());
        DocumentResponse documentResponse = uploadDocument(prepareDto);
        DocumentResponse invoiceResponse = uploadInvoice(prepareDto, request.isCreateInvoice());
        salesEntity.setDocumentId(documentResponse.getDocumentId());
        salesEntity.setInvoiceId(invoiceResponse.getDocumentId());
        SalesEntity savedSalesEntity = saveSalesEntity(salesEntity);
        saveSalesItemEntity(prepareDto.getSalesItems(), savedSalesEntity.getId());
        decreaseProductInventory(prepareDto);
        clearBasket(prepareDto.getBroker().getBrokerId());
        saveTransaction(savedSalesEntity, request.isCreateInvoice());
        sendEmail(prepareDto, documentResponse);
        return SalesConverter.toResponse(
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
        validate(brokerId);
        List<SalesProductDto> availableProducts = getSalesInventory();
        List<BasketDto> basket = getBrokerBasket(brokerId);
        validateBasket(basket);
        BrokerDto broker = getBroker(brokerId);
        BigDecimal discountRate = getDiscountRate(broker.getDiscountRate());
        return validateAndPrepareProducts(basket, availableProducts, false, discountRate);
    }

    private SalesPrepareDto prepareSalesFlow(SalesRequest request) {
        validate(request);
        List<SalesProductDto> availableProducts = getSalesInventory();
        List<BasketDto> basket = getBrokerBasket(request.getBrokerId());
        validateBasket(basket);
        BrokerDto broker = getBroker(request.getBrokerId());
        BigDecimal discountRate = getDiscountRate(broker.getDiscountRate());
        List<SalesItemDto> salesItems = validateAndPrepareProducts(basket, availableProducts, false, discountRate);
        SalesPriceDto salesPriceDto = calculateTaxAndDiscount(salesItems, discountRate);
        SalesDto sales = SalesConverter.toSalesDto(request.getBrokerId(), salesPriceDto);
        return SalesConverter.toPrepareDto(sales, salesItems, broker);
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

    private SalesEntity saveSalesEntity(SalesEntity salesEntity) {
        return salesRepository.save(salesEntity);
    }

    private void saveSalesItemEntity(List<SalesItemDto> salesItems, Long salesId) {
        List<SalesItemDto> salesItemDtoList = salesItems.stream()
                .peek(salesItem -> salesItem.setSalesId(salesId))
                .toList();
        salesItemRepository.saveAll(SalesConverter.toSalesItemEntity(salesItemDtoList));
    }

    private void decreaseProductInventory(SalesPrepareDto prepareDto) {
        inventoryPostService.decreaseInventory(prepareDto);
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
            InvoiceCreateResponse invoice = invoiceCreateService.createInvoice(prepareDto);
            return documentPostService.uploadInvoiceFile(prepareDto, invoice);
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