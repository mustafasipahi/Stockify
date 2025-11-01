package com.stockify.project.service;

import com.stockify.project.configuration.properties.InvoiceProperties;
import com.stockify.project.exception.StockifyRuntimeException;
import com.stockify.project.model.response.InvoiceTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static com.stockify.project.converter.InvoiceConverter.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceGetService {

    private final RestTemplate restTemplate;
    private final InvoiceProperties invoiceProperties;
    private final InvoiceTokenService invoiceTokenService;

    public byte[] downloadInvoice(String outId) {
        try {
            InvoiceTokenResponse tokenResponse = invoiceTokenService.prepareToken("mehmetali@birhesap.com.tr", "Abc123456!");
            String url = invoiceProperties.getBaseUrl() + "/api/app/invoice-outbox/get-invoice-outbox-pdf";
            HttpHeaders headers = createDownloadHeaders(tokenResponse.getAccessToken(), outId);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url).queryParam("ettn", outId);
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    request,
                    byte[].class);
            validateDownloadResponse(response);
            return response.getBody();
        } catch (Exception e) {
            String errorMessage = createDownloadErrorMessage(e);
            log.error("Fatura indirme hatası - ETTN: {}, Hata: {}", outId, errorMessage);
            throw new StockifyRuntimeException("Fatura indirme hatası: " + errorMessage);
        }
    }
}
