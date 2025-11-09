package com.project.envantra.service.document;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.project.envantra.exception.DocumentUploadException;
import com.project.envantra.model.dto.*;
import com.project.envantra.model.other.ByteArrayMultipartFile;
import com.project.envantra.model.response.SalesDocumentResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static com.project.envantra.constant.DocumentConstants.*;
import static com.project.envantra.constant.TemplateUtil.SALES_PDF_DOCUMENT_TEMPLATE;
import static com.project.envantra.util.DocumentUtil.replaceCharacter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesDocumentService {

    public SalesDocumentResponse generatePDF(SalesPrepareDto prepareDto) throws IOException {
        String htmlTemplate = readHtmlTemplate();
        String filledHtml = fillTemplate(htmlTemplate, prepareDto);
        return generate(filledHtml);
    }

    private String readHtmlTemplate() throws IOException {
        try (InputStream inputStream = new ClassPathResource(SALES_PDF_DOCUMENT_TEMPLATE).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder html = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line).append("\n");
            }
            return html.toString();
        }
    }

    private String fillTemplate(String template, SalesPrepareDto prepareDto) {
        SalesDto sales = prepareDto.getSales();
        List<SalesItemDto> salesItems = prepareDto.getSalesItems();
        BrokerDto broker = prepareDto.getBroker();
        CompanyDto company = prepareDto.getCompany();
        LocalDateTime now = LocalDateTime.now();
        String html = template;

        String pageSizeCSS = calculatePageSize(salesItems.size());
        html = html.replace("{{page_size}}", pageSizeCSS);

        html = html.replace("{{brand}}", replaceCharacter(company.getName()));
        html = html.replace("{{address}}", replaceCharacter(company.getAddress()));
        html = html.replace("{{customer}}", replaceCharacter(broker.getFirstName() + " " + broker.getLastName()));
        html = html.replace("{{issued_date}}", now.format(DATE_FORMAT));
        html = html.replace("{{issued_time}}", now.format(TIME_FORMAT));
        html = html.replace("{{doc_no}}", replaceCharacter(sales.getDocumentNumber()));

        html = html.replace("{{th_stock}}", "URUN");
        html = html.replace("{{th_vat}}", "%KDV");
        html = html.replace("{{th_discount}}", "%ISK");
        html = html.replace("{{th_unit_price}}", "B.FIYAT");
        html = html.replace("{{th_amount}}", "TUTAR");

        html = html.replace("{{lbl_subtotal}}", "Ara Toplam");
        html = html.replace("{{lbl_discount}}", "Iskonto");
        html = html.replace("{{lbl_total}}", "Net Toplam");
        html = html.replace("{{lbl_vat}}", "KDV");
        html = html.replace("{{lbl_grand}}", "GENEL TOPLAM");

        StringBuilder itemsHtml = new StringBuilder();
        for (SalesItemDto salesItem : salesItems) {
            itemsHtml.append("<tr>");
            itemsHtml.append("<td>");
            itemsHtml.append("<span class=\"sku\">")
                    .append(replaceCharacter(salesItem.getProductName()))
                    .append("</span>");
            itemsHtml.append("<span class=\"qty\">")
                    .append(QTY_FORMAT.format(salesItem.getProductCount()))
                    .append(" ").append("Adet")
                    .append("</span>");
            itemsHtml.append("</td>");

            itemsHtml.append("<td>%")
                    .append(salesItem.getTaxRate().setScale(0, RoundingMode.DOWN))
                    .append("</td>");

            itemsHtml.append("<td>");
            if (sales.getDiscountRate().compareTo(BigDecimal.ZERO) > 0) {
                itemsHtml.append("%")
                        .append(sales.getDiscountRate().setScale(0, RoundingMode.DOWN));
            } else {
                itemsHtml.append("-");
            }
            itemsHtml.append("</td>");
            itemsHtml.append("<td>").append(MONEY_FORMAT.format(salesItem.getUnitPrice())).append("</td>");
            itemsHtml.append("<td>").append(MONEY_FORMAT.format(salesItem.getTotalPriceWithTax())).append("</td>");
            itemsHtml.append("</tr>");
        }

        html = html.replace("{{items}}", itemsHtml.toString());
        html = html.replace("{{subtotal}}", MONEY_FORMAT.format(sales.getSubtotalPrice()) + " " + DEFAULT_CURRENCY);
        html = html.replace("{{discount}}", MONEY_FORMAT.format(sales.getDiscountPrice()) + " " + DEFAULT_CURRENCY);
        html = html.replace("{{total}}", MONEY_FORMAT.format(sales.getTotalPrice()) + " " + DEFAULT_CURRENCY);
        html = html.replace("{{vat}}", MONEY_FORMAT.format(sales.getTotalTaxPrice()) + " " + DEFAULT_CURRENCY);
        html = html.replace("{{grand}}", MONEY_FORMAT.format(sales.getTotalPriceWithTax()) + " " + DEFAULT_CURRENCY);

        BigDecimal brokerBalance = broker.getCurrentBalance();
        html = html.replace("{{old_balance}}", MONEY_FORMAT.format(brokerBalance) + " " + DEFAULT_CURRENCY);
        html = html.replace("{{current_balance}}", MONEY_FORMAT.format(brokerBalance.add(sales.getTotalPriceWithTax())) + " " + DEFAULT_CURRENCY);
        html = html.replace("{{footnote}}", "Tesekkurler");
        html = html.replace("{{brand_short}}", DEFAULT_BRAND_NAME);
        html = html.replace("{{brand_url}}", DEFAULT_BRAND_URL);
        return html;
    }

    private String calculatePageSize(int itemCount) {
        int totalHeightMM = 30 + (itemCount * 10) + 30 + 12;
        totalHeightMM = Math.max(totalHeightMM, 40);
        double widthPt = (80.0) * (2.834645669);
        double heightPt = totalHeightMM * (2.834645669);
        String widthStr = String.format(java.util.Locale.US, "%.2f", widthPt);
        String heightStr = String.format(java.util.Locale.US, "%.2f", heightPt);
        return widthStr + "pt " + heightStr + "pt";
    }

    private SalesDocumentResponse generate(String html) {
        try {
            byte[] pdfBytes = createPDFAsBytes(html);
            MultipartFile pdfFile = new ByteArrayMultipartFile(pdfBytes, DEFAULT_SALES_FILENAME, DEFAULT_CONTENT_TYPE);
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