package com.stockify.project.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.model.dto.SalesItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesResponse {

    private Long salesId;
    private String documentNumber;
    private List<SalesItemDto> salesItems;
    private BigDecimal subtotalPrice;
    private BigDecimal discountRate;
    private BigDecimal discountPrice;
    private BigDecimal totalPrice;
    private BigDecimal totalTaxPrice;
    private BigDecimal totalPriceWithTax;
    private String downloadUrl;
    private String invoiceDownloadUrl;
    private Long createdDate;
}
