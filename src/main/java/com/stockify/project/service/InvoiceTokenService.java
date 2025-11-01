package com.stockify.project.service;

import com.stockify.project.configuration.properties.InvoiceProperties;
import com.stockify.project.exception.StockifyRuntimeException;
import com.stockify.project.model.response.InvoiceTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.stockify.project.converter.InvoiceConverter.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceTokenService {

    private final RestTemplate restTemplate;
    private final InvoiceProperties invoiceProperties;

    public InvoiceTokenResponse prepareToken(String username, String password) {
        try {
            String url = invoiceProperties.getBaseUrl() + "/connect/token";
            HttpHeaders headers = createTokenHeaders(invoiceProperties.getTenant());
            MultiValueMap<String, String> body = createTokenRequestBody(username, password, invoiceProperties.getClientId());
            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            ResponseEntity<InvoiceTokenResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    InvoiceTokenResponse.class);
            return response.getBody();
        } catch (Exception e) {
            String errorMessage = createTokenErrorMessage(e);
            log.error("Token alma hatası: {}", errorMessage);
            throw new StockifyRuntimeException(errorMessage);
        }
    }
}
