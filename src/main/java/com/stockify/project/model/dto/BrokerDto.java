package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.BrokerStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrokerDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long brokerId;
    private String firstName;
    private String lastName;
    private BigDecimal currentBalance;
    private BigDecimal discountRate;
    private BrokerStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
