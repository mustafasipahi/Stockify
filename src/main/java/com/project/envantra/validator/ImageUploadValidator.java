package com.project.envantra.validator;

import com.project.envantra.exception.DocumentRequiredException;
import com.project.envantra.exception.InvalidImageTypeException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUploadValidator {

    private static final List<String> ALLOWED_IMAGE_TYPES = List.of("image/png", "image/jpeg");

    public static void validateImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DocumentRequiredException();
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new InvalidImageTypeException();
        }
    }
}
