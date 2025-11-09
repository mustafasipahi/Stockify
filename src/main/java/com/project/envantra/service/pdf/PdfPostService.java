package com.project.envantra.service.pdf;

import com.project.envantra.enums.DocumentType;
import com.project.envantra.exception.PdfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static com.project.envantra.constant.DocumentConstants.PATH_DELIMITER;
import static com.project.envantra.util.LoginContext.getUsername;

@Slf4j
@Service
public class PdfPostService {

    @Value("${user.dir}")
    private String basePath;

    @Value("${document.pdf-path:/pdfs}")
    private String folderPath;

    public String uploadPdf(MultipartFile file, String pdfName, DocumentType documentType) {
        try {
            String pdfPath = folderPath + PATH_DELIMITER + getUsername() + PATH_DELIMITER + documentType.getLowerName();
            Path path = Paths.get(basePath + pdfPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(pdfName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return pdfPath;
        } catch (IOException e) {
            log.error("Error uploading pdf file: {}", pdfName, e);
            throw new PdfException();
        }
    }
}