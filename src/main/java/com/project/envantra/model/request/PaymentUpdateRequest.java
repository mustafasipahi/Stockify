package com.project.envantra.model.request;

import com.project.envantra.enums.PaymentType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentUpdateRequest {

    private Long paymentId;
    private BigDecimal paymentPrice;
    private PaymentType paymentType;
}
