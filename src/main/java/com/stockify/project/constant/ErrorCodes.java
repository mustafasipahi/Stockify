package com.stockify.project.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorCodes {

    public static final int UNAUTHENTICATED = 1000;
    public static final int PRODUCT_NOT_FOUND = 1001;
    public static final int PRODUCT_NAME_ALREADY_USED = 1003;
    public static final int PRODUCT_ID_REQUIRED = 1004;
    public static final int PRODUCT_NAME_REQUIRED = 1005;
    public static final int INVENTORY_PRICE_REQUIRED = 1006;
    public static final int CATEGORY_NAME_REQUIRED = 1007;
    public static final int CATEGORY_NAME_ALREADY_USED = 1008;
    public static final int CATEGORY_ID_REQUIRED = 1009;
    public static final int CATEGORY_NOT_FOUND = 1010;
    public static final int KDV_REQUIRED = 1011;
    public static final int INVENTORY_COUNT_REQUIRED = 1012;
    public static final int INVENTORY_ID_REQUIRED = 1013;
    public static final int INVENTORY_NOT_FOUND = 1014;
    public static final int INVENTORY_CRITICAL_COUNT_REQUIRED = 1015;

    public static final int UNKNOWN_ERROR = 9999;
}
