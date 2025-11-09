package com.project.envantra.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FinanceUtil {

    public static final int DEFAULT_SCALE = 6;

    public static BigDecimal divide(BigDecimal first, BigDecimal second) {
        if (first == null || second == null || BigDecimal.ZERO.compareTo(second) == 0) {
            return BigDecimal.ZERO;
        }
        return first.divide(second, DEFAULT_SCALE, RoundingMode.HALF_DOWN);
    }

    public static BigDecimal multiply(BigDecimal first, BigDecimal second) {
        if (first == null || second == null) {
            return BigDecimal.ZERO;
        }
        return first.multiply(second).setScale(DEFAULT_SCALE, RoundingMode.HALF_DOWN);
    }

    public static BigDecimal multiply(BigDecimal first, Integer second) {
        if (first == null || second == null) {
            return BigDecimal.ZERO;
        }
        return first.multiply(BigDecimal.valueOf(second)).setScale(DEFAULT_SCALE, RoundingMode.HALF_DOWN);
    }

    public static boolean isValidAmount(BigDecimal amount) {
        return Objects.nonNull(amount) && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
