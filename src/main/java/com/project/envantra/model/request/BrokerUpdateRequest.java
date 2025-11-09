package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrokerUpdateRequest {

    private Long brokerId;
    private BigDecimal discountRate;
    private DayOfWeek targetDayOfWeek;
}
