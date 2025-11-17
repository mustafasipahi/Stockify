package com.project.envantra.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.enums.PaymentStatus;
import com.project.envantra.enums.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentResponse {

    private String firstName;
    private String lastName;
    private BigDecimal paymentPrice;
    private PaymentType paymentType;
    private PaymentStatus status;
    private String downloadUrl;
}
