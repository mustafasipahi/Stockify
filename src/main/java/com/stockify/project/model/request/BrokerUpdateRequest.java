package com.stockify.project.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrokerUpdateRequest {

    private Long brokerId;
    private String firstName;
    private String lastName;
    private String email;
    private BigDecimal discountRate;
}
