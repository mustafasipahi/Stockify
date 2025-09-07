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
    public static final int TAX_RATE_REQUIRED = 1011;
    public static final int INVENTORY_COUNT_REQUIRED = 1012;
    public static final int INVENTORY_ID_REQUIRED = 1013;
    public static final int INVENTORY_NOT_FOUND = 1014;
    public static final int INVENTORY_CRITICAL_COUNT_REQUIRED = 1015;
    public static final int BROKER_NAME_REQUIRED = 1016;
    public static final int BROKER_DISCOUNT_REQUIRED = 1017;
    public static final int BROKER_ID_REQUIRED = 1018;
    public static final int BROKER_NOT_FOUND = 1019;
    public static final int MULTIPLE_PRODUCT_ID = 1020;
    public static final int PAYMENT_PRICE_REQUIRED = 1021;
    public static final int PAYMENT_TYPE_REQUIRED = 1022;
    public static final int COMPANY_INFO_NOT_FOUND = 1023;
    public static final int DOCUMENT_REQUIRED = 1024;
    public static final int DOCUMENT_TYPE_REQUIRED = 1025;
    public static final int INVALID_CONTENT_TYPE = 1026;
    public static final int INVALID_DOCUMENT_SIZE = 1027;
    public static final int DOCUMENT_UPLOAD_ERROR = 1028;
    public static final int DOCUMENT_DOWNLOAD_ERROR = 1029;
    public static final int DOCUMENT_NOT_FOUND = 1030;
    public static final int BASKET_EMPTY = 1031;

    public static final int UNKNOWN_ERROR = 9999;
}
