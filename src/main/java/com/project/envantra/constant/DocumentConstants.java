package com.project.envantra.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentConstants {

    public static final DateTimeFormatter DATE_TIME_FORMATTER_1 = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_2 = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_3 = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    public static final DecimalFormat QTY_FORMAT = new DecimalFormat("#,##0.###");

    public static final String PAYMENT_PREFIX = "PY";
    public static final String SALES_PREFIX = "SL";
    public static final String UNKNOWN_PREFIX = "UKN";
    public static final String PROFILE_IMAGE_PREFIX = "PI";
    public static final String COMPANY_LOGO_PREFIX = "CL";

    public static final String PATH_DELIMITER = "/";
    public static final String DEFAULT_CONTENT_TYPE = "application/pdf";
    public static final String DEFAULT_CURRENCY = "TL";
    public static final String DEFAULT_CURRENCY_FRACTION = "Kr";

    public static final String DEFAULT_PAYMENT_FILENAME = "payment.pdf";
    public static final String DEFAULT_SALES_FILENAME = "sales.pdf";

    public static final String DEFAULT_BRAND_NAME = "Envantra";
    public static final String DEFAULT_BRAND_URL = "www.envantra.com";
}
