package com.project.envantra.service.document;
import com.project.envantra.converter.DocumentConverter;
import com.project.envantra.exception.DocumentDownloadException;

import com.project.envantra.exception.DocumentNotFoundException;
import com.project.envantra.model.dto.DocumentAsByteDto;
import com.project.envantra.model.dto.ImageDto;
import com.project.envantra.model.entity.DocumentEntity;
import com.project.envantra.model.response.DocumentResponse;
import com.project.envantra.repository.DocumentRepository;
import com.project.envantra.service.image.ImageGetService;
import com.project.envantra.service.invoice.InvoiceGetService;
import com.project.envantra.service.pdf.PdfGetService;
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

import static com.project.envantra.enums.DocumentType.isImage;
import static com.project.envantra.enums.DocumentType.isPdf;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentGetService {

    private final DocumentRepository documentRepository;
    private final PdfGetService pdfGetService;
    private final ImageGetService imageGetService;
    private final InvoiceGetService invoiceGetService;

    public ResponseEntity<InputStreamResource> downloadFile(Long documentId) {
        try {
            DocumentEntity document = documentRepository.findById(documentId)
                    .orElseThrow(DocumentNotFoundException::new);
            if (isPdf(document.getDocumentType())) {
                if (StringUtils.isNotBlank(document.getOutId())) {
                    return downloadPdfFromOut(document);
                } else {
                    return downloadPdfFromDb(document);
                }
            }
            if (isImage(document.getDocumentType())) {
                return downloadImage(document);
            }
            throw new DocumentDownloadException();
        } catch (Exception e) {
            throw new DocumentDownloadException();
        }
    }

    public DocumentAsByteDto getDocumentAsByte(Long documentId) {
        DocumentEntity document = documentRepository.findById(documentId)
                .orElseThrow(DocumentNotFoundException::new);
        byte[] bytes = imageGetService.downloadImage(document.getFileName(), document.getDocumentType());
        return DocumentAsByteDto.builder()
                .document(document)
                .documentAsByte(bytes)
                .build();
    }

    public DocumentResponse getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .map(documentEntity -> DocumentConverter.toResponse(documentEntity, null))
                .orElse(new DocumentResponse());
    }

    public List<DocumentResponse> getAllDocument(Long brokerId) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        List<DocumentEntity> documents = documentRepository.findByBrokerId(brokerId, sort);
        return documents.stream()
                .map(documentEntity -> DocumentConverter.toResponse(documentEntity, null))
                .toList();
    }

    public List<DocumentResponse> getAllDocument(Set<Long> documentIds) {
        return documentRepository.findAllByIdIn(documentIds).stream()
                .map(documentEntity -> DocumentConverter.toResponse(documentEntity, null))
                .toList();
    }

    public ImageDto getImages(Long profileImageId, Long companyLogoId) {
        return ImageDto.builder()
                .profileImageDownloadUr(profileImageId != null ? getDocument(profileImageId).getDownloadUrl() : null)
                .companyLogoDownloadUrl(companyLogoId != null ? getDocument(companyLogoId).getDownloadUrl() : null)
                .build();
    }

    private ResponseEntity<InputStreamResource> downloadPdfFromDb(DocumentEntity document) {
        byte[] documentBytes = pdfGetService.downloadPdf(document.getFileName(), document.getDocumentType());
        ByteArrayInputStream bis = new ByteArrayInputStream(documentBytes);
        InputStreamResource inputStreamResource = new InputStreamResource(bis);
        HttpHeaders headers = getHttpHeaders(document, documentBytes);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(documentBytes.length)
                .body(inputStreamResource);
    }

    private ResponseEntity<InputStreamResource> downloadPdfFromOut(DocumentEntity document) {
        byte[] documentBytes = invoiceGetService.downloadInvoice(document.getOutId());
        ByteArrayInputStream bis = new ByteArrayInputStream(documentBytes);
        InputStreamResource inputStreamResource = new InputStreamResource(bis);
        HttpHeaders headers = getHttpHeaders(document, documentBytes);
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(documentBytes.length)
                .body(inputStreamResource);
    }

    private ResponseEntity<InputStreamResource> downloadImage(DocumentEntity document) {
        byte[] documentBytes = imageGetService.downloadImage(document.getFileName(), document.getDocumentType());
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
