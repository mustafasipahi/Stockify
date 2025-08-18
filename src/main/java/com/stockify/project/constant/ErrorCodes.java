package com.stockify.project.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCodes {

    public static final int UNAUTHENTICATED = 1000;
    public static final int PRODUCT_NOT_FOUND = 1001;
    public static final int SEARCH_TEXT_REQUIRED = 1002;
    public static final int PRODUCT_NAME_ALREADY_USED = 1003;
    public static final int PRODUCT_ID_REQUIRED = 1004;
    public static final int PRODUCT_NAME_REQUIRED = 1005;
    public static final int PRODUCT_PRICE_REQUIRED = 1006;
    public static final int CATEGORY_NAME_REQUIRED = 1007;
    public static final int CATEGORY_NAME_ALREADY_USED = 1008;
    public static final int CATEGORY_ID_REQUIRED = 1009;
    public static final int CATEGORY_NOT_FOUND = 1010;

    public static final int UNKNOWN_ERROR = 9999;
}
