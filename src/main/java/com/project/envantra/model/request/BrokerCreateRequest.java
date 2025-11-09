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
public class BrokerCreateRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String tkn;
    private String vkn;
    private BigDecimal discountRate;
    private DayOfWeek targetDayOfWeek;
}
