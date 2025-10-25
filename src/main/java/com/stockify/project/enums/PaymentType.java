package com.stockify.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentType {

    CASH("Nakit"),
    CREDIT_CARD("Kredi Kartı"),
    BANK_TRANSFER("Banka Transferi"),
    CHECK("Çek");

    private final String name;
}
