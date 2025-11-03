package com.stockify.project.configuration;

import com.stockify.project.configuration.properties.SupabaseProperties;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@AllArgsConstructor
public class SupabaseConfig {

    private final SupabaseProperties supabaseProperties;

    @Bean
    public WebClient supabaseWebClient() {
        String serviceRoleKey = supabaseProperties.getServiceRoleKey();
        String anonKey = supabaseProperties.getAnonKey();
        String token = StringUtils.isNotBlank(serviceRoleKey) ? serviceRoleKey : anonKey;
        return WebClient.builder()
                .baseUrl(supabaseProperties.getUrl())
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchangeStrategies(getStrategies())
                .build();
    }

    private ExchangeStrategies getStrategies() {
        return ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }
}



