package com.stockify.project.service.document;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.stockify.project.exception.DocumentUploadException;
import com.stockify.project.model.dto.CompanyDto;
import com.stockify.project.model.dto.DocumentAsByteDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.other.ByteArrayMultipartFile;
import com.stockify.project.model.response.PaymentDocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.stockify.project.constant.DocumentConstants.*;
import static com.stockify.project.util.DocumentUtil.convertNumberToWords;
import static com.stockify.project.util.DocumentUtil.replaceCharacter;
import static com.stockify.project.util.NameUtil.getBrokerFullName;
import static com.stockify.project.util.NameUtil.getUserFullName;
import static com.stockify.project.util.LoginContext.getUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentDocumentService {

    private final DocumentGetService documentGetService;

    public PaymentDocumentResponse generatePDF(PaymentDto paymentDto) throws IOException {
        String htmlTemplate = readHtmlTemplate();
        String filledHtml = fillTemplate(htmlTemplate, paymentDto);
        return generate(filledHtml);
    }

    private String readHtmlTemplate() throws IOException {
        try (InputStream inputStream = new ClassPathResource("/templates/payment_document.html").getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder html = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append("\n");
            }
            return html.toString();
        }
    }

    private String fillTemplate(String template, PaymentDto paymentDto) {
        String html = template;
        html = html.replace("{{company_name}}", replaceCharacter(paymentDto.getCompany().getName()));
        html = html.replace("{{company_address}}", replaceCharacter(paymentDto.getCompany().getAddress()));
        html = html.replace("{{company_phone}}", paymentDto.getCompany().getPhoneNumber());
        html = html.replace("{{document_title}}", "TAHSILAT MAKBUZU");
        html = html.replace("{{document_number}}", replaceCharacter(paymentDto.getDocumentNumber() != null ? paymentDto.getDocumentNumber() : ""));

        String logoBase64 = getCompanyLogoAsBase64(paymentDto.getCompany());
        html = html.replace("{{company_logo}}", logoBase64);
        html = html.replace("{{logo_display}}", StringUtils.isBlank(logoBase64) ? "display:none;" : "");

        html = html.replace("{{payment_amount}}", MONEY_FORMAT.format(paymentDto.getPrice()));
        html = html.replace("{{payment_date}}", paymentDto.getCreatedDate() != null ? paymentDto.getCreatedDate().format(DATE_FORMAT) : "");

        String creatorUserName = getUserFullName(getUser());
        String brokerName = getBrokerFullName(paymentDto.getBroker());

        html = html.replace("{{creator_user_name}}", creatorUserName);
        html = html.replace("{{broker_name}}", brokerName);
        html = html.replace("{{currency}}", DEFAULT_CURRENCY);

        html = html.replace("{{payment_type}}", paymentDto.getType() != null ? replaceCharacter(paymentDto.getType().getName()) : "");
        html = html.replace("{{amount_in_words}}", convertToWords(paymentDto.getPrice()));

        html = html.replace("{{brand_short}}", DEFAULT_BRAND_NAME);
        html = html.replace("{{brand_url}}", DEFAULT_BRAND_URL);
        return html;
    }

    private String getCompanyLogoAsBase64(CompanyDto company) {
        try {
            DocumentAsByteDto documentAsByte = documentGetService.getDocumentAsByte(company.getLogoImageId());
            String mimeType = determineMimeType(documentAsByte.getDocument().getFileName());
            String base64Logo = Base64.getEncoder().encodeToString(documentAsByte.getDocumentAsByte());
            return "data:" + mimeType + ";base64," + base64Logo;
        } catch (Exception e) {
            log.error("Error reading company logo", e);
            return "";
        }
    }

    private String determineMimeType(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".png")) {
            return "image/png";
        } else if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerFileName.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFileName.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "image/png";
    }

    private String convertToWords(BigDecimal amount) {
        if (amount == null) return "Sifir " + DEFAULT_CURRENCY;
        int intPart = amount.intValue();
        int decimalPart = amount.remainder(BigDecimal.ONE).multiply(new BigDecimal("100")).intValue();
        return convertNumberToWords(intPart) + " " + DEFAULT_CURRENCY + (decimalPart > 0 ? " " + convertNumberToWords(decimalPart) + " " + DEFAULT_CURRENCY_FRACTION : "");
    }

    private PaymentDocumentResponse generate(String html) {
        try {
            byte[] pdfBytes = createPDFAsBytes(html);
            MultipartFile pdfFile = new ByteArrayMultipartFile(pdfBytes, DEFAULT_PAYMENT_FILENAME, DEFAULT_CONTENT_TYPE);
            return PaymentDocumentResponse.builder()
                    .file(pdfFile)
                    .build();
        } catch (IOException e) {
            log.error("Payment Generate Error", e);
            throw new DocumentUploadException();
        }
    }

    private byte[] createPDFAsBytes(String html) throws IOException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setBaseUri("");
            converterProperties.setCharset("UTF-8");
            HtmlConverter.convertToPdf(html, outputStream, converterProperties);
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("Create PDF As Bytes Error", e);
            throw new IOException("Create PDF Error: " + e.getMessage(), e);
        }
    }
}