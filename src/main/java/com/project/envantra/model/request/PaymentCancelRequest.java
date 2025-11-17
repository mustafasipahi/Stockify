package com.project.envantra.model.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentCancelRequest {

    private Long paymentId;
    private String cancelReason;
}
