package com.stockify.project.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FinanceUtil {

    public static BigDecimal multiply(BigDecimal first, Integer second) {
        if (first == null || second == null) {
            return BigDecimal.ZERO;
        }
        return first.multiply(BigDecimal.valueOf(second));
    }
}
