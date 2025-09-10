package com.stockify.project.converter;

import com.stockify.project.model.entity.DocumentEntity;
import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.stockify.project.util.DocumentUtil.getDownloadUrl;
import static com.stockify.project.util.TenantContext.getTenantId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentConverter {

    public static DocumentEntity toEntity(DocumentUploadRequest request, String originalFilename,
                                          String bucket, String objectName, String path, String fullPath) {
        return DocumentEntity.builder()
                .brokerId(request.getBrokerId())
                .originalFilename(originalFilename)
                .fileName(objectName)
                .documentType(request.getDocumentType())
                .bucket(bucket)
                .path(path)
                .fullPath(fullPath)
                .tenantId(getTenantId())
                .build();
    }

    public static DocumentResponse toResponse(DocumentEntity document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .fileName(document.getFileName())
                .downloadUrl(getDownloadUrl(document.getId()))
                .build();
    }
}
