package com.stockify.project.generator;

import java.security.SecureRandom;
import java.util.UUID;

import com.stockify.project.model.dto.UserSecurityDto;
import com.stockify.project.service.UserGetService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class UserInfoGenerator {

    private static final int DEFAULT_LENGTH = 8;
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    private static final SecureRandom random = new SecureRandom();
    private final UserGetService userGetService;

    public UserSecurityDto generate(String firstName, String lastName) {
        return UserSecurityDto.builder()
                .username(generateUsername(firstName, lastName))
                .password(generatePassword())
                .build();
    }

    private String generateUsername(String firstName, String lastName) {
        String cleanFirstName = firstName.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        String cleanLastName = lastName.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        String baseUsername = String.format("user_%s_%s", cleanFirstName, cleanLastName);
        String username = baseUsername;
        int attempt = 0;
        while (userGetService.findByUsername(username).isPresent() && attempt < 10) {
            String randomSuffix = String.format("%03d", random.nextInt(1000));
            username = baseUsername + "_" + randomSuffix;
            attempt++;
        }
        if (userGetService.findByUsername(username).isPresent()) {
            username = baseUsername + "_" + UUID.randomUUID().toString().substring(0, 5);
        }
        return username;
    }

    private String generatePassword() {
        String allowedChars = UPPERCASE + LOWERCASE + DIGITS;
        StringBuilder password = new StringBuilder(DEFAULT_LENGTH);
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            int index = random.nextInt(allowedChars.length());
            password.append(allowedChars.charAt(index));
        }
        return password.toString();
    }
}
