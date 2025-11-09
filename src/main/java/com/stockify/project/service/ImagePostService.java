package com.stockify.project.service;

import com.stockify.project.enums.DocumentType;
import com.stockify.project.exception.ImageException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.stockify.project.constant.DocumentConstants.PATH_DELIMITER;
import static com.stockify.project.util.LoginContext.getUsername;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImagePostService {

    @Value("${user.dir}")
    private String basePath;

    @Value("${document.image-path:/images}")
    private String folderPath;

    public String uploadImages(MultipartFile file, String imageName, DocumentType documentType) {
        try {
            String imagePath = folderPath + PATH_DELIMITER + getUsername() + PATH_DELIMITER + documentType.getLowerName();
            Path path = Paths.get(basePath + imagePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(imageName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return imagePath;
        } catch (IOException e) {
            log.error("Error uploading image file: {}", imageName, e);
            throw new ImageException();
        }
    }
}
