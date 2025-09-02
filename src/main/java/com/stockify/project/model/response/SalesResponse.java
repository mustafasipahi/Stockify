package com.stockify.project.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.model.dto.SalesItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SalesResponse {

    private Long salesId;
    private List<SalesItemDto> salesItems;
    private BigDecimal totalPriceWithTax;
    private BigDecimal subtotalPrice;
    private BigDecimal totalPrice;
    private BigDecimal discountPrice;
    private BigDecimal discountRate;
    private String documentNumber;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime createdDate;
}
