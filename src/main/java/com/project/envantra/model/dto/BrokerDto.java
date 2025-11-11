package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.enums.BrokerStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.DayOfWeek;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrokerDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long creatorUserId;
    private Long brokerId;
    private Long brokerUserId;
    private Integer orderNo;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal currentBalance;
    private BigDecimal discountRate;
    private BrokerStatus status;
    private DayOfWeek targetDayOfWeek;
    private BrokerVisitDto visitInfo;
    private Long createdDate;
    private Long lastModifiedDate;
}
