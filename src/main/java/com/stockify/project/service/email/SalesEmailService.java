package com.stockify.project.service.email;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.SalesPrepareDto;
import com.stockify.project.model.response.DocumentResponse;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.stockify.project.util.TenantContext.getEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class SalesEmailService {

    private static final String SELLER_TEMPLATE_PATH = "templates/sales_seller_email.html";
    private static final String BUYER_TEMPLATE_PATH = "templates/sales_buyer_email.html";
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String COMPANY_NAME = "Stokify";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendSalesNotifications(SalesPrepareDto salesPrepareDto, DocumentResponse documentResponse) {
        if (salesPrepareDto == null || documentResponse == null) {
            log.warn("Invalid Request - salesPrepareDto or documentResponse null");
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                sendNotificationsInternal(salesPrepareDto, documentResponse);
            } catch (Exception e) {
                log.error("CompletableFuture RunAsync Error!", e);
            }
        });
    }

    private void sendNotificationsInternal(SalesPrepareDto salesPrepareDto, DocumentResponse documentResponse) {
        BrokerDto broker = salesPrepareDto.getBroker();
        String userEmail = getEmail();
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
        String brokerEmail = broker.getEmail();
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
        String htmlContent = createEmailContent(SELLER_TEMPLATE_PATH, salesData);
        String fileName = createSellerFileName(salesData);
        sendEmailWithAttachment(sellerEmail, subject, htmlContent, file, fileName);
    }

    private void sendBuyerNotification(SalesPrepareDto salesData, String buyerEmail, MultipartFile file) throws MessagingException {
        String subject = createBuyerSubject(salesData);
        String htmlContent = createEmailContent(BUYER_TEMPLATE_PATH, salesData);
        String fileName = createBuyerFileName(salesData);
        sendEmailWithAttachment(buyerEmail, subject, htmlContent, file, fileName);
    }

    private void sendEmailWithAttachment(String to, String subject, String htmlContent,
                                         MultipartFile file, String fileName) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.addAttachment(fileName, file);
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Send Email Error! to: {} subject: {}", maskEmail(to), subject, e);
            throw new MessagingException(e.getMessage(), e);
        }
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
        ClassPathResource resource = new ClassPathResource(templatePath);
        if (!resource.exists()) {
            log.error("Template not found!");
        }
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private Map<String, String> buildTemplateVariables(SalesPrepareDto salesData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String brokerName = buildBrokerName(salesData.getBroker());
        String productList = buildProductList(salesData);

        Map<String, String> variables = new HashMap<>();
        variables.put("{{BROKER_NAME}}", brokerName);
        variables.put("{{DOCUMENT_NUMBER}}", salesData.getSales().getDocumentNumber());
        variables.put("{{SALE_DATE}}", salesData.getSales().getCreatedDate().format(formatter));
        variables.put("{{BROKER_ID}}", salesData.getSales().getBrokerId().toString());
        variables.put("{{PRODUCT_LIST}}", productList);
        variables.put("{{SUBTOTAL_PRICE}}", formatPrice(salesData.getSales().getSubtotalPrice()));
        variables.put("{{DISCOUNT_RATE}}", formatDiscountRate(salesData.getSales().getDiscountRate()));
        variables.put("{{DISCOUNT_PRICE}}", formatPrice(salesData.getSales().getDiscountPrice()));
        variables.put("{{TOTAL_PRICE}}", formatPrice(salesData.getSales().getTotalPrice()));
        variables.put("{{TOTAL_TAX_PRICE}}", formatPrice(salesData.getSales().getTotalTaxPrice()));
        variables.put("{{TOTAL_PRICE_WITH_TAX}}", formatPrice(salesData.getSales().getTotalPriceWithTax()));
        variables.put("{{COMPANY_NAME}}", COMPANY_NAME);

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

    private String buildBrokerName(BrokerDto broker) {
        return String.join(" ",
                StringUtils.defaultString(broker.getFirstName(), ""),
                StringUtils.defaultString(broker.getLastName(), "")
        ).trim();
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

    private String formatPrice(java.math.BigDecimal price) {
        return price != null ? String.format("%,.2f", price) : "0,00";
    }

    private String formatDiscountRate(java.math.BigDecimal rate) {
        return rate != null ? String.format("%.1f", rate) : "0,0";
    }

    private boolean isValidEmail(String email) {
        return StringUtils.isNotBlank(email) && email.contains("@") && email.contains(".");
    }

    private String maskEmail(String email) {
        if (StringUtils.isBlank(email) || !email.contains("@")) {
            return "***";
        }
        String[] parts = email.split("@");
        String username = parts[0];
        String domain = parts[1];

        if (username.length() <= 2) {
            return "***@" + domain;
        }
        return username.substring(0, 2) + "***@" + domain;
    }
}