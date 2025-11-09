package com.stockify.project.converter;

import com.stockify.project.model.entity.DocumentEntity;
import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import static com.stockify.project.util.DocumentUtil.getDownloadUrl;
import static com.stockify.project.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentConverter {

    public static DocumentEntity toEntity(DocumentUploadRequest request, String outId, String documentNumber,
                                          String filename, String path) {
        return DocumentEntity.builder()
                .outId(outId)
                .creatorUserId(getUserId())
                .brokerId(request.getBrokerDto() != null ? request.getBrokerDto().getBrokerId() : null)
                .fileName(filename)
                .documentNumber(documentNumber)
                .documentType(request.getDocumentType())
                .path(path)
                .build();
    }

    public static DocumentResponse toResponse(DocumentEntity document, MultipartFile file) {
        return DocumentResponse.builder()
                .documentId(document.getId())
                .documentNumber(document.getDocumentNumber())
                .fileName(document.getFileName())
                .file(file)
                .downloadUrl(getDownloadUrl(document))
                .build();
    }
}
