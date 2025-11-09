package com.stockify.project.util;

import com.stockify.project.model.entity.DocumentEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentUtil {

    public static String getDownloadUrl(DocumentEntity document) {
        if (document == null) {
            return null;
        }
        if (document.getId() == null) {
            return null;
        }
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/documents/download/")
                .path(String.valueOf(document.getId()))
                .toUriString();
    }

    public static String replaceCharacter(String originalFileName) {
        if (StringUtils.isBlank(originalFileName)) {
            return StringUtils.EMPTY;
        }
        return originalFileName
                .replace("ü", "u").replace("Ü", "U")
                .replace("ö", "o").replace("Ö", "O")
                .replace("ç", "c").replace("Ç", "C")
                .replace("ğ", "g").replace("Ğ", "G")
                .replace("ş", "s").replace("Ş", "S")
                .replace("ı", "i").replace("İ", "I");
    }

    public static String replaceCharacterDetail(String originalFileName) {
        if (StringUtils.isBlank(originalFileName)) {
            return "unnamed_file";
        }
        return originalFileName
                .replace("ü", "u").replace("Ü", "U")
                .replace("ö", "o").replace("Ö", "O")
                .replace("ç", "c").replace("Ç", "C")
                .replace("ğ", "g").replace("Ğ", "G")
                .replace("ş", "s").replace("Ş", "S")
                .replace("ı", "i").replace("İ", "I")
                .replaceAll("[^a-zA-Z0-9._-]", "_")
                .replaceAll("_{2,}", "_")
                .replaceAll("(^_+)|(_+$)", "");
    }

    public static String convertNumberToWords(int number) {
        if (number == 0) return "Sifir";
        if (number < 0) return "Eksi " + convertNumberToWords(-number);

        String[] ones = {"", "Bir", "Iki", "Uc", "Dort", "Bes", "Alti", "Yedi", "Sekiz", "Dokuz"};
        String[] tens = {"", "On", "Yirmi", "Otuz", "Kirk", "Elli", "Altmis", "Yetmis", "Seksen", "Doksan"};
        String[] hundreds = {"", "Yuz", "Ikiyuz", "Ucyuz", "Dortyuz", "Besyuz", "Altiyuz", "Yediyuz", "Sekizyuz", "Dokuzyuz"};

        if (number < 10) return ones[number];
        if (number < 100) return tens[number / 10] + ones[number % 10];
        if (number < 1000) return hundreds[number / 100] + tens[(number % 100) / 10] + ones[number % 10];
        if (number < 10000) {
            int thousands = number / 1000;
            return (thousands == 1 ? "Bin" : ones[thousands] + "Bin") + convertNumberToWords(number % 1000);
        }
        if (number < 1000000) {
            return convertNumberToWords(number / 1000) + "Bin" + convertNumberToWords(number % 1000);
        }
        if (number < 1000000000) {
            return convertNumberToWords(number / 1000000) + "Milyon" + convertNumberToWords(number % 1000000);
        }
        return convertNumberToWords(number / 1000000000) + "Milyar" + convertNumberToWords(number % 1000000000);
    }
}
