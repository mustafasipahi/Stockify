package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.BrokerStatus;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrokerDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long brokerId;
    private Long brokerUserId;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String vkn;
    private BigDecimal currentBalance;
    private BigDecimal discountRate;
    private BrokerStatus status;
    private Long targetDay;
    private Long createdDate;
    private Long lastModifiedDate;
}
