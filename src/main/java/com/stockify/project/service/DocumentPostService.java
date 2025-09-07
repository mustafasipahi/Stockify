package com.stockify.project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.stockify.project.converter.DocumentConverter;
import com.stockify.project.enums.DocumentType;
import com.stockify.project.exception.DocumentNotFoundException;
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
import java.util.UUID;

import static com.stockify.project.util.DateUtil.getDocumentNameDate;
import static com.stockify.project.util.DocumentUtil.replaceCharacter;
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
    private final Cloudinary cloudinary;

    @Transactional
    public DocumentResponse uploadSalesFile(SalesPrepareDto prepareDto) {
        try {
            Long tenantId = getTenantId();
            CompanyInfoDto companyInfo = companyGetService.getCompanyInfo(tenantId);
            SalesDocumentResponse salesPDF = salesDocumentService.generatePDF(companyInfo, prepareDto);
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(prepareDto.getBroker().getBrokerId(), DocumentType.VOUCHER);
            return uploadFileToCloud(salesPDF.getFile(), uploadRequest, getUsername());
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
    public DocumentResponse uploadFile(MultipartFile file, DocumentUploadRequest request, String username) {
        return uploadFileToCloud(file, request, username);
    }

    public DocumentResponse uploadFileToCloud(MultipartFile file, DocumentUploadRequest request, String username) {
        uploadValidator.validate(file, request);
        try {
            String documentNameDate = getDocumentNameDate();
            String originalFilename = file.getOriginalFilename();
            String safeFileName = replaceCharacter(username + "_" + documentNameDate + "_" + originalFilename);
            String publicId = generatePublicId(username, request.getBrokerId(), request.getDocumentType());
            Map<String, Object> uploadParams = buildCloudinaryUploadParams(publicId, request, username, safeFileName);
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            DocumentEntity document = DocumentConverter.toEntity(request, uploadResult, originalFilename, safeFileName, file, username);
            DocumentEntity savedDocument = documentRepository.save(document);
            return DocumentConverter.toResponse(savedDocument);
        } catch (Exception e) {
            log.error("Upload File Error", e);
            throw new DocumentUploadException();
        }
    }

    @Transactional
    public void deleteDocument(Long documentId) {
        try {
            DocumentEntity document = documentRepository.findByIdAndTenantId(documentId, getTenantId())
                    .orElseThrow(DocumentNotFoundException::new);
            cloudinary.uploader().destroy(document.getCloudinaryPublicId(), ObjectUtils.emptyMap());
            documentRepository.deleteByIdAndTenantId(documentId, getTenantId());
        } catch (Exception e) {
            log.error("Delete File Error", e);
            throw new DocumentUploadException();
        }
    }

    private Map<String, Object> buildCloudinaryUploadParams(String publicId, DocumentUploadRequest request, String username, String originalFilename) {
        String folderPath = String.format("stockify/%s", getUsername());
        Map<String, Object> context = Map.of(
                "tenant_id", getTenantId().toString(),
                "broker_id", request.getBrokerId().toString(),
                "document_type", request.getDocumentType().name(),
                "uploaded_by", username,
                "original_filename", originalFilename
        );
        return Map.of(
                "public_id", publicId,
                "folder", folderPath,
                "resource_type", "auto",
                "context", context
        );
    }

    private String generatePublicId(String username, Long brokerId, DocumentType documentType) {
        return String.format("%s_%s_%s_%s_%s",
                getTenantId(),
                username,
                brokerId,
                documentType.name(),
                UUID.randomUUID().toString().substring(0, 8));
    }
}