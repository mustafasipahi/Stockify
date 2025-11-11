package com.project.envantra.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TemplateConstant {

    public static final String PAYMENT_PDF_DOCUMENT_TEMPLATE = "/templates/payment_document.html";
    public static final String PAYMENT_EMAIL_RECEIVER_TEMPLATE = "templates/payment_receiver_email.html";
    public static final String PAYMENT_EMAIL_PAYER_TEMPLATE = "templates/payment_payer_email.html";

    public static final String SALES_PDF_DOCUMENT_TEMPLATE = "/templates/sales_document.html";
    public static final String SALES_EMAIL_SELLER_TEMPLATE = "templates/sales_seller_email.html";
    public static final String SALES_EMAIL_BUYER_TEMPLATE = "templates/sales_buyer_email.html";

    public static final String USER_CREATION_TEMPLATE = "templates/user_creation_email.html";
}
