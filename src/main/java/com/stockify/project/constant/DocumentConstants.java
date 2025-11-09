package com.stockify.project.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentConstants {

    public static final DateTimeFormatter DATE_TIME_FORMATTER_1 = DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss");
    public static final DateTimeFormatter DATE_TIME_FORMATTER_2 = DateTimeFormatter.ofPattern("ddMMyyyy-HHmmss");

    public static final String PAYMENT_PREFIX = "PY";
    public static final String SALES_PREFIX = "SL";
    public static final String UNKNOWN_PREFIX = "UKN";
    public static final String PROFILE_IMAGE_PREFIX = "PI";
    public static final String COMPANY_LOGO_PREFIX = "CL";

    public static final String PATH_DELIMITER = "/";
}
