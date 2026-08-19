package de.tuantu.playbacklog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient workCatalogRestClient(
            @Value("${workcatalog.api.url:NO_API}") String baseUrl) {

        if (baseUrl == null || baseUrl.isEmpty() || "NO_API".equals(baseUrl)) {
            return RestClient.builder().build();
        }

        return RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
