package com.stockify.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocumentType {

    INVOICE("FATURA"),
    RECEIPT("MAKBUZ"),
    VOUCHER("FİŞ"),
    OTHER("DİĞER");

    private final String name;
}
