package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
