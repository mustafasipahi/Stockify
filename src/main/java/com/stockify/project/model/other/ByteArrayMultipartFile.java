package com.stockify.project.model.other;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Getter
@Setter
@AllArgsConstructor
public class ByteArrayMultipartFile implements MultipartFile {

    private final byte[] content;
    private final String name;
    private final String contentType;

    @Override
    public String getOriginalFilename() {
        return name;
    }

    @Override
    public boolean isEmpty() {
        return content == null || content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte @NotNull [] getBytes() {
        return content;
    }

    @Override
    public @NotNull InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(@NotNull File dest) throws IOException, IllegalStateException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }
}
