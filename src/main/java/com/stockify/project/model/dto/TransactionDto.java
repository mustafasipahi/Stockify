package com.stockify.project.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.enums.PaymentType;
import com.stockify.project.enums.TransactionType;
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
public class TransactionDto {

    private String firstName;
    private String lastName;
    private BigDecimal price;
    private BigDecimal balance;
    private TransactionType type;
    private PaymentType paymentType;
    private boolean requestedInvoice;
    private String downloadDocumentUrl;
    private String downloadInvoiceUrl;
    private Long createdDate;
}
