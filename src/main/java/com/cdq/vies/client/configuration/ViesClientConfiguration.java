package com.cdq.vies.client.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ViesClientProperties.class)
public class ViesClientConfiguration
{

    private final ViesClientProperties viesClientProperties;

    @Bean
    WebClient viesClient(WebClient.Builder builder)
    {
        return builder
                .baseUrl(viesClientProperties.baseUrl())
                .build();
    }

}
