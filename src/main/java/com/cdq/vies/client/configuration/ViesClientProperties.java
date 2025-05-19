package com.cdq.vies.client.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ConfigurationProperties("com.cdq.vies")
public record ViesClientProperties(String baseUrl)
{

    @PostConstruct
    public void init()
    {
        log.info("VIES client initialized with baseUrl: {}", baseUrl);
    }

}
