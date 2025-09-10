package com.stockify.project.service.document;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.stockify.project.exception.DocumentUploadException;
import com.stockify.project.model.dto.*;
import com.stockify.project.model.other.ByteArrayMultipartFile;
import com.stockify.project.model.response.SalesDocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.stockify.project.util.DocumentUtil.replaceCharacter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesDocumentService {

    private static final DecimalFormat MONEY_FORMAT = new DecimalFormat("#,##0.00");
    private static final DecimalFormat QTY_FORMAT = new DecimalFormat("#,##0.###");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String DEFAULT_FILENAME = "sales.pdf";
    private static final String DEFAULT_CONTENT_TYPE = "application/pdf";
    private static final String DEFAULT_NOTE = "Teşekkürler";
    private static final String DEFAULT_BRAND_NAME = "Stockify";
    private static final String DEFAULT_BRAND_URL = "//www.stockify.com";

    public SalesDocumentResponse generatePDF(CompanyInfoDto companyInfoDto, SalesPrepareDto prepareDto) throws IOException {
        String htmlTemplate = readHtmlTemplate();
        String filledHtml = fillTemplate(htmlTemplate, companyInfoDto, prepareDto);
        return generate(filledHtml);
    }

    private String readHtmlTemplate() throws IOException {
        try (InputStream inputStream = new ClassPathResource("/templates/sales_document.html").getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder html = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append("\n");
            }
            return html.toString();
        }
    }

    private String fillTemplate(String template, CompanyInfoDto companyInfoDto, SalesPrepareDto prepareDto) {
        SalesDto sales = prepareDto.getSales();
        List<SalesItemDto> salesItems = prepareDto.getSalesItems();
        BrokerDto broker = prepareDto.getBroker();
        LocalDateTime now = LocalDateTime.now();
        String html = template;

        // Temel bilgiler - Türkçe karakterleri Latin'e çevir
        html = html.replace("{{brand}}", replaceCharacter(companyInfoDto.getCompanyName()));
        html = html.replace("{{address}}", replaceCharacter(companyInfoDto.getCompanyAddress()));
        html = html.replace("{{customer}}", broker.getFirstName() + " " + broker.getLastName());
        html = html.replace("{{issued_date}}", now.format(DATE_FORMAT));
        html = html.replace("{{issued_time}}", now.format(TIME_FORMAT));
        html = html.replace("{{doc_no}}", replaceCharacter(sales.getDocumentNumber()));

        // Items tablosu
        StringBuilder itemsHtml = new StringBuilder();
        for (SalesItemDto salesItem : salesItems) {
            itemsHtml.append("<tr>");

            // Ürün adı ve miktar
            itemsHtml.append("<td class=\"col-product\">");
            itemsHtml.append("<div class=\"item-name\">")
                    .append(replaceCharacter(salesItem.getProductName()))
                    .append("</div>");
            itemsHtml.append("<div class=\"item-qty\">")
                    .append(QTY_FORMAT.format(salesItem.getProductCount()))
                    .append("</div>");
            itemsHtml.append("</td>");

            // KDV%
            itemsHtml.append("<td class=\"col-vat\">%")
                    .append(salesItem.getTaxRate().setScale(0, RoundingMode.DOWN))
                    .append("</td>");

            // İskonto%
            itemsHtml.append("<td class=\"col-discount\">");
            if (sales.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
                itemsHtml.append("%")
                        .append(sales.getDiscountRate().setScale(0, RoundingMode.DOWN));
            } else {
                itemsHtml.append("-");
            }
            itemsHtml.append("</td>");

            // Birim Fiyat
            itemsHtml.append("<td class=\"col-price\">").append(MONEY_FORMAT.format(salesItem.getUnitPrice())).append("</td>");

            // Toplam Tutar
            itemsHtml.append("<td class=\"col-total\">").append(MONEY_FORMAT.format(salesItem.getTotalPriceWithTax())).append("</td>");

            itemsHtml.append("</tr>");
        }
        html = html.replace("{{items}}", itemsHtml.toString());

        // Tutarlar
        html = html.replace("{{subtotal}}", MONEY_FORMAT.format(sales.getSubtotalPrice()) + " TL");
        html = html.replace("{{discount}}", MONEY_FORMAT.format(sales.getDiscountPrice()) + " TL");
        html = html.replace("{{total}}", MONEY_FORMAT.format(sales.getTotalPrice()) + " TL");
        html = html.replace("{{vat}}", MONEY_FORMAT.format(sales.getTotalTaxPrice()) + " TL");
        html = html.replace("{{grand}}", MONEY_FORMAT.format(sales.getTotalPriceWithTax()) + " TL");

        // Bakiyeler
        BigDecimal brokerBalance = broker.getCurrentBalance();
        html = html.replace("{{old_balance}}", MONEY_FORMAT.format(brokerBalance) + " TL");
        html = html.replace("{{current_balance}}", MONEY_FORMAT.format(brokerBalance.add(sales.getTotalPriceWithTax())) + " TL");

        // Dipnot ve marka bilgileri
        html = html.replace("{{footnote}}", DEFAULT_NOTE);
        html = html.replace("{{brand_short}}", DEFAULT_BRAND_NAME);
        html = html.replace("{{brand_url}}", DEFAULT_BRAND_URL);

        return html;
    }

    private SalesDocumentResponse generate(String html) {
        try {
            byte[] pdfBytes = createPDFAsBytes(html);
            MultipartFile pdfFile = new ByteArrayMultipartFile(pdfBytes, DEFAULT_FILENAME , DEFAULT_CONTENT_TYPE);
            return SalesDocumentResponse.builder()
                    .file(pdfFile)
                    .build();
        } catch (IOException e) {
            log.error("Sale Generate Error", e);
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
