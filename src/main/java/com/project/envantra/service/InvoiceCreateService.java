package com.project.envantra.service;

import com.project.envantra.configuration.properties.InvoiceProperties;
import com.project.envantra.exception.EnvantraRuntimeException;
import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.response.InvoiceTokenResponse;
import com.project.envantra.model.dto.SalesPrepareDto;
import com.project.envantra.model.request.InvoiceCreateRequest;
import com.project.envantra.model.response.InvoiceCreateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.project.envantra.converter.InvoiceConverter.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvoiceCreateService {

    private final RestTemplate restTemplate;
    private final InvoiceProperties invoiceProperties;
    private final InvoiceTokenService invoiceTokenService;

    public InvoiceCreateResponse createInvoice(SalesPrepareDto prepareDto) {
        CompanyDto company = prepareDto.getCompany();
        InvoiceTokenResponse invoiceTokenResponse = invoiceTokenService.prepareToken(company.getInvoiceUsername(), company.getInvoicePassword());
        InvoiceCreateResponse invoiceCreateResponse = prepareInvoice(prepareDto, invoiceTokenResponse);
        log.info("Fatura oluşturma işlemi başarılı: {}", invoiceCreateResponse);
        return invoiceCreateResponse;
    }

    private InvoiceCreateResponse prepareInvoice(SalesPrepareDto prepareDto, InvoiceTokenResponse token) {
        try {
            String url = invoiceProperties.getBaseUrl() + "/api/app/invoice-outbox/send-invoice-from-out?UseTurmob=false";
            HttpHeaders headers = createInvoiceHeaders(token.getAccessToken());
            InvoiceCreateRequest body = createInvoiceRequestBody(prepareDto);
            HttpEntity<InvoiceCreateRequest> request = new HttpEntity<>(body, headers);
            ResponseEntity<InvoiceCreateResponse> response = restTemplate.postForEntity(
                    url,
                    request,
                    InvoiceCreateResponse.class);
            InvoiceCreateResponse responseDto = response.getBody();
            validateInvoiceResponse(responseDto);
            return responseDto;
        } catch (Exception e) {
            String errorMessage = createInvoiceErrorMessage(e);
            log.error("Fatura gönderme hatası: {}", errorMessage);
            throw new EnvantraRuntimeException(errorMessage);
        }
    }
}