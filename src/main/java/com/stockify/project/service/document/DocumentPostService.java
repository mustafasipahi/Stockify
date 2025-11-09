package com.stockify.project.service.document;

import com.stockify.project.converter.DocumentConverter;
import com.stockify.project.enums.DocumentType;
import com.stockify.project.exception.DocumentUploadException;
import com.stockify.project.generator.DocumentNumberGenerator;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.dto.SalesPrepareDto;
import com.stockify.project.model.entity.DocumentEntity;
import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.model.response.InvoiceCreateResponse;
import com.stockify.project.model.response.PaymentDocumentResponse;
import com.stockify.project.model.response.SalesDocumentResponse;
import com.stockify.project.repository.DocumentRepository;
import com.stockify.project.service.ImagePostService;
import com.stockify.project.service.pdf.PdfPostService;
import com.stockify.project.validator.DocumentUploadValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.stockify.project.generator.DocumentNameGenerator.createPdfName;
import static com.stockify.project.util.NameUtil.getBrokerUsername;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentPostService {

    private final DocumentUploadValidator uploadValidator;
    private final DocumentRepository documentRepository;
    private final SalesDocumentService salesDocumentService;
    private final PaymentDocumentService paymentDocumentService;
    private final PdfPostService pdfPostService;
    private final ImagePostService imagePostService;

    @Transactional
    public DocumentResponse uploadSalesPdf(SalesPrepareDto prepareDto) {
        try {
            String documentNumber = DocumentNumberGenerator.getSalesDocumentNumber();
            prepareDto.getSales().setDocumentNumber(documentNumber);
            SalesDocumentResponse salesPDF = salesDocumentService.generatePDF(prepareDto);
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(prepareDto.getBroker(), DocumentType.VOUCHER);
            return uploadPdf(uploadRequest, documentNumber, salesPDF.getFile());
        } catch (Exception e) {
            log.error("Upload Sales Pdf Error", e);
            throw new DocumentUploadException();
        }
    }

    @Transactional
    public DocumentResponse uploadPaymentPdf(PaymentDto paymentDto) {
        try {
            String documentNumber = DocumentNumberGenerator.getPaymentDocumentNumber();
            paymentDto.setDocumentNumber(documentNumber);
            PaymentDocumentResponse paymentPDF = paymentDocumentService.generatePDF(paymentDto);
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(paymentDto.getBroker(), DocumentType.RECEIPT);
            return uploadPdf(uploadRequest, documentNumber, paymentPDF.getFile());
        } catch (Exception e) {
            log.error("Upload Payment Pdf Error", e);
            throw new DocumentUploadException();
        }
    }

    @Transactional
    public DocumentResponse uploadInvoicePdf(SalesPrepareDto prepareDto, InvoiceCreateResponse invoice) {
        BrokerDto brokerDto = new BrokerDto();
        brokerDto.setBrokerId(prepareDto.getSales().getBrokerId());
        DocumentUploadRequest uploadRequest = new DocumentUploadRequest(brokerDto, DocumentType.INVOICE);
        return uploadPdf(uploadRequest, prepareDto.getSales().getDocumentNumber(), invoice);
    }

    @Transactional
    public DocumentResponse uploadProfileImage(MultipartFile file) {
        try {
            String documentNumber = DocumentNumberGenerator.getProfileImageNumber();
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(null, DocumentType.PROFILE_IMAGES);
            return uploadImage(uploadRequest, documentNumber, file);
        } catch (Exception e) {
            log.error("Upload Profile Image Error", e);
            throw new DocumentUploadException();
        }
    }

    @Transactional
    public DocumentResponse uploadCompanyLogo(MultipartFile file) {
        try {
            String documentNumber = DocumentNumberGenerator.getCompanyLogoNumber();
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(null, DocumentType.COMPANY_LOGO);
            return uploadImage(uploadRequest, documentNumber, file);
        } catch (Exception e) {
            log.error("Upload Company Logo Error", e);
            throw new DocumentUploadException();
        }
    }

    private DocumentResponse uploadPdf(DocumentUploadRequest request, String documentNumber, MultipartFile file) {
        uploadValidator.validate(file, request);
        try {
            String pdfName = createPdfName(getBrokerUsername(request.getBrokerDto()), request.getDocumentType());
            String path = pdfPostService.uploadPdf(file, pdfName, request.getDocumentType());
            DocumentEntity document = DocumentConverter.toEntity(request, null, getDocumentNumber(documentNumber), pdfName, path);
            DocumentEntity savedDocument = documentRepository.save(document);
            return DocumentConverter.toResponse(savedDocument, file);
        } catch (Exception e) {
            log.error("Upload Pdf Error", e);
            throw new DocumentUploadException();
        }
    }

    private DocumentResponse uploadPdf(DocumentUploadRequest request, String documentNumber, InvoiceCreateResponse invoice) {
        DocumentEntity document = DocumentConverter.toEntity(request, invoice.getEttn(), getDocumentNumber(documentNumber), "invoice", "/out");
        DocumentEntity savedDocument = documentRepository.save(document);
        return DocumentConverter.toResponse(savedDocument, null);
    }

    private DocumentResponse uploadImage(DocumentUploadRequest uploadRequest, String documentNumber, MultipartFile file) {
        try {
            String imageName = file.getOriginalFilename();
            String path = imagePostService.uploadImages(file, imageName, uploadRequest.getDocumentType());
            DocumentEntity document = DocumentConverter.toEntity(uploadRequest, null, getDocumentNumber(documentNumber), imageName, path);
            DocumentEntity savedDocument = documentRepository.save(document);
            return DocumentConverter.toResponse(savedDocument, file);
        } catch (Exception e) {
            log.error("Upload Image Error", e);
            throw new DocumentUploadException();
        }
    }

    private String getDocumentNumber(String documentNumber) {
        if (StringUtils.isBlank(documentNumber)) {
            return DocumentNumberGenerator.getUnknownDocumentNumber();
        }
        return documentNumber;
    }
}