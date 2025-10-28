package com.stockify.project.util;

import java.security.SecureRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfoGenerator {

    private static final int DEFAULT_LENGTH = 8;
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";

    private static final SecureRandom random = new SecureRandom();

    public static String generateUsername(String firstName, String lastName) {
        String cleanFirstName = firstName.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        String cleanLastName = lastName.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        return String.format("user_%s_%s", cleanFirstName, cleanLastName);
    }

    public static String generatePassword() {
        String allowedChars = UPPERCASE + LOWERCASE + DIGITS;
        StringBuilder password = new StringBuilder(DEFAULT_LENGTH);
        for (int i = 0; i < DEFAULT_LENGTH; i++) {
            int index = random.nextInt(allowedChars.length());
            password.append(allowedChars.charAt(index));
        }
        return password.toString();
    }
}
