package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BrokerDto {

    private Long brokerId;
    private String firstName;
    private String lastName;
    private BigDecimal discount;
    private BigDecimal debtPrice;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
}
