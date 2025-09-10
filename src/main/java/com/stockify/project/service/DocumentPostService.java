package com.stockify.project.service;
import com.stockify.project.converter.DocumentConverter;
import com.stockify.project.enums.DocumentType;
import com.stockify.project.exception.DocumentUploadException;
import com.stockify.project.model.dto.CompanyInfoDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.dto.SalesPrepareDto;

import com.stockify.project.model.entity.DocumentEntity;
import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.model.response.SalesDocumentResponse;
import com.stockify.project.repository.DocumentRepository;
import com.stockify.project.service.document.SalesDocumentService;
import com.stockify.project.validator.DocumentUploadValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

import static com.stockify.project.util.DateUtil.getDocumentNameDate;
import static com.stockify.project.util.DocumentUtil.createDocumentName;
import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUsername;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentPostService {

    private final DocumentUploadValidator uploadValidator;
    private final DocumentRepository documentRepository;
    private final CompanyGetService companyGetService;
    private final SalesDocumentService salesDocumentService;
    private final PdfPostService pdfPostService;

    @Transactional
    public DocumentResponse uploadSalesFile(SalesPrepareDto prepareDto) {
        try {
            Long tenantId = getTenantId();
            CompanyInfoDto companyInfo = companyGetService.getCompanyInfo(tenantId);
            SalesDocumentResponse salesPDF = salesDocumentService.generatePDF(companyInfo, prepareDto);
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(prepareDto.getBroker().getBrokerId(), DocumentType.VOUCHER);
            return uploadFileToCloud(salesPDF.getFile(), uploadRequest);
        } catch (Exception e) {
            log.error("Upload Sales File Error", e);
            throw new DocumentUploadException();
        }
    }

    public DocumentResponse uploadPaymentFile(PaymentDto paymentDto) {
        return DocumentResponse.builder()
                .id(1234L)
                .build();
    }

    @Transactional
    public DocumentResponse uploadFile(MultipartFile file, DocumentUploadRequest request) {
        return uploadFileToCloud(file, request);
    }

    public DocumentResponse uploadFileToCloud(MultipartFile file, DocumentUploadRequest request) {
        uploadValidator.validate(file, request);
        try {
            String documentNameDate = getDocumentNameDate();
            String originalFilename = file.getOriginalFilename();
            String fileName = createDocumentName(
                    request.getBrokerId(),
                    request.getDocumentType(),
                    documentNameDate,
                    originalFilename);
            Map<String, String> stringObjectMap = pdfPostService.uploadPdf(file, fileName);
            DocumentEntity document = DocumentConverter.toEntity(
                    request,
                    originalFilename,
                    stringObjectMap.getOrDefault("bucket", null),
                    stringObjectMap.getOrDefault("objectName", null),
                    stringObjectMap.getOrDefault("path", null),
                    stringObjectMap.getOrDefault("fullPath", null));
            DocumentEntity savedDocument = documentRepository.save(document);
            return DocumentConverter.toResponse(savedDocument);
        } catch (Exception e) {
            log.error("Upload File Error", e);
            throw new DocumentUploadException();
        }
    }
}