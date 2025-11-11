package com.project.envantra.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.model.dto.DailyBrokerReportDto;
import com.project.envantra.model.dto.DailySummaryDto;
import com.project.envantra.model.dto.DailyTotalDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DailyReportResponse {

    private List<DailyBrokerReportDto> dailyBrokerReports;
    private List<DailySummaryDto> dailySummaryReports;
    private DailyTotalDto totals;
}
