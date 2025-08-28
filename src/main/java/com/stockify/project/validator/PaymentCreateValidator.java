package com.stockify.project.validator;

import com.stockify.project.exception.BrokerIdException;
import com.stockify.project.exception.PaymentPriceException;
import com.stockify.project.exception.PaymentTypeException;
import com.stockify.project.model.request.PaymentCreateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCreateValidator {

    public static void validate(PaymentCreateRequest request) {
        if (request.getBrokerId() == null) {
            throw new BrokerIdException();
        }
        if (request.getPaymentPrice() == null) {
            throw new PaymentPriceException();
        }
        if (request.getPaymentPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new PaymentPriceException();
        }
        if (request.getPaymentType() == null) {
            throw new PaymentTypeException();
        }
    }
}
