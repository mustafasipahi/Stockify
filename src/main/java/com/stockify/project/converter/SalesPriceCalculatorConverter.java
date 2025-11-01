package com.stockify.project.converter;

import com.stockify.project.model.dto.SalesItemDto;
import com.stockify.project.model.dto.SalesPriceDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesPriceCalculatorConverter {

    public static SalesPriceDto calculateTaxAndDiscount(List<SalesItemDto> salesItems, BigDecimal discountRate) {
        BigDecimal subtotalPrice = salesItems.stream()
                .map(SalesItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal discountPrice = salesItems.stream()
                .map(SalesItemDto::getDiscountPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal subtotalPriceWithDiscount = salesItems.stream()
                .map(SalesItemDto::getPriceAfterDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalTaxPrice = salesItems.stream()
                .map(SalesItemDto::getTaxPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPriceWithTax = salesItems.stream()
                .map(SalesItemDto::getTotalPriceWithTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return SalesPriceDto.builder()
                .subtotalPrice(subtotalPrice)
                .discountRate(discountRate)
                .discountPrice(discountPrice)
                .totalPrice(subtotalPriceWithDiscount)
                .totalTaxPrice(totalTaxPrice)
                .totalPriceWithTax(totalPriceWithTax)
                .build();
    }
}
