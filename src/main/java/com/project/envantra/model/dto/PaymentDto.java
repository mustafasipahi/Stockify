package com.project.envantra.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.enums.PaymentType;
import com.project.envantra.model.entity.UserEntity;
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
public class PaymentDto {

    private UserEntity user;
    private Long documentId;
    private String documentNumber;
    private BrokerDto broker;
    private CompanyDto company;
    private BigDecimal price;
    private PaymentType type;
    private LocalDateTime createdDate;
}
