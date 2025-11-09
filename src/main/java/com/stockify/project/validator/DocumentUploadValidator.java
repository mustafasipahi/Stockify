package com.stockify.project.validator;

import com.stockify.project.configuration.properties.FileStorageProperties;
import com.stockify.project.exception.*;
import com.stockify.project.model.request.DocumentUploadRequest;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@AllArgsConstructor
public class DocumentUploadValidator {

    private final FileStorageProperties fileStorageProperties;

    public void validate(MultipartFile file, DocumentUploadRequest request) {
        if (request.getBrokerDto() == null) {
            throw new BrokerIdException();
        }
        if (request.getBrokerDto().getBrokerId() == null) {
            throw new BrokerIdException();
        }
        if (request.getDocumentType() == null) {
            throw new DocumentTypeRequiredException();
        }
        if (file.isEmpty()) {
            throw new DocumentRequiredException();
        }
        if (!isAllowedContentType(file.getContentType())) {
            throw new InvalidContentTypeException();
        }
        String maxFileSize = fileStorageProperties.getMaxFileSize().toUpperCase();
        if (file.getSize() > getMaxFileSizeInBytes(maxFileSize)) {
            throw new InvalidDocumentSizeException(maxFileSize);
        }
    }

    private boolean isAllowedContentType(String contentType) {
        if (StringUtils.isBlank(contentType)) {
            return false;
        }
        List<String> allowedTypes = fileStorageProperties.getAllowedTypes();
        if (CollectionUtils.isEmpty(allowedTypes)) {
            return true;
        }
        return allowedTypes.contains(contentType);
    }

    private long getMaxFileSizeInBytes(String maxFileSize) {
        if (maxFileSize.endsWith("MB")) {
            return Long.parseLong(maxFileSize.replace("MB", "")) * 1024 * 1024;
        } else if (maxFileSize.endsWith("KB")) {
            return Long.parseLong(maxFileSize.replace("KB", "")) * 1024;
        } else if (maxFileSize.endsWith("GB")) {
            return Long.parseLong(maxFileSize.replace("GB", "")) * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(maxFileSize);
        }
    }
}
