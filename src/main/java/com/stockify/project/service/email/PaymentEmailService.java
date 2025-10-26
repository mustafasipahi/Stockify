package com.stockify.project.service.email;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.PaymentDto;
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

import static com.stockify.project.util.TenantContext.getEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEmailService {

    private static final String RECEIVER_TEMPLATE_PATH = "templates/payment_receiver_email.html";
    private static final String PAYER_TEMPLATE_PATH = "templates/payment_payer_email.html";
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String COMPANY_NAME = "Stokify";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPaymentNotifications(PaymentDto paymentDto, DocumentResponse documentResponse) {
        if (paymentDto == null || documentResponse == null) {
            log.warn("Invalid Request - paymentDto or documentResponse null");
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                sendNotificationsInternal(paymentDto, documentResponse);
            } catch (Exception e) {
                log.error("Payment CompletableFuture RunAsync Error!", e);
            }
        });
    }

    private void sendNotificationsInternal(PaymentDto paymentDto, DocumentResponse documentResponse) {
        BrokerDto broker = paymentDto.getBroker();
        String userEmail = getEmail();
        if (isValidEmail(userEmail)) {
            try {
                sendReceiverNotification(paymentDto, userEmail, documentResponse.getFile());
                log.info("Sent ReceiverNotification email to {}", maskEmail(userEmail));
            } catch (Exception e) {
                log.error("Send ReceiverNotification Error! Email: {}", maskEmail(userEmail), e);
            }
        } else {
            log.info("Company Email is invalid");
        }
        String brokerEmail = broker.getEmail();
        if (isValidEmail(brokerEmail)) {
            try {
                sendPayerNotification(paymentDto, brokerEmail, documentResponse.getFile());
                log.info("Sent PayerNotification email to {}", maskEmail(brokerEmail));
            } catch (Exception e) {
                log.error("Send PayerNotification Error! Email: {}", maskEmail(brokerEmail), e);
            }
        } else {
            log.error("Broker Email is invalid");
        }
    }

    private void sendReceiverNotification(PaymentDto paymentDto, String receiverEmail, MultipartFile file) throws MessagingException {
        String subject = createReceiverSubject(paymentDto);
        String htmlContent = createEmailContent(RECEIVER_TEMPLATE_PATH, paymentDto);
        String fileName = createReceiverFileName(paymentDto);
        sendEmailWithAttachment(receiverEmail, subject, htmlContent, file, fileName);
    }

    private void sendPayerNotification(PaymentDto paymentDto, String payerEmail, MultipartFile file) throws MessagingException {
        String subject = createPayerSubject(paymentDto);
        String htmlContent = createEmailContent(PAYER_TEMPLATE_PATH, paymentDto);
        String fileName = createPayerFileName(paymentDto);
        sendEmailWithAttachment(payerEmail, subject, htmlContent, file, fileName);
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

    private String createEmailContent(String templatePath, PaymentDto paymentDto) throws MessagingException {
        try {
            String htmlTemplate = loadTemplate(templatePath);
            Map<String, String> templateVariables = buildTemplateVariables(paymentDto);
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

    private Map<String, String> buildTemplateVariables(PaymentDto paymentDto) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
        String brokerName = buildBrokerName(paymentDto.getBroker());

        Map<String, String> variables = new HashMap<>();
        variables.put("{{BROKER_NAME}}", brokerName);
        variables.put("{{DOCUMENT_NUMBER}}", paymentDto.getDocumentNumber());
        variables.put("{{PAYMENT_DATE}}", paymentDto.getCreatedDate().format(formatter));
        variables.put("{{BROKER_ID}}", paymentDto.getBroker().getBrokerId().toString());
        variables.put("{{PAYMENT_AMOUNT}}", formatPrice(paymentDto.getPrice()));
        variables.put("{{PAYMENT_METHOD}}", paymentDto.getType().getName());
        variables.put("{{DESCRIPTION}}", StringUtils.defaultString(null, "Açıklama bulunmuyor"));
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

    private String buildBrokerName(BrokerDto broker) {
        return String.join(" ",
                StringUtils.defaultString(broker.getFirstName(), ""),
                StringUtils.defaultString(broker.getLastName(), "")
        ).trim();
    }

    private String createReceiverSubject(PaymentDto paymentDto) {
        return "Ödeme Alındı - Makbuz #" + paymentDto.getDocumentNumber();
    }

    private String createPayerSubject(PaymentDto paymentDto) {
        return "Ödemeniz Onaylandı - Makbuz #" + paymentDto.getDocumentNumber();
    }

    private String createReceiverFileName(PaymentDto paymentDto) {
        return "Odeme_Makbuzu_" + paymentDto.getDocumentNumber() + ".pdf";
    }

    private String createPayerFileName(PaymentDto paymentDto) {
        return "Makbuz_" + paymentDto.getDocumentNumber() + ".pdf";
    }

    private String formatPrice(java.math.BigDecimal price) {
        return price != null ? String.format("%,.2f", price) : "0,00";
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