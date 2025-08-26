package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesPriceDto {

    private BigDecimal subtotalPrice;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private BigDecimal discountRate;
}
