package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyBrokerReportDto {

    private Integer orderNo;
    private String brokerFullName;
    private List<DailyBrokerReportDetailDto> dailyDetails;
    private BigDecimal totalSalesAmount;
    private BigDecimal totalPaymentAmount;
    private BigDecimal profitOrLoss;
}
