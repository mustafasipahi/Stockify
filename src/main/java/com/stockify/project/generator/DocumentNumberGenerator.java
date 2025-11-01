package com.stockify.project.generator;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

import static com.stockify.project.constant.DocumentNumberConstants.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentNumberGenerator {

    private static final AtomicInteger counter = new AtomicInteger(100);

    public static String getSalesDocumentNumber() {
        LocalDateTime now = LocalDateTime.now();
        String period = now.format(DATE_TIME_FORMATTER);
        String prefix = SALES_PREFIX + "-" + period + "-";
        int nextSequence = counter.incrementAndGet();
        return prefix + nextSequence;
    }

    public static String getPaymentDocumentNumber() {
        LocalDateTime now = LocalDateTime.now();
        String period = now.format(DATE_TIME_FORMATTER);
        String prefix = PAYMENT_PREFIX + "-" + period + "-";
        int nextSequence = counter.incrementAndGet();
        return prefix + nextSequence;
    }

    public static String getUnknownDocumentNumber() {
        LocalDateTime now = LocalDateTime.now();
        String period = now.format(DATE_TIME_FORMATTER);
        String prefix = UNKNOWN_PREFIX + "-" + period + "-";
        int nextSequence = counter.incrementAndGet();
        return prefix + nextSequence;
    }
}