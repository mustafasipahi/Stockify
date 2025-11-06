package com.stockify.project.service.pdf;

import com.stockify.project.enums.DocumentType;
import com.stockify.project.exception.PdfException;
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
import static com.stockify.project.util.TenantContext.getUsername;

@Slf4j
@Service
public class PdfPostService {

    @Value("${user.dir}")
    private String basePath;

    @Value("${pdf.folder-path:/pdfs}")
    private String folderPath;

    public String uploadPdf(MultipartFile file, String fileName, DocumentType documentType) {
        try {
            String documentPath = folderPath + PATH_DELIMITER + getUsername() + PATH_DELIMITER + documentType.getLowerName();
            Path path = Paths.get(basePath + documentPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return documentPath;
        } catch (IOException e) {
            log.error("Error uploading PDF file: {}", fileName, e);
            throw new PdfException();
        }
    }
}