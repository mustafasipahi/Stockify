package com.stockify.project.converter;

import com.stockify.project.model.entity.DocumentEntity;
import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import static com.stockify.project.util.DocumentUtil.getDownloadUrl;
import static com.stockify.project.util.TenantContext.getTenantId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentConverter {

    public static DocumentEntity toEntity(DocumentUploadRequest request, String outId, String originalFilename,
                                          String bucket, String objectName, String path, String fullPath, String message) {
        return DocumentEntity.builder()
                .outId(outId)
                .brokerId(request.getBrokerId())
                .originalFilename(originalFilename)
                .fileName(objectName)
                .documentType(request.getDocumentType())
                .bucket(bucket)
                .path(path)
                .fullPath(fullPath)
                .tenantId(getTenantId())
                .message(message)
                .build();
    }

    public static DocumentResponse toResponse(DocumentEntity document, MultipartFile file) {
        return DocumentResponse.builder()
                .documentId(document.getId())
                .fileName(document.getFileName())
                .file(file)
                .downloadUrl(getDownloadUrl(document))
                .build();
    }
}
