package com.project.envantra.service.email;

import com.project.envantra.model.request.UserCreationEmailRequest;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static com.project.envantra.constant.DocumentConstants.DEFAULT_BRAND_NAME;
import static com.project.envantra.constant.TemplateUtil.USER_CREATION_TEMPLATE;
import static com.project.envantra.util.NameUtil.getBrokerFullName;
import static com.project.envantra.util.NameUtil.getUserFullName;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCreationEmailService {

    private final EmailService emailService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendUserCreationNotification(UserCreationEmailRequest request) {
        log.info("Sending user creation notification creator user: {} request: {}", getUserFullName(request), request);
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
        emailService.sendEmail(to, subject, htmlContent);
    }

    private String createUserCreationEmailContent(UserCreationEmailRequest request) throws MessagingException {
        try {
            String htmlTemplate = loadTemplate();
            Map<String, String> templateVariables = buildUserCreationVariables(request);
            return replacePlaceholders(htmlTemplate, templateVariables);
        } catch (IOException e) {
            log.error("Load Template Error!", e);
            throw new MessagingException(USER_CREATION_TEMPLATE, e);
        }
    }

    private Map<String, String> buildUserCreationVariables(UserCreationEmailRequest request) {
        Map<String, String> variables = new HashMap<>();
        variables.put("{{BROKER_USERNAME}}", request.getBrokerUsername());
        variables.put("{{BROKER_PASSWORD}}", request.getBrokerPassword());
        variables.put("{{CREATOR_USER_FULL_NAME}}", getUserFullName(request));
        variables.put("{{BROKER_FULL_NAME}}", getBrokerFullName(request));
        variables.put("{{COMPANY_NAME}}", DEFAULT_BRAND_NAME);
        return variables;
    }

    private String loadTemplate() throws IOException {
        ClassPathResource resource = new ClassPathResource(USER_CREATION_TEMPLATE);
        if (!resource.exists()) {
            log.error("Template not found: {}", USER_CREATION_TEMPLATE);
            throw new IOException("Template not found: " + USER_CREATION_TEMPLATE);
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
