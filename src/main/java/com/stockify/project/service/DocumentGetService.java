package com.stockify.project.service;

import com.stockify.project.converter.DocumentConverter;
import com.stockify.project.exception.DocumentDownloadException;
import com.stockify.project.exception.DocumentNotFoundException;
import com.stockify.project.model.entity.DocumentEntity;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.List;
import java.util.Set;

import static com.stockify.project.util.DocumentUtil.encodeFileName;
import static com.stockify.project.util.TenantContext.getTenantId;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentGetService {

    private final DocumentRepository documentRepository;

    public ResponseEntity<InputStreamResource> downloadFile(Long documentId) {
        try {
            DocumentEntity document = documentRepository.findByIdAndTenantId(documentId, getTenantId())
                    .orElseThrow(DocumentNotFoundException::new);
            URL url = new URL(document.getSecureUrl());
            byte[] fileData = url.openStream().readAllBytes();
            InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileData));
            String encodedFileName = encodeFileName(document.getSafeFilename());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .contentLength(fileData.length)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getSafeFilename() + "\"; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
        } catch (Exception e) {
            log.error("Download File Error", e);
            throw new DocumentDownloadException();
        }
    }

    public List<DocumentResponse> getAllDocument(Long brokerId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        List<DocumentEntity> documents = documentRepository.findByBrokerIdAndTenantId(brokerId, getTenantId(), sort);
        return documents.stream()
                .map(DocumentConverter::toResponse)
                .toList();
    }

    public List<DocumentResponse> getAllDocument(Set<Long> documentIds) {
        return documentRepository.findAllByIdInAndTenantId(documentIds, getTenantId()).stream()
                .map(DocumentConverter::toResponse)
                .toList();
    }

    public String getDownloadUrl(Long documentId) {
        DocumentEntity document = documentRepository.findByIdAndTenantId(documentId, getTenantId())
                .orElseThrow(DocumentNotFoundException::new);
        return document.getCloudinaryUrl();
    }
}
