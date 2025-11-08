package com.stockify.project.validator;

import com.stockify.project.exception.BrokerDiscountRateException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerUpdateValidator {

    public static void validateDiscountRate(BigDecimal discountRate) {
        if (discountRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new BrokerDiscountRateException();
        }
    }
}
