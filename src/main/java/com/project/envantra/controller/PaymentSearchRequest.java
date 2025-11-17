package com.project.envantra.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentSearchRequest {

    private Long brokerId;
    private Long startDate;
    private Long endDate;
}
