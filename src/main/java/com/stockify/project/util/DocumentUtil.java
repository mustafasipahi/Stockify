package com.stockify.project.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentUtil {

    public static String replaceCharacter(String originalFileName) {
        if (StringUtils.isEmpty(originalFileName)) {
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

    public static String replaceCharacterForFile(String originalFileName) {
        if (StringUtils.isEmpty(originalFileName)) {
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

    public static String encodeFileName(String originalFileName) {
        if (StringUtils.isEmpty(originalFileName)) {
            return null;
        }
        return URLEncoder.encode(originalFileName, StandardCharsets.UTF_8);
    }
}
