package com.stockify.project.service.email;

import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.response.DocumentResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.stockify.project.util.EmailUtil.*;
import static com.stockify.project.util.NameUtil.getBrokerFullName;
import static com.stockify.project.util.LoginContext.getEmail;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEmailService {

    private static final String RECEIVER_TEMPLATE_PATH = "templates/payment_receiver_email.html";
    private static final String PAYER_TEMPLATE_PATH = "templates/payment_payer_email.html";
    private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String COMPANY_NAME = "Stokify";

    private final EmailService emailService;

    public void sendPaymentNotifications(PaymentDto paymentDto, DocumentResponse documentResponse) {
        if (paymentDto == null || documentResponse == null) {
            log.warn("Invalid Request - paymentDto or documentResponse null");
            return;
        }
        String userEmail = getEmail();
        String brokerEmail = paymentDto.getBroker().getEmail();
        CompletableFuture.runAsync(() -> {
            try {
                sendNotificationsInternal(paymentDto, documentResponse, userEmail, brokerEmail);
            } catch (Exception e) {
                log.error("Payment CompletableFuture RunAsync Error!", e);
            }
        });
    }

    private void sendNotificationsInternal(PaymentDto paymentDto, DocumentResponse documentResponse,
                                           String userEmail, String brokerEmail) {
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
        emailService.sendEmailWithAttachment(to, subject, htmlContent, file, fileName);
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
        String brokerName = getBrokerFullName(paymentDto.getBroker());

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
}