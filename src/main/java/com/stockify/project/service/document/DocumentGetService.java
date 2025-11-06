package com.stockify.project.service.document;
import com.stockify.project.converter.DocumentConverter;
import com.stockify.project.exception.DocumentDownloadException;

import com.stockify.project.exception.DocumentNotFoundException;
import com.stockify.project.model.entity.DocumentEntity;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.repository.DocumentRepository;
import com.stockify.project.service.InvoiceGetService;
import com.stockify.project.service.pdf.PdfGetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Set;

import static com.stockify.project.util.TenantContext.getTenantId;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentGetService {

    private final DocumentRepository documentRepository;
    private final PdfGetService pdfGetService;
    private final InvoiceGetService invoiceGetService;

    public ResponseEntity<InputStreamResource> downloadFile(Long documentId) {
        try {
            DocumentEntity document = documentRepository.findByIdAndTenantId(documentId, getTenantId())
                    .orElseThrow(DocumentNotFoundException::new);
            if (StringUtils.isNotBlank(document.getOutId())) {
                return downloadFileFromOut(document);
            } else {
                return downloadFileFromDb(document);
            }
        } catch (Exception e) {
            throw new DocumentDownloadException();
        }
    }

    public List<DocumentResponse> getAllDocument(Long brokerId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        List<DocumentEntity> documents = documentRepository.findByBrokerIdAndTenantId(brokerId, getTenantId(), sort);
        return documents.stream()
                .map(documentEntity -> DocumentConverter.toResponse(documentEntity, null))
                .toList();
    }

    public List<DocumentResponse> getAllDocument(Set<Long> documentIds) {
        return documentRepository.findAllByIdInAndTenantId(documentIds, getTenantId()).stream()
                .map(documentEntity -> DocumentConverter.toResponse(documentEntity, null))
                .toList();
    }

    private ResponseEntity<InputStreamResource> downloadFileFromDb(DocumentEntity document) {
        byte[] documentBytes = pdfGetService.downloadPdf(document.getFileName(), document.getDocumentType());
        ByteArrayInputStream bis = new ByteArrayInputStream(documentBytes);
        InputStreamResource inputStreamResource = new InputStreamResource(bis);
        HttpHeaders headers = getHttpHeaders(document, documentBytes);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(documentBytes.length)
                .body(inputStreamResource);
    }

    private ResponseEntity<InputStreamResource> downloadFileFromOut(DocumentEntity document) {
        byte[] documentBytes = invoiceGetService.downloadInvoice(document.getOutId());
        ByteArrayInputStream bis = new ByteArrayInputStream(documentBytes);
        InputStreamResource inputStreamResource = new InputStreamResource(bis);
        HttpHeaders headers = getHttpHeaders(document, documentBytes);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(documentBytes.length)
                .body(inputStreamResource);
    }

    private static HttpHeaders getHttpHeaders(DocumentEntity document, byte[] documentBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        headers.add(HttpHeaders.CONTENT_LENGTH, String.valueOf(documentBytes.length));
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        headers.add(HttpHeaders.EXPIRES, "0");
        return headers;
    }
}
