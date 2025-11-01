package com.stockify.project.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentNumberConstants {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");

    public static final String PAYMENT_PREFIX = "PY";
    public static final String SALES_PREFIX = "SL";
    public static final String UNKNOWN_PREFIX = "UKN";
}
