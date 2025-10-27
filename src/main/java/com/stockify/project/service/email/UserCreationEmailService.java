package com.stockify.project.service.email;

import com.stockify.project.model.request.UserCreationEmailRequest;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreationEmailService {

    private static final String USER_CREATION_TEMPLATE_PATH = "templates/user_creation_email.html";
    private static final String COMPANY_NAME = "Stokify";

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendUserCreationNotification(UserCreationEmailRequest request) {
        if (StringUtils.isBlank(request.getBrokerUsername()) || StringUtils.isBlank(request.getBrokerPassword())
                || StringUtils.isBlank(request.getBrokerFirstName()) || StringUtils.isBlank(request.getBrokerLastName())
                || StringUtils.isBlank(request.getCreatorUserFirstName()) || StringUtils.isBlank(request.getCreatorUserLastName())) {
            log.warn("Invalid Request - one or more parameters are null or blank");
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                sendUserCreationEmail(request);
                log.info("User creation notification sent successfully for username: {}", request.getBrokerUsername());
            } catch (Exception e) {
                log.error("User Creation Email CompletableFuture RunAsync Error!", e);
            }
        });
    }

    private void sendUserCreationEmail(UserCreationEmailRequest request) {
        try {
            String subject = createUserCreationSubject(request.getBrokerUsername());
            String htmlContent = createUserCreationEmailContent(request);
            sendSimpleEmail(fromEmail, subject, htmlContent);
            log.info("Sent User Creation notification email from {} to {}", fromEmail, fromEmail);
        } catch (Exception e) {
            log.error("Send User Creation Notification Error!", e);
        }
    }

    private void sendSimpleEmail(String to, String subject, String htmlContent) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Send Email Error! to: {} subject: {}", to, subject, e);
            throw new MessagingException(e.getMessage(), e);
        }
    }

    private String createUserCreationEmailContent(UserCreationEmailRequest request) throws MessagingException {
        try {
            String htmlTemplate = loadTemplate(USER_CREATION_TEMPLATE_PATH);
            Map<String, String> templateVariables = buildUserCreationVariables(request);
            return replacePlaceholders(htmlTemplate, templateVariables);
        } catch (IOException e) {
            log.error("Load Template Error!", e);
            throw new MessagingException(USER_CREATION_TEMPLATE_PATH, e);
        }
    }

    private Map<String, String> buildUserCreationVariables(UserCreationEmailRequest request) {
        Map<String, String> variables = new HashMap<>();
        variables.put("{{BROKER_USERNAME}}", request.getBrokerUsername());
        variables.put("{{BROKER_PASSWORD}}", request.getBrokerPassword());
        variables.put("{{CREATOR_USER_FULL_NAME}}", request.getCreatorUserFirstName() + " " + request.getCreatorUserLastName());
        variables.put("{{BROKER_FULL_NAME}}", request.getBrokerFirstName() + " " + request.getBrokerLastName());
        variables.put("{{COMPANY_NAME}}", COMPANY_NAME);
        return variables;
    }

    private String loadTemplate(String templatePath) throws IOException {
        ClassPathResource resource = new ClassPathResource(templatePath);
        if (!resource.exists()) {
            log.error("Template not found: {}", templatePath);
            throw new IOException("Template not found: " + templatePath);
        }
        return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }

    private String replacePlaceholders(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private String createUserCreationSubject(String brokerUsername) {
        return "Yeni Kullanıcı Oluşturuldu - " + brokerUsername;
    }
}
