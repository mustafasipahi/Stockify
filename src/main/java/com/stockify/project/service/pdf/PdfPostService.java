package com.stockify.project.service.pdf;

import com.stockify.project.configuration.properties.SupabaseProperties;
import com.stockify.project.enums.TenantType;
import com.stockify.project.exception.PdfException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Map;

import static com.stockify.project.constant.StockifyConstants.PATH;
import static com.stockify.project.util.TenantContext.getTenantId;

@Slf4j
@Service
@AllArgsConstructor
public class PdfPostService {

    private final WebClient supabaseWebClient;
    private final SupabaseProperties supabaseProperties;

    public Map<String, String> uploadPdf(MultipartFile file, String fileName) {
        try {
            String bucketName = supabaseProperties.getBucket();
            String path = "/storage/v1/object/" + bucketName + PATH + TenantType.fromValue(getTenantId()) + PATH + fileName;
            MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
            ByteArrayResource fileResource = new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };
            parts.add("file", fileResource);
            Map<String, Object> response = supabaseWebClient.post()
                    .uri(path)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(BodyInserters.fromMultipartData(parts))
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .timeout(Duration.ofSeconds(30))
                    .block();
            String returnedPath = response != null && response.get("Key") instanceof String
                    ? (String) response.get("Key")
                    : bucketName + PATH + TenantType.fromValue(getTenantId()) + PATH + fileName;
            return Map.of(
                    "bucket", bucketName,
                    "objectName", fileName,
                    "path", returnedPath,
                    "fullPath", bucketName + "/" + fileName
            );
        } catch (Exception e) {
            throw new PdfException();
        }
    }
}
