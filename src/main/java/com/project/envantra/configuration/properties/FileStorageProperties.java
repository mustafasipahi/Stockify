package com.project.envantra.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "envantra.files.storage")
public class FileStorageProperties {

    private List<String> allowedTypes;
    private String maxFileSize;
}
