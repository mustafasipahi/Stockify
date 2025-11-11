package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyTotalDto {

    private BigDecimal totalSalesAmount;
    private BigDecimal totalPaymentAmount;
    private BigDecimal profitOrLoss;
}
