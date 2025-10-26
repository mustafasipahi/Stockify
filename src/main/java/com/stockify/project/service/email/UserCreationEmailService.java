package com.stockify.project.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void sendUserCreationNotification(String username, String password,
                                             String brokerName, String userFirstname) {
        if (username == null || password == null || brokerName == null || userFirstname == null) {
            log.warn("Invalid Request - one or more parameters are null");
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                sendUserCreationEmail(username, password, brokerName, userFirstname);
                log.info("User creation notification sent successfully for username: {}", username);
            } catch (Exception e) {
                log.error("User Creation Email CompletableFuture RunAsync Error!", e);
            }
        });
    }

    private void sendUserCreationEmail(String username, String password,
                                       String brokerName, String userFirstname) {
        try {
            String subject = createUserCreationSubject(userFirstname);
            String htmlContent = createUserCreationEmailContent(username, password, brokerName, userFirstname);
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

    private String createUserCreationEmailContent(String username, String password,
                                                  String brokerName, String userFirstname) throws MessagingException {
        try {
            String htmlTemplate = loadTemplate(USER_CREATION_TEMPLATE_PATH);
            Map<String, String> templateVariables = buildUserCreationVariables(
                    username, password, brokerName, userFirstname
            );
            return replacePlaceholders(htmlTemplate, templateVariables);
        } catch (IOException e) {
            log.error("Load Template Error!", e);
            throw new MessagingException(USER_CREATION_TEMPLATE_PATH, e);
        }
    }

    private Map<String, String> buildUserCreationVariables(String username, String password,
                                                           String brokerName, String userFirstname) {
        Map<String, String> variables = new HashMap<>();
        variables.put("{{USERNAME}}", username);
        variables.put("{{PASSWORD}}", password);
        variables.put("{{BROKER_NAME}}", brokerName);
        variables.put("{{USER_FIRSTNAME}}", userFirstname);
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

    private String createUserCreationSubject(String userFirstname) {
        return "Yeni Kullanıcı Oluşturuldu - " + userFirstname;
    }
}
