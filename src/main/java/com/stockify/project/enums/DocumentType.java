package com.stockify.project.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public enum DocumentType {

    INVOICE("FATURA", "fatura"),
    RECEIPT("MAKBUZ", "makbuz"),
    VOUCHER("FİŞ", "fis"),
    PROFILE_IMAGES("Profil", "profil"),
    COMPANY_LOGO("LOGO", "logo"),
    OTHER("DİĞER", "diger");

    private final String name;
    private final String lowerName;

    public static boolean isPdf(DocumentType documentType) {
        return List.of(INVOICE, RECEIPT, VOUCHER, OTHER).contains(documentType);
    }

    public static boolean isImage(DocumentType documentType) {
        return List.of(PROFILE_IMAGES, COMPANY_LOGO).contains(documentType);
    }
}
