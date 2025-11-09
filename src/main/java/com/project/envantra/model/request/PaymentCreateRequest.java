package com.project.envantra.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentCreateRequest {

    private Long brokerId;
    private BigDecimal paymentPrice;
    private PaymentType paymentType;
}
