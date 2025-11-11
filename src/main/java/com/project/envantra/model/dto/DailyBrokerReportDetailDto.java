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
public class DailyBrokerReportDetailDto {

    private Long date;
    private BigDecimal salesAmount;
    private BigDecimal paymentAmount;
    private BigDecimal profitOrLoss;
    private BrokerVisitDto visitInfo;
}
