package com.stockify.project.service;

import com.stockify.project.enums.DocumentType;
import com.stockify.project.exception.ImageException;
import com.stockify.project.exception.PdfException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.stockify.project.constant.DocumentConstants.PATH_DELIMITER;
import static com.stockify.project.util.LoginContext.getUsername;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageGetService {

    @Value("${user.dir}")
    private String basePath;

    @Value("${document.image-path:/images}")
    private String folderPath;

    public byte[] downloadImage(String imageName, DocumentType documentType) {
        try {
            String imagePath = folderPath + PATH_DELIMITER + getUsername() + PATH_DELIMITER + documentType.getLowerName();
            Path path = Paths.get(basePath + imagePath);
            if (!Files.exists(path)) {
                log.error("Image not found: {}", path);
                throw new PdfException();
            }
            Path filePath = path.resolve(imageName);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Error uploading image file: {}", imageName, e);
            throw new ImageException();
        }
    }
}
