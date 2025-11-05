package com.stockify.project.service.pdf;

import com.stockify.project.exception.PdfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.stockify.project.util.TenantContext.getUsername;

@Slf4j
@Service
public class PdfGetService {

    @Value("${user.dir}")
    private String basePath;

    @Value("${pdf.folder-path:/pdfs}")
    private String folderPath;

    public byte[] downloadPdf(String objectName) {
        try {
            String pathFolder = getUsername();
            Path filePath = Paths.get(basePath + folderPath, pathFolder, objectName);
            if (!Files.exists(filePath)) {
                log.error("PDF file not found: {}", filePath);
                throw new PdfException();
            }
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Error reading PDF file: {}", objectName, e);
            throw new PdfException();
        }
    }
}