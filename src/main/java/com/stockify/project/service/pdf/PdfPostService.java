package com.stockify.project.service.pdf;

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
import java.util.Map;

import static com.stockify.project.util.TenantContext.getUsername;

@Slf4j
@Service
public class PdfPostService {

    @Value("${user.dir}")
    private String basePath;

    @Value("${pdf.folder-path:/pdfs}")
    private String folderPath;

    public Map<String, String> uploadPdf(MultipartFile file, String fileName) {
        try {
            String pathFolder = getUsername();
            Path path = Paths.get(basePath + folderPath, pathFolder);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            Path filePath = path.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            String relativePath = pathFolder + "/" + fileName;
            return Map.of(
                    "tenant", pathFolder,
                    "objectName", fileName,
                    "path", relativePath,
                    "fullPath", filePath.toString()
            );
        } catch (IOException e) {
            log.error("Error uploading PDF file: {}", fileName, e);
            throw new PdfException();
        }
    }
}