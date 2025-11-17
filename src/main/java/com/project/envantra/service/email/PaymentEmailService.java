package com.project.envantra.service.email;

import com.project.envantra.enums.EmailType;
import com.project.envantra.enums.RecipientType;
import com.project.envantra.model.dto.PaymentDto;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.response.DocumentResponse;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static com.project.envantra.constant.DocumentConstants.DATE_TIME_FORMATTER_3;
import static com.project.envantra.constant.DocumentConstants.DEFAULT_BRAND_NAME;
import static com.project.envantra.constant.TemplateConstant.*;
import static com.project.envantra.util.EmailUtil.*;
import static com.project.envantra.util.NameUtil.getBrokerFullName;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEmailService {

    private final EmailService emailService;

    @Async
    public void sendPaymentCreatedNotifications(PaymentDto paymentDto, DocumentResponse documentResponse) {
        if (!validateRequest(paymentDto, documentResponse)) {
            return;
        }
        String userEmail = paymentDto.getUser().getEmail();
        String brokerEmail = paymentDto.getBroker().getEmail();
        sendEmailToReceiver(
                userEmail,
                paymentDto,
                documentResponse.getFile(),
                PAYMENT_EMAIL_RECEIVER_TEMPLATE,
                EmailType.CREATED,
                Map.of());
        sendEmailToPayer(
                brokerEmail,
                paymentDto,
                documentResponse.getFile(),
                PAYMENT_EMAIL_PAYER_TEMPLATE,
                EmailType.CREATED,
                Map.of());
        log.info("Payment created notifications sent successfully..");
    }

    @Async
    public void sendPaymentUpdatedNotifications(PaymentDto newPaymentDto, DocumentResponse newDocumentResponse,
                                                PaymentEntity oldPayment) {
        if (!validateRequest(newPaymentDto, newDocumentResponse)) {
            return;
        }
        String userEmail = newPaymentDto.getUser().getEmail();
        String brokerEmail = newPaymentDto.getBroker().getEmail();
        Map<String, String> oldPaymentData = extractOldPaymentData(oldPayment);
        sendEmailToReceiver(
                userEmail,
                newPaymentDto,
                newDocumentResponse.getFile(),
                PAYMENT_EMAIL_RECEIVER_UPDATE_TEMPLATE,
                EmailType.UPDATED,
                oldPaymentData);
        sendEmailToPayer(
                brokerEmail,
                newPaymentDto,
                newDocumentResponse.getFile(),
                PAYMENT_EMAIL_PAYER_UPDATE_TEMPLATE,
                EmailType.UPDATED,
                oldPaymentData);
        log.info("Payment update notifications sent successfully..");
    }

    @Async
    public void sendPaymentCancelledNotifications(PaymentDto paymentDto) {
        if (paymentDto == null) {
            log.warn("Invalid Request - cancelledPayment is null");
            return;
        }
        String userEmail = paymentDto.getUser().getEmail();
        String brokerEmail = paymentDto.getBroker().getEmail();
        String cancelReason = StringUtils.defaultString(paymentDto.getCancelReason(), "Belirtilmemiş");
        Map<String, String> cancelData = Map.of("{{CANCEL_REASON}}", cancelReason);
        sendEmailToReceiver(
                userEmail,
                paymentDto,
                null,
                PAYMENT_EMAIL_RECEIVER_CANCEL_TEMPLATE,
                EmailType.CANCELLED,
                cancelData);
        sendEmailToPayer(
                brokerEmail,
                paymentDto,
                null,
                PAYMENT_EMAIL_PAYER_CANCEL_TEMPLATE,
                EmailType.CANCELLED,
                cancelData);
        log.info("Payment cancel notifications sent successfully..");
    }

    private void sendEmailToReceiver(String email, PaymentDto paymentDto, MultipartFile file,
                                     String templatePath, EmailType emailType, Map<String, String> extraData) {
        if (!isValidEmail(email)) {
            log.warn("Receiver email is invalid: {}", maskEmail(email));
            return;
        }
        try {
            String subject = createSubject(paymentDto, emailType, RecipientType.RECEIVER);
            String htmlContent = createEmailContent(templatePath, paymentDto, extraData);
            String fileName = createFileName(paymentDto, emailType, RecipientType.RECEIVER);
            if (file != null) {
                emailService.sendEmailWithAttachment(email, subject, htmlContent, file, fileName);
            } else {
                emailService.sendEmail(email, subject, htmlContent);
            }
            log.info("Email sent to receiver: {}", maskEmail(email));
        } catch (Exception e) {
            log.error("Failed to send email to receiver: {}", maskEmail(email), e);
        }
    }

    private void sendEmailToPayer(String email, PaymentDto paymentDto, MultipartFile file,
                                  String templatePath, EmailType emailType, Map<String, String> extraData) {
        if (!isValidEmail(email)) {
            log.warn("Payer email is invalid: {}", maskEmail(email));
            return;
        }
        try {
            String subject = createSubject(paymentDto, emailType, RecipientType.PAYER);
            String htmlContent = createEmailContent(templatePath, paymentDto, extraData);
            String fileName = createFileName(paymentDto, emailType, RecipientType.PAYER);
            if (file != null) {
                emailService.sendEmailWithAttachment(email, subject, htmlContent, file, fileName);
            } else {
                emailService.sendEmail(email, subject, htmlContent);
            }
            log.info("Email sent to payer: {}", maskEmail(email));
        } catch (Exception e) {
            log.error("Failed to send email to payer: {}", maskEmail(email), e);
        }
    }

    private String createEmailContent(String templatePath, PaymentDto paymentDto,
                                      Map<String, String> extraData) throws MessagingException {
        try {
            String htmlTemplate = loadTemplate(templatePath);
            Map<String, String> variables = buildTemplateVariables(paymentDto);
            variables.putAll(extraData);
            return replacePlaceholders(htmlTemplate, variables);
        } catch (IOException e) {
            log.error("Failed to load template: {}", templatePath, e);
            throw new MessagingException("Template loading failed", e);
        }
    }

    private String loadTemplate(String templatePath) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {
            if (inputStream == null) {
                throw new IOException("Template not found: " + templatePath);
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Map<String, String> buildTemplateVariables(PaymentDto paymentDto) {
        String brokerName = getBrokerFullName(paymentDto.getBroker());
        Map<String, String> variables = new HashMap<>();
        variables.put("{{BROKER_NAME}}", brokerName);
        variables.put("{{DOCUMENT_NUMBER}}", paymentDto.getDocumentNumber());
        variables.put("{{PAYMENT_DATE}}", paymentDto.getCreatedDate().format(DATE_TIME_FORMATTER_3));
        variables.put("{{BROKER_ID}}", paymentDto.getBroker().getBrokerId().toString());
        variables.put("{{PAYMENT_AMOUNT}}", formatPrice(paymentDto.getPrice()));
        variables.put("{{PAYMENT_METHOD}}", paymentDto.getType().getName());
        variables.put("{{DESCRIPTION}}", StringUtils.defaultString(null, "Açıklama bulunmuyor"));
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

    private String createSubject(PaymentDto paymentDto, EmailType emailType, RecipientType recipientType) {
        String documentNumber = paymentDto.getDocumentNumber();
        return switch (emailType) {
            case CREATED -> recipientType == RecipientType.RECEIVER
                    ? "Ödeme Alındı - Makbuz #" + documentNumber
                    : "Ödemeniz Onaylandı - Makbuz #" + documentNumber;
            case UPDATED -> recipientType == RecipientType.RECEIVER
                    ? "Ödeme Güncellendi - Makbuz #" + documentNumber
                    : "Ödemeniz Güncellendi - Makbuz #" + documentNumber;
            case CANCELLED -> recipientType == RecipientType.RECEIVER
                    ? "Ödeme İptal Edildi - Makbuz #" + documentNumber
                    : "Ödemeniz İptal Edildi - Makbuz #" + documentNumber;
        };
    }

    private String createFileName(PaymentDto paymentDto, EmailType emailType, RecipientType recipientType) {
        String documentNumber = paymentDto.getDocumentNumber();
        String prefix = recipientType == RecipientType.RECEIVER ? "Odeme_Makbuzu_" : "Makbuz_";
        return switch (emailType) {
            case CREATED -> prefix + documentNumber + ".pdf";
            case UPDATED -> prefix + documentNumber + "_Guncelleme.pdf";
            case CANCELLED -> prefix + documentNumber + "_Iptal.pdf";
        };
    }

    private boolean validateRequest(PaymentDto paymentDto, DocumentResponse documentResponse) {
        if (paymentDto == null || documentResponse == null) {
            log.warn("Invalid request - paymentDto or documentResponse is null");
            return false;
        }
        return true;
    }

    private Map<String, String> extractOldPaymentData(PaymentEntity oldPayment) {
        Map<String, String> oldData = new HashMap<>();
        oldData.put("{{OLD_DOCUMENT_NUMBER}}", String.valueOf(oldPayment.getId()));
        oldData.put("{{OLD_PAYMENT_AMOUNT}}", formatPrice(oldPayment.getPrice()));
        oldData.put("{{OLD_PAYMENT_METHOD}}", oldPayment.getType().getName());
        return oldData;
    }
}