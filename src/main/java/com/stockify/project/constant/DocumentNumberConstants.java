package com.stockify.project.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentNumberConstants {

    public static final String PAYMENT_PREFIX = "PY";
    public static final Integer PAYMENT_DEFAULT = 1000;

    public static final String SALES_PREFIX = "SL";
    public static final Integer SALES_DEFAULT = 1000;
}
