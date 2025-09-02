package com.stockify.project.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "stokify.files.storage")
public class FileStorageProperties {

    private String type;
    private List<String> allowedTypes;
    private String maxFileSize;
}
