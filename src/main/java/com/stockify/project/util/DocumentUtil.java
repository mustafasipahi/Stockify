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

    public static String replaceCharacterDetail(String originalFileName) {
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
}
