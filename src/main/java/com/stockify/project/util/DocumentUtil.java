package com.stockify.project.util;

import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentUtil {

    public static String safeFileName(String originalFileName) {
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
                .replaceAll("^_+|_+$", "");
    }

    public static String encodeFileName(String originalFileName) {
        if (StringUtils.isEmpty(originalFileName)) {
            return null;
        }
        return URLEncoder.encode(originalFileName, StandardCharsets.UTF_8);
    }

    public static String getMetadataValue(GridFSFile file, String key, String defaultValue) {
        if (file.getMetadata() == null) {
            return defaultValue;
        }
        String value = (String) file.getMetadata().get(key);
        return value != null ? value : defaultValue;
    }

    public static LocalDateTime getMetadataDate(GridFSFile file, String key) {
        try {
            Document metadata = file.getMetadata();
            if (metadata == null) {
                return null;
            }
            Object value = file.getMetadata().get(key);
            if (value instanceof LocalDateTime ldt) {
                return ldt;
            }
            if (value instanceof Date d) {
                return d.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    public static String getDownloadUrl(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/documents/download/")
                .path(id)
                .toUriString();
    }
}
