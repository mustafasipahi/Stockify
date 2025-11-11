package com.project.envantra.service.email;

import com.project.envantra.model.dto.SalesPrepareDto;
import com.project.envantra.model.response.DocumentResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.project.envantra.constant.DocumentConstants.DATE_TIME_FORMATTER_3;
import static com.project.envantra.constant.DocumentConstants.DEFAULT_BRAND_NAME;
import static com.project.envantra.constant.TemplateConstant.SALES_EMAIL_BUYER_TEMPLATE;
import static com.project.envantra.constant.TemplateConstant.SALES_EMAIL_SELLER_TEMPLATE;
import static com.project.envantra.util.EmailUtil.*;
import static com.project.envantra.util.NameUtil.getBrokerFullName;
import static com.project.envantra.util.LoginContext.getEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesEmailService {

    private final EmailService emailService;

    @Async
    public void sendSalesNotifications(SalesPrepareDto salesPrepareDto, DocumentResponse documentResponse) {
        if (salesPrepareDto == null || documentResponse == null) {
            log.warn("Invalid Request - salesPrepareDto or documentResponse null");
            return;
        }
        String userEmail = getEmail();
        String brokerEmail = salesPrepareDto.getBroker().getEmail();
        sendNotificationsInternal(salesPrepareDto, documentResponse, userEmail, brokerEmail);
        log.info("Payment Notification sent successfully for user: {}", userEmail);
    }

    private void sendNotificationsInternal(SalesPrepareDto salesPrepareDto, DocumentResponse documentResponse,
                                           String userEmail, String brokerEmail) {
        if (isValidEmail(userEmail)) {
            try {
                sendSellerNotification(salesPrepareDto, userEmail, documentResponse.getFile());
                log.info("Sent SellerNotification email to {}", maskEmail(userEmail));
            } catch (Exception e) {
                log.error("Send SellerNotification Error! Email: {}", maskEmail(userEmail), e);
            }
        } else {
            log.info("Company Email is invalid");
        }
        if (isValidEmail(brokerEmail)) {
            try {
                sendBuyerNotification(salesPrepareDto, brokerEmail, documentResponse.getFile());
                log.info("Sent BuyerNotification email to {}", maskEmail(brokerEmail));
            } catch (Exception e) {
                log.error("Send BuyerNotification Error! Email: {}", maskEmail(brokerEmail), e);
            }
        } else {
            log.error("Broker Email is invalid");
        }
    }

    private void sendSellerNotification(SalesPrepareDto salesData, String sellerEmail, MultipartFile file) throws MessagingException {
        String subject = createSellerSubject(salesData);
        String htmlContent = createEmailContent(SALES_EMAIL_SELLER_TEMPLATE, salesData);
        String fileName = createSellerFileName(salesData);
        sendEmailWithAttachment(sellerEmail, subject, htmlContent, file, fileName);
    }

    private void sendBuyerNotification(SalesPrepareDto salesData, String buyerEmail, MultipartFile file) throws MessagingException {
        String subject = createBuyerSubject(salesData);
        String htmlContent = createEmailContent(SALES_EMAIL_BUYER_TEMPLATE, salesData);
        String fileName = createBuyerFileName(salesData);
        sendEmailWithAttachment(buyerEmail, subject, htmlContent, file, fileName);
    }

    private void sendEmailWithAttachment(String to, String subject, String htmlContent,
                                         MultipartFile file, String fileName) throws MessagingException {
        emailService.sendEmailWithAttachment(to, subject, htmlContent, file, fileName);
    }

    private String createEmailContent(String templatePath, SalesPrepareDto salesData) throws MessagingException {
        try {
            String htmlTemplate = loadTemplate(templatePath);
            Map<String, String> templateVariables = buildTemplateVariables(salesData);
            return replacePlaceholders(htmlTemplate, templateVariables);
        } catch (IOException e) {
            log.error("Load Template Error!", e);
            throw new MessagingException(templatePath, e);
        }
    }

    private String loadTemplate(String templatePath) throws IOException {
        try (InputStream inputStream = SalesEmailService.class.getClassLoader().getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                log.error("Template not found in classpath: {}", templatePath);
                throw new IOException("Template not found: " + templatePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Map<String, String> buildTemplateVariables(SalesPrepareDto salesData) {
        String brokerName = getBrokerFullName(salesData.getBroker());
        String productList = buildProductList(salesData);
        Map<String, String> variables = new HashMap<>();
        variables.put("{{BROKER_NAME}}", brokerName);
        variables.put("{{DOCUMENT_NUMBER}}", salesData.getSales().getDocumentNumber());
        variables.put("{{SALE_DATE}}", salesData.getSales().getCreatedDate().format(DATE_TIME_FORMATTER_3));
        variables.put("{{BROKER_ID}}", salesData.getSales().getBrokerId().toString());
        variables.put("{{PRODUCT_LIST}}", productList);
        variables.put("{{SUBTOTAL_PRICE}}", formatPrice(salesData.getSales().getSubtotalPrice()));
        variables.put("{{DISCOUNT_RATE}}", formatDiscountRate(salesData.getSales().getDiscountRate()));
        variables.put("{{DISCOUNT_PRICE}}", formatPrice(salesData.getSales().getDiscountPrice()));
        variables.put("{{TOTAL_PRICE}}", formatPrice(salesData.getSales().getTotalPrice()));
        variables.put("{{TOTAL_TAX_PRICE}}", formatPrice(salesData.getSales().getTotalTaxPrice()));
        variables.put("{{TOTAL_PRICE_WITH_TAX}}", formatPrice(salesData.getSales().getTotalPriceWithTax()));
        variables.put("{{COMPANY_NAME}}", DEFAULT_BRAND_NAME);

        return variables;
    }

    private String replacePlaceholders(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String buildProductList(SalesPrepareDto salesData) {
        return salesData.getSalesItems().stream()
                .map(item -> String.format("• %s (Adet: %d) - ₺%s",
                        item.getProductName(),
                        item.getProductCount(),
                        formatPrice(item.getTotalPriceWithTax())))
                .collect(Collectors.joining("<br>"));
    }

    private String createSellerSubject(SalesPrepareDto salesData) {
        return "Satış Gerçekleşti - Fatura #" + salesData.getSales().getDocumentNumber();
    }

    private String createBuyerSubject(SalesPrepareDto salesData) {
        return "Siparişiniz Onaylandı - Fatura #" + salesData.getSales().getDocumentNumber();
    }

    private String createSellerFileName(SalesPrepareDto salesData) {
        return "Satis_Faturasi_" + salesData.getSales().getDocumentNumber() + ".pdf";
    }

    private String createBuyerFileName(SalesPrepareDto salesData) {
        return "Fatura_" + salesData.getSales().getDocumentNumber() + ".pdf";
    }
}