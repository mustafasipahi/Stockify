package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesDto {

    private Long id;
    private Long brokerId;
    private Long documentId;
    private String documentNumber;
    private BigDecimal subtotalPrice;
    private BigDecimal discountRate;
    private BigDecimal discountPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalTaxPrice;
    private BigDecimal totalPriceWithTax;
    private LocalDateTime createdDate;
}
