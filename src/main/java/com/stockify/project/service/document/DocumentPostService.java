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
import com.stockify.project.service.pdf.PdfPostService;
import com.stockify.project.validator.DocumentUploadValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.stockify.project.generator.DocumentNameGenerator.createDocumentName;
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

    @Transactional
    public DocumentResponse uploadSalesFile(SalesPrepareDto prepareDto) {
        try {
            String documentNumber = DocumentNumberGenerator.getSalesDocumentNumber();
            prepareDto.getSales().setDocumentNumber(documentNumber);
            SalesDocumentResponse salesPDF = salesDocumentService.generatePDF(prepareDto);
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(prepareDto.getBroker().getBrokerId(), DocumentType.VOUCHER);
            return uploadFileToCloud(uploadRequest, documentNumber, prepareDto.getBroker(), salesPDF.getFile());
        } catch (Exception e) {
            log.error("Upload Sales File Error", e);
            throw new DocumentUploadException();
        }
    }

    @Transactional
    public DocumentResponse uploadPaymentFile(PaymentDto paymentDto) {
        try {
            String documentNumber = DocumentNumberGenerator.getPaymentDocumentNumber();
            paymentDto.setDocumentNumber(documentNumber);
            PaymentDocumentResponse paymentPDF = paymentDocumentService.generatePDF(paymentDto);
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(paymentDto.getBroker().getBrokerId(), DocumentType.RECEIPT);
            return uploadFileToCloud(uploadRequest, documentNumber, paymentDto.getBroker(), paymentPDF.getFile());
        } catch (Exception e) {
            log.error("Upload Payment File Error", e);
            throw new DocumentUploadException();
        }
    }

    @Transactional
    public DocumentResponse uploadInvoiceFile(SalesPrepareDto prepareDto, InvoiceCreateResponse invoice) {
        DocumentUploadRequest uploadRequest = new DocumentUploadRequest(prepareDto.getSales().getBrokerId(), DocumentType.INVOICE);
        return uploadFileToCloud(uploadRequest, prepareDto.getSales().getDocumentNumber(), invoice);
    }

    @Transactional
    public DocumentResponse uploadRestFile(MultipartFile file, DocumentUploadRequest request) {
        String documentNumber = DocumentNumberGenerator.getUnknownDocumentNumber();
        return uploadFileToCloud(request, documentNumber, new BrokerDto(), file);
    }

    private DocumentResponse uploadFileToCloud(DocumentUploadRequest request, String documentNumber, BrokerDto broker, MultipartFile file) {
        uploadValidator.validate(file, request);
        try {
            String fileName = createDocumentName(getBrokerUsername(broker), request.getDocumentType());
            String path = pdfPostService.uploadPdf(file, fileName, request.getDocumentType());
            DocumentEntity document = DocumentConverter.toEntity(request, null, getDocumentNumber(documentNumber), fileName, path);
            DocumentEntity savedDocument = documentRepository.save(document);
            return DocumentConverter.toResponse(savedDocument, file);
        } catch (Exception e) {
            log.error("Upload File Error", e);
            throw new DocumentUploadException();
        }
    }

    private DocumentResponse uploadFileToCloud(DocumentUploadRequest request, String documentNumber, InvoiceCreateResponse invoice) {
        DocumentEntity document = DocumentConverter.toEntity(request, invoice.getEttn(), getDocumentNumber(documentNumber), "invoice", "/out");
        DocumentEntity savedDocument = documentRepository.save(document);
        return DocumentConverter.toResponse(savedDocument, null);
    }

    private String getDocumentNumber(String documentNumber) {
        if (StringUtils.isBlank(documentNumber)) {
            return DocumentNumberGenerator.getUnknownDocumentNumber();
        }
        return documentNumber;
    }
}