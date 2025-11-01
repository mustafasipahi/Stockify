package com.stockify.project.generator;

import com.stockify.project.enums.DocumentType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Locale;

import static com.stockify.project.constant.DocumentNameConstants.DATE_TIME_FORMATTER;
import static com.stockify.project.util.DocumentUtil.replaceCharacterDetail;
import static com.stockify.project.util.TenantContext.getUsername;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentNameGenerator {

    public static String createDocumentName(String brokerName, DocumentType documentType) {
        String creatorUserNameLower = getUsername().toLowerCase(Locale.ENGLISH);
        String brokerNameLower = brokerName.toLowerCase(Locale.ENGLISH);
        String documentNameDate = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String fileName = creatorUserNameLower + "_" + brokerNameLower + "_" + documentType + "_" + documentNameDate;
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            fileName = fileName + ".pdf";
        }
        return replaceCharacterDetail(fileName);
    }
}
