package com.stockify.project.service.pdf;

import com.stockify.project.configuration.properties.SupabaseProperties;
import com.stockify.project.enums.TenantType;
import com.stockify.project.exception.PdfException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

import static com.stockify.project.constant.StockifyConstants.PATH;
import static com.stockify.project.util.TenantContext.getTenantId;

@Slf4j
@Service
@AllArgsConstructor
public class PdfGetService {

    private final WebClient supabaseWebClient;
    private final SupabaseProperties supabaseProperties;

    public byte[] downloadPdf(String objectName) {
        try {
            String bucketName = supabaseProperties.getBucket();
            String path = "/storage/v1/object/" + bucketName + PATH + TenantType.fromValue(getTenantId()) + PATH + objectName;
            return supabaseWebClient.get()
                    .uri(path)
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .timeout(Duration.ofSeconds(30))
                    .block();
        } catch (Exception e) {
            throw new PdfException();
        }
    }
}
