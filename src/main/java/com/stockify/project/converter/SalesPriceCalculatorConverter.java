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
        // Brüt tutar (KDV hariç, indirim öncesi)
        BigDecimal subtotalPrice = salesItems.stream()
                .map(SalesItemDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Toplam indirim tutarı
        BigDecimal discountPrice = salesItems.stream()
                .map(SalesItemDto::getDiscountPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Net tutar (KDV hariç, indirim sonrası)
        BigDecimal totalPrice = salesItems.stream()
                .map(SalesItemDto::getPriceAfterDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // KDV tutarı (indirimli fiyat üzerinden hesaplanmış)
        BigDecimal totalTaxPrice = salesItems.stream()
                .map(SalesItemDto::getTaxPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Genel toplam (KDV dahil)
        BigDecimal totalPriceWithTax = salesItems.stream()
                .map(SalesItemDto::getTotalPriceWithTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return SalesPriceDto.builder()
                .subtotalPrice(subtotalPrice)              // Brüt tutar (KDV hariç)
                .discountRate(discountRate)                // İskonto oranı %
                .discountPrice(discountPrice)              // İskonto tutarı
                .totalPrice(totalPrice)                    // Net tutar (KDV hariç)
                .totalTaxPrice(totalTaxPrice)              // İskonto sonrası KDV tutarı
                .totalPriceWithTax(totalPriceWithTax)      // Genel toplam (KDV dahil)
                .build();
    }
}