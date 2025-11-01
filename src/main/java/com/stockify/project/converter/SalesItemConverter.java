package com.stockify.project.converter;

import com.stockify.project.exception.InsufficientInventoryException;
import com.stockify.project.exception.InventoryCountException;
import com.stockify.project.exception.ProductNotFoundException;
import com.stockify.project.model.dto.BasketDto;
import com.stockify.project.model.dto.SalesItemDto;
import com.stockify.project.model.dto.SalesProductDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.stockify.project.util.FinanceUtil.divide;
import static com.stockify.project.util.FinanceUtil.multiply;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesItemConverter {

    public static List<SalesItemDto> validateAndPrepareProducts(List<BasketDto> basket,
                                                                List<SalesProductDto> availableProducts,
                                                                boolean throwError,
                                                                BigDecimal discountRate) {
        Map<Long, SalesProductDto> productMap = availableProducts.stream()
                .collect(Collectors.toMap(SalesProductDto::getProductId, Function.identity()));
        List<SalesItemDto> salesItems = new ArrayList<>();

        // Eğer discountRate null ise 0 yap
        BigDecimal effectiveDiscountRate = (discountRate != null) ? discountRate : BigDecimal.ZERO;

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
            BigDecimal itemDiscountAmount = divide(multiply(totalPrice, effectiveDiscountRate), BigDecimal.valueOf(100));
            BigDecimal priceAfterDiscount = totalPrice.subtract(itemDiscountAmount);
            BigDecimal taxRate = availableProduct.getTaxRate();
            BigDecimal taxPrice = divide(multiply(priceAfterDiscount, taxRate), BigDecimal.valueOf(100));
            BigDecimal totalPriceWithTax = priceAfterDiscount.add(taxPrice);
            SalesItemDto salesItem = SalesConverter.toSalesItemDto(
                    productId,
                    productCount,
                    unitPrice,
                    totalPrice,
                    taxRate,
                    taxPrice,
                    totalPriceWithTax,
                    availableProduct.getProductName(),
                    effectiveDiscountRate,
                    itemDiscountAmount,
                    priceAfterDiscount
            );
            salesItems.add(salesItem);
        }
        return salesItems;
    }
}
