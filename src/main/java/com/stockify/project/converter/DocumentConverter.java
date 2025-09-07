package com.stockify.project.converter;

import com.stockify.project.model.entity.DocumentEntity;
import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.TenantContext.getTenantId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentConverter {

    public static DocumentEntity toEntity(DocumentUploadRequest request, Map<String, Object> uploadResult,
                                          String originalFilename, String safeFileName,
                                          MultipartFile file, String username) {
        return DocumentEntity.builder()
                .tenantId(getTenantId())
                .brokerId(request.getBrokerId())
                .cloudinaryPublicId(uploadResult.get("public_id").toString())
                .originalFilename(originalFilename)
                .safeFilename(safeFileName)
                .cloudinaryUrl(uploadResult.get("url").toString())
                .secureUrl(uploadResult.get("secure_url").toString())
                .documentType(request.getDocumentType())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .uploadedBy(username)
                .build();
    }

    public static DocumentResponse toResponse(DocumentEntity document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .name(document.getSafeFilename())
                .documentType(document.getDocumentType().name())
                .contentType(document.getContentType())
                .uploadDate(getTime(document.getCreatedDate()))
                .downloadUrl(document.getSecureUrl())
                .build();
    }
}
