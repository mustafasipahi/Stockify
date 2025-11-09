package com.project.envantra.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EmailUtil {

    public static String formatPrice(java.math.BigDecimal price) {
        return price != null ? String.format("%,.2f", price) : "0,00";
    }

    public static String formatDiscountRate(java.math.BigDecimal rate) {
        return rate != null ? String.format("%.1f", rate) : "0,0";
    }

    public static boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && email.contains("@") && email.contains(".");
    }

    public static String maskEmail(String email) {
        if (StringUtils.isBlank(email) || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "***@" + domain;
        }
        return username.substring(0, 2) + "***@" + domain;
    }
}
