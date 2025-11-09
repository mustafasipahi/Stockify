package com.project.envantra.service.pdf;

import com.project.envantra.enums.DocumentType;
import com.project.envantra.exception.PdfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.project.envantra.constant.DocumentConstants.PATH_DELIMITER;
import static com.project.envantra.util.LoginContext.getUsername;

@Slf4j
@Service
public class PdfGetService {

    @Value("${user.dir}")
    private String basePath;

    @Value("${document.pdf-path:/pdfs}")
    private String folderPath;

    public byte[] downloadPdf(String fileName, DocumentType documentType) {
        try {
            String pdfPath = folderPath + PATH_DELIMITER + getUsername() + PATH_DELIMITER + documentType.getLowerName();
            Path path = Paths.get(basePath + pdfPath);
            if (!Files.exists(path)) {
                log.error("PDF file not found: {}", path);
                throw new PdfException();
            }
            Path filePath = path.resolve(fileName);
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("Error reading PDF file: {}", fileName, e);
            throw new PdfException();
        }
    }
}