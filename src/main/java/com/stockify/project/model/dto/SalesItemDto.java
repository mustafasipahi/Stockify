package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesItemDto {

    private Long id;
    private Long salesId;
    private Long productId;
    private String productName;
    private Integer productCount;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private BigDecimal taxRate;
    private BigDecimal taxPrice;
    private BigDecimal totalPriceWithTax;
    private Long createdDate;
}
