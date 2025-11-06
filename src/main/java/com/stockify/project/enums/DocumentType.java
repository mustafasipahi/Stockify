package com.stockify.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DocumentType {

    INVOICE("FATURA", "fatura"),
    RECEIPT("MAKBUZ", "makbuz"),
    VOUCHER("FİŞ", "fis"),
    OTHER("DİĞER", "diger");

    private final String name;
    private final String lowerName;
}
