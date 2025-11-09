package com.project.envantra.service;

import com.project.envantra.configuration.properties.InvoiceProperties;
import com.project.envantra.exception.EnvantraRuntimeException;
import com.project.envantra.model.response.InvoiceTokenResponse;
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

import static com.project.envantra.converter.InvoiceConverter.*;

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
            String documentUrl = invoiceProperties.getBaseUrl() + "/api/app/invoice-outbox/get-invoice-outbox-pdf";
            HttpHeaders documentUrlHeaders = createInvoiceDownloadUrlHeaders(tokenResponse.getAccessToken(), outId);
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(documentUrl).queryParam("ettn", outId);
            HttpEntity<MultiValueMap<String, String>> documentUrlRequest = new HttpEntity<>(documentUrlHeaders);
            ResponseEntity<String> documentUrlResponse = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    documentUrlRequest,
                    String.class
            );
            validateInvoiceUrlResponse(documentUrlResponse);
            String pdfUrl = documentUrlResponse.getBody();
            HttpHeaders documentDownloadHeaders = createInvoiceDownloadHeaders(documentUrlHeaders);
            HttpEntity<MultiValueMap<String, String>> documentDownloadRequest = new HttpEntity<>(documentDownloadHeaders);
            ResponseEntity<byte[]> documentDownloadResponse = restTemplate.exchange(
                    pdfUrl,
                    HttpMethod.GET,
                    documentDownloadRequest,
                    byte[].class
            );
            validateInvoiceDownloadResponse(documentDownloadResponse);
            return documentDownloadResponse.getBody();
        } catch (Exception e) {
            String errorMessage = createDownloadErrorMessage(e);
            log.error("Fatura indirme hatası - ETTN: {}, Hata: {}", outId, errorMessage);
            throw new EnvantraRuntimeException("Fatura indirme hatası: " + errorMessage);
        }
    }
}
