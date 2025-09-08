package com.stockify.project.validator;

import com.stockify.project.converter.SalesConverter;
import com.stockify.project.exception.InsufficientInventoryException;
import com.stockify.project.exception.InventoryCountException;
import com.stockify.project.exception.ProductNotFoundException;
import com.stockify.project.model.dto.BasketDto;
import com.stockify.project.model.dto.SalesItemDto;
import com.stockify.project.model.dto.SalesProductDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BasketValidator {

    public static List<SalesItemDto> validateAndProcessProducts(List<BasketDto> basket,
                                                                List<SalesProductDto> availableProducts,
                                                                boolean throwError) {
        Map<Long, SalesProductDto> productMap = availableProducts.stream()
                .collect(Collectors.toMap(SalesProductDto::getProductId, Function.identity()));
        List<SalesItemDto> salesItems = new ArrayList<>();
        for (BasketDto basketItem : basket) {
            SalesProductDto availableProduct = productMap.get(basketItem.getProductId());
            if (availableProduct == null) {
                if (throwError) {
                    throw new ProductNotFoundException(basketItem.getProductId());
                } else {
                    continue;
                }
            }
            if (basketItem.getProductCount() == null || basketItem.getProductCount() <= 0) {
                if (throwError) {
                    throw new InventoryCountException();
                } else {
                    continue;
                }
            }
            if (availableProduct.getProductCount() < basketItem.getProductCount()) {
                if (throwError) {
                    throw new InsufficientInventoryException(
                            availableProduct.getProductName(),
                            availableProduct.getProductCount(),
                            basketItem.getProductCount());
                } else {
                    continue;
                }
            }
            Long productId = basketItem.getProductId();
            Integer productCount = basketItem.getProductCount();
            BigDecimal unitPrice = availableProduct.getPrice();
            BigDecimal totalPrice = unitPrice.multiply(BigDecimal.valueOf(productCount));
            BigDecimal taxRate = availableProduct.getTaxRate();
            BigDecimal taxPrice = totalPrice.multiply(taxRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            BigDecimal totalPriceWithTax = totalPrice.add(taxPrice);
            SalesItemDto salesItem = SalesConverter.toSalesItemDto(productId, productCount, unitPrice, totalPrice, taxRate,
                    taxPrice, totalPriceWithTax, availableProduct.getProductName());
            salesItems.add(salesItem);
        }
        return salesItems;
    }
}
