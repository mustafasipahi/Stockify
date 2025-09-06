package com.stockify.project.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.stockify.project.enums.DocumentType;
import com.stockify.project.exception.DocumentDownloadException;
import com.stockify.project.exception.DocumentNotFoundException;
import com.stockify.project.exception.DocumentUploadException;
import com.stockify.project.model.dto.CompanyInfoDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.dto.SalesPrepareDto;
import com.stockify.project.model.request.DocumentUploadRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.model.response.SalesDocumentResponse;
import com.stockify.project.service.document.SalesDocumentService;
import com.stockify.project.validator.DocumentUploadValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.metadata.HttpHeaders;
import org.bson.types.ObjectId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.stockify.project.util.DateUtil.getDocumentNameDate;
import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.DocumentUtil.*;
import static com.stockify.project.util.DocumentUtil.getMetadataValue;
import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUsername;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentService {

    private final DocumentUploadValidator uploadValidator;
    private final GridFsTemplate gridFsTemplate;
    private final GridFSBucket gridFSBucket;
    private final CompanyGetService companyGetService;
    private final SalesDocumentService salesDocumentService;

    public DocumentResponse uploadSalesFile(SalesPrepareDto prepareDto) {
        try {
            Long tenantId = getTenantId();
            CompanyInfoDto companyInfo = companyGetService.getCompanyInfo(tenantId);
            SalesDocumentResponse salesPDF = salesDocumentService.generatePDF(companyInfo, prepareDto);
            DocumentUploadRequest uploadRequest = new DocumentUploadRequest(prepareDto.getBroker().getBrokerId(), DocumentType.VOUCHER);
            return uploadFile(salesPDF.getFile(), uploadRequest, getUsername());
        } catch (Exception e) {
            log.error("Upload Sales File Error", e);
            throw new DocumentUploadException();
        }
    }

    public DocumentResponse uploadPaymentFile(PaymentDto paymentDto) {
        return DocumentResponse.builder()
                .id("test")
                .build();
    }

    public DocumentResponse uploadFile(MultipartFile file, DocumentUploadRequest request, String username) {
        uploadValidator.validate(file, request);
        try {
            String documentNameDate = getDocumentNameDate();
            String originalFilename = file.getOriginalFilename();
            String safeFileName = replaceCharacter(username + "_" + documentNameDate + "_" + originalFilename);
            String safeOriginalFilename = replaceCharacter(originalFilename);
            DBObject metadata = new BasicDBObject();
            metadata.put("safeFileName", safeFileName);
            metadata.put("tenantId", getTenantId());
            metadata.put("brokerId", request.getBrokerId());
            metadata.put("documentType", request.getDocumentType());
            metadata.put("contentType", file.getContentType());
            metadata.put("createdDate", new Date());
            ObjectId fileId = gridFsTemplate.store(file.getInputStream(), safeOriginalFilename, file.getContentType(), metadata);
            return DocumentResponse.builder()
                    .id(fileId.toHexString())
                    .name(safeFileName)
                    .documentType(request.getDocumentType().name())
                    .contentType(file.getContentType())
                    .downloadUrl(getDownloadUrl(fileId.toHexString()))
                    .build();
        } catch (Exception e) {
            log.error("Upload File Error", e);
            throw new DocumentUploadException();
        }
    }

    public ResponseEntity<InputStreamResource> downloadFile(String fileId) {
        try {
            Query query = new Query();
            query.addCriteria(Criteria.where("_id").is(fileId));
            query.addCriteria(Criteria.where("metadata.tenantId").is(getTenantId()));
            Optional<GridFSFile> fileOptional = Optional.ofNullable(gridFsTemplate.findOne(query));
            GridFSFile file = fileOptional.orElseThrow(DocumentNotFoundException::new);
            String originalFileName = getMetadataValue(file, "safeFileName", "downloaded_file");
            String contentType = getMetadataValue(file, "contentType", "application/octet-stream");
            GridFSDownloadStream downloadStream = gridFSBucket.openDownloadStream(new ObjectId(fileId));
            InputStreamResource resource = new InputStreamResource(downloadStream);
            String encodedFileName = encodeFileName(originalFileName);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(file.getLength())
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + originalFileName + "\"; filename*=UTF-8''" + encodedFileName)
                    .body(resource);
        } catch (Exception e) {
            log.error("Download File Error", e);
            throw new DocumentDownloadException();
        }
    }

    public List<DocumentResponse> getAllDocument(Long brokerId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("metadata.brokerId").is(brokerId));
        query.addCriteria(Criteria.where("metadata.tenantId").is(getTenantId()));
        query.with(Sort.by(Sort.Direction.DESC, "metadata.createdDate"));
        List<DocumentResponse> responseList = new ArrayList<>();
        gridFsTemplate.find(query).forEach(file -> {
            DocumentResponse documentSearchResponse = new DocumentResponse();
            documentSearchResponse.setName(getMetadataValue(file, "safeFileName", null));
            documentSearchResponse.setDocumentType(getMetadataValue(file, "documentType", null));
            documentSearchResponse.setContentType(getMetadataValue(file, "contentType", null));
            documentSearchResponse.setUploadDate(getTime(getMetadataDate(file, "createdDate")));
            documentSearchResponse.setDownloadUrl(getDownloadUrl(file.getObjectId().toHexString()));
            responseList.add(documentSearchResponse);
        });
        return responseList;
    }
}
