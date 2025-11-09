package com.project.envantra.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.envantra.exception.EnvantraRuntimeException;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.SalesDto;
import com.project.envantra.model.dto.SalesItemDto;
import com.project.envantra.model.dto.SalesPrepareDto;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.model.request.InvoiceCreateRequest;
import com.project.envantra.model.response.InvoiceCreateResponse;
import com.project.envantra.model.response.InvoiceTokenResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.project.envantra.util.LoginContext.getUser;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InvoiceConverter {

    private static final Integer SOURCE_ID = 15;
    private static final String UNIT = "AD";
    private static final String UNIT_CODE = "C62";
    private static final String COUNTRY = "Türkiye";

    public static HttpHeaders createTokenHeaders(String tenant) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("__tenant", tenant);
        return headers;
    }

    public static HttpHeaders createInvoiceHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);
        return headers;
    }

    public static HttpHeaders createInvoiceDownloadUrlHeaders(String accessToken, String tenant) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.set("Cookie", ".AspNetCore.Culture=c%3Den%7Cuic%3Den; __tenant=" + tenant);
        return headers;
    }

    public static HttpHeaders createInvoiceDownloadHeaders(HttpHeaders headers) {
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_PDF, MediaType.APPLICATION_OCTET_STREAM));
        return headers;
    }

    public static MultiValueMap<String, String> createTokenRequestBody(String username, String password, String clientId) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("scope", "offline_access xfatura");
        body.add("username", username);
        body.add("password", password);
        body.add("client_id", clientId);
        return body;
    }

    public static InvoiceCreateRequest createInvoiceRequestBody(SalesPrepareDto prepareDto) {
        SalesDto sales = prepareDto.getSales();
        BrokerDto broker = prepareDto.getBroker();
        List<SalesItemDto> salesItems = prepareDto.getSalesItems();
        return InvoiceCreateRequest.builder()
                .masterClientERPReference(null)
                .masterClientERPCode(getMasterClientCode())
                .invoiceERPReference(sales.getDocumentNumber())
                .invoice(createInvoice(sales, broker))
                .invoiceLines(createInvoiceLines(salesItems))
                .build();
    }

    public static String getMasterClientCode() {
        UserEntity user = getUser();
        if (StringUtils.isNotBlank(user.getVkn())) {
            return user.getVkn();
        }
        return user.getTkn();
    }

    public static InvoiceCreateRequest.InvoiceRequest createInvoice(SalesDto sales, BrokerDto broker) {
        String fullName = broker.getFirstName() + " " + broker.getLastName();
        UserEntity user = getUser();
        return InvoiceCreateRequest.InvoiceRequest.builder()
                .tcknVn(user.getTkn())
                .taxOffice("")
                .country(COUNTRY)
                .city("")
                .town("")
                .buildingName(null)
                .buildingNumber(null)
                .doorNumber(null)
                .postCode(null)
                .streetName("")
                .email(user.getEmail())
                .phone(null)
                .fax(null)
                .webAddress(null)
                .invoiceDate(LocalDateTime.now().withNano(0))
                .note1(null)
                .subTotal(sales.getSubtotalPrice())
                .discountTotal(sales.getDiscountPrice())
                .totalWithDiscount(sales.getTotalPrice())
                .vatAmount(sales.getTotalTaxPrice())
                .totalWithVat(sales.getTotalPriceWithTax())
                .title(fullName)
                .name(broker.getFirstName())
                .surname(broker.getLastName())
                .sourceId(SOURCE_ID)
                .build();
    }

    public static List<InvoiceCreateRequest.InvoiceLineRequest> createInvoiceLines(List<SalesItemDto> salesItems) {
        return salesItems.stream()
                .map(item -> {
                    BigDecimal quantity = BigDecimal.valueOf(item.getProductCount());
                    return InvoiceCreateRequest.InvoiceLineRequest.builder()
                            .productName(item.getProductName())
                            .quantity(quantity)
                            .unit(UNIT)
                            .price(item.getUnitPrice())
                            .discountRate(item.getDiscountRate())
                            .discountAmount(item.getDiscountPrice())
                            .subTotal(item.getTotalPrice())
                            .totalWithDiscount(item.getPriceAfterDiscount())
                            .vatRate(item.getTaxRate())
                            .vatAmount(item.getTaxPrice())
                            .totalWithVat(item.getTotalPriceWithTax())
                            .unitCode(UNIT_CODE)
                            .productCode(null)
                            .build();
                })
                .toList();
    }

    public static void validateInvoiceResponse(InvoiceCreateResponse response) {
        if (response == null) {
            throw new EnvantraRuntimeException("Fatura response alınamadı");
        }
        if (response.getHttpStatusCode() != null && response.getHttpStatusCode() != 200) {
            throw new EnvantraRuntimeException(response.getMessage());
        }
    }

    public static void validateInvoiceUrlResponse(ResponseEntity<String> documentUrlResponse) {
        if (!documentUrlResponse.getStatusCode().is2xxSuccessful()) {
            throw new EnvantraRuntimeException("Fatura url hatası: " + documentUrlResponse.getStatusCode());
        }
        if (documentUrlResponse.getBody() == null) {
            throw new EnvantraRuntimeException("Fatura URL'si alınamadı");
        }
    }

    public static void validateInvoiceDownloadResponse(ResponseEntity<byte[]> documentDownloadResponse) {
        if (documentDownloadResponse == null) {
            throw new EnvantraRuntimeException("Fatura indirilemedi");
        }
        if (!documentDownloadResponse.getStatusCode().is2xxSuccessful()) {
            throw new EnvantraRuntimeException("Fatura indirilemedi: " + documentDownloadResponse.getStatusCode());
        }
        if (documentDownloadResponse.getBody() == null) {
            throw new EnvantraRuntimeException("Fatura içeriği boş");
        }
    }

    public static String createTokenErrorMessage(Exception e) {
        if (e instanceof HttpClientErrorException httpClientErrorException) {
            try {
                String responseBody = httpClientErrorException.getResponseBodyAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                InvoiceTokenResponse errorResponse = objectMapper.readValue(responseBody, InvoiceTokenResponse.class);
                if (errorResponse.getErrorDescription() != null) {
                    return errorResponse.getErrorDescription();
                }
            } catch (Exception ex) {
                log.warn("Error response parse edilemedi", ex);
            }
        }
        if (e instanceof EnvantraRuntimeException envantraRuntimeException) {
            try {
                String message = envantraRuntimeException.getMessage();
                if (message != null) {
                    return message;
                }
            } catch (Exception ex) {
                log.warn("createTokenErrorMessage mesajı alınamadı", ex);
            }
        }
        return "Token alma işlemi başarısız";
    }

    public static String createInvoiceErrorMessage(Exception e) {
        if (e instanceof HttpClientErrorException httpClientErrorException) {
            try {
                String responseBody = httpClientErrorException.getResponseBodyAsString();
                ObjectMapper objectMapper = new ObjectMapper();
                InvoiceCreateResponse errorResponse = objectMapper.readValue(responseBody, InvoiceCreateResponse.class);
                if (errorResponse.getMessage() != null) {
                    return errorResponse.getMessage();
                }
            } catch (Exception ex) {
                log.warn("Invoice error response parse edilemedi", ex);
            }
        }
        if (e instanceof EnvantraRuntimeException envantraRuntimeException) {
            try {
                String message = envantraRuntimeException.getMessage();
                if (message != null) {
                    return message;
                }
            } catch (Exception ex) {
                log.warn("createInvoiceErrorMessage mesajı alınamadı", ex);
            }
        }
        return "Fatura gönderme işlemi başarısız";
    }

    public static String createDownloadErrorMessage(Exception e) {
        if (e instanceof HttpClientErrorException httpClientErrorException) {
            return "HTTP bağlantı tatası: " + httpClientErrorException.getStatusCode();
        }
        if (e instanceof EnvantraRuntimeException envantraRuntimeException) {
            try {
                String message = envantraRuntimeException.getMessage();
                if (message != null) {
                    return message;
                }
            } catch (Exception ex) {
                log.warn("createDownloadErrorMessage mesajı alınamadı", ex);
            }
        }
        return "Fatura indirme işlemi başarısız";
    }
}