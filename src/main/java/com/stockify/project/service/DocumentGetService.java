package com.stockify.project.service;

import com.cloudinary.Cloudinary;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Set;

import static com.stockify.project.util.DocumentUtil.encodeFileName;
import static com.stockify.project.util.TenantContext.getTenantId;
import static java.util.Base64.getEncoder;

@Slf4j
@Service
@AllArgsConstructor
public class DocumentGetService {

    private final DocumentRepository documentRepository;
    private final Cloudinary cloudinary;

    public ResponseEntity<InputStreamResource> downloadFile(Long documentId) {
        try {
            DocumentEntity document = documentRepository.findByIdAndTenantId(documentId, getTenantId())
                    .orElseThrow(DocumentNotFoundException::new);
            URL url = new URL(resolveDownloadUrl(document));
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

    private String resolveDownloadUrl(DocumentEntity document) {
        String contentType = document.getContentType();
        boolean isImage = contentType != null && contentType.startsWith("image/");
        if (isImage) {
            return document.getSecureUrl();
        }
        return generateAdminDownloadUrl(document.getCloudinaryPublicId());
    }

    private String generateAdminDownloadUrl(String publicId) {
        try {
            String cloudName = cloudinary.config.cloudName;
            String apiKey = cloudinary.config.apiKey;
            String apiSecret = cloudinary.config.apiSecret;
            URL endpoint = new URL("https://api.cloudinary.com/v1_1/" + cloudName + "/resources/download");
            HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            String basic = getEncoder().encodeToString((apiKey + ":" + apiSecret).getBytes());
            conn.setRequestProperty("Authorization", "Basic " + basic);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            String body = "public_id=" + URLEncoder.encode(publicId, java.nio.charset.StandardCharsets.UTF_8)
                    + "&resource_type=raw&type=authenticated";
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }
            try (InputStream is = conn.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode node = mapper.readTree(is);
                return node.get("url").asText();
            }
        } catch (Exception e) {
            log.error("Generate Admin Download URL Error", e);
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
