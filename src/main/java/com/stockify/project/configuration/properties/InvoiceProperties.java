package com.stockify.project.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "invoice")
public class InvoiceProperties {

    private String baseUrl;
    private String clientId;
    private String tenant;
}
