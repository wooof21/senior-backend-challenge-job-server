package com.exmple.jobserver.adapters.external.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Data
@Configuration
@ConfigurationProperties(prefix = "external.service.job")
public class ExternalServiceProps {

    private String baseUrl;
    private Timeout timeout;

    @Data
    public static class Timeout {
        private Duration connect;
        private Duration response;
        private Duration read;
        private Duration write;
    }
}
