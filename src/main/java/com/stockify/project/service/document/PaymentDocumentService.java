package com.stockify.project.service.document;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.stockify.project.exception.DocumentUploadException;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.other.ByteArrayMultipartFile;
import com.stockify.project.model.response.PaymentDocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;

import static com.stockify.project.util.DocumentUtil.replaceCharacter;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentDocumentService {

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String DEFAULT_FILENAME = "payment.pdf";
    private static final String DEFAULT_CONTENT_TYPE = "application/pdf";
    private static final String DEFAULT_CURRENCY = "TL";

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

        // Şirket bilgileri (hardcoded veya configuration'dan alınabilir)
        html = html.replace("{{company_name}}", "STOCKIFY YAZILIM A.S.");
        html = html.replace("{{company_address}}", replaceCharacter("Teknokent Mah. Silikon Vadisi Cad. No:123/A\nAnkara/TURKIYE"));
        html = html.replace("{{company_phone}}", "0312 123 45 67");
        html = html.replace("{{document_title}}", "TAHSILAT MAKBUZU");
        html = html.replace("{{document_number}}", replaceCharacter(paymentDto.getDocumentNumber() != null ? paymentDto.getDocumentNumber() : ""));

        // Ödeme bilgileri
        html = html.replace("{{payment_amount}}", MONEY_FORMAT.format(paymentDto.getPrice()));
        html = html.replace("{{payment_date}}", paymentDto.getCreatedDate() != null ? paymentDto.getCreatedDate().format(DATE_FORMAT) : "");

        // Broker bilgileri
        String payerName = "";
        if (paymentDto.getBroker() != null) {
            payerName = replaceCharacter(
                    (paymentDto.getBroker().getFirstName() != null ? paymentDto.getBroker().getFirstName() : "") + " " +
                            (paymentDto.getBroker().getLastName() != null ? paymentDto.getBroker().getLastName() : "")
            ).trim();
        }
        html = html.replace("{{payer_name}}", payerName);
        html = html.replace("{{currency}}", DEFAULT_CURRENCY);

        // Ödeme tipi
        String paymentType = "";
        if (paymentDto.getType() != null) {
            paymentType = replaceCharacter(paymentDto.getType().getName());
        }
        html = html.replace("{{payment_type}}", paymentType);

        // Yazı ile tutar
        html = html.replace("{{amount_in_words}}", convertToWords(paymentDto.getPrice()));

        return html;
    }

    /**
     * Sayıyı yazıya çevirir (basit implementasyon)
     */
    private String convertToWords(BigDecimal amount) {
        if (amount == null) return "Sifir TL";

        int intPart = amount.intValue();
        int decimalPart = amount.remainder(BigDecimal.ONE).multiply(new BigDecimal("100")).intValue();

        return convertNumberToWords(intPart) + " TL" +
                (decimalPart > 0 ? " " + convertNumberToWords(decimalPart) + " Kr" : "");
    }

    private String convertNumberToWords(int number) {
        if (number == 0) return "Sifir";
        if (number < 0) return "Eksi " + convertNumberToWords(-number);

        String[] ones = {"", "Bir", "Iki", "Uc", "Dort", "Bes", "Alti", "Yedi", "Sekiz", "Dokuz"};
        String[] tens = {"", "On", "Yirmi", "Otuz", "Kirk", "Elli", "Altmis", "Yetmis", "Seksen", "Doksan"};
        String[] hundreds = {"", "Yuz", "Ikiyuz", "Ucyuz", "Dortyuz", "Besyuz", "Altiyuz", "Yediyuz", "Sekizyuz", "Dokuzyuz"};

        if (number < 10) return ones[number];
        if (number < 100) return tens[number / 10] + ones[number % 10];
        if (number < 1000) return hundreds[number / 100] + tens[(number % 100) / 10] + ones[number % 10];
        if (number < 10000) {
            int thousands = number / 1000;
            return (thousands == 1 ? "Bin" : ones[thousands] + "Bin") + convertNumberToWords(number % 1000);
        }
        if (number < 1000000) {
            return convertNumberToWords(number / 1000) + "Bin" + convertNumberToWords(number % 1000);
        }
        if (number < 1000000000) {
            return convertNumberToWords(number / 1000000) + "Milyon" + convertNumberToWords(number % 1000000);
        }

        return convertNumberToWords(number / 1000000000) + "Milyar" + convertNumberToWords(number % 1000000000);
    }

    private PaymentDocumentResponse generate(String html) {
        try {
            byte[] pdfBytes = createPDFAsBytes(html);
            MultipartFile pdfFile = new ByteArrayMultipartFile(pdfBytes, DEFAULT_FILENAME, DEFAULT_CONTENT_TYPE);
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