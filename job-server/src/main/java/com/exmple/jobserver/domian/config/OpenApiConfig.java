package com.exmple.jobserver.domian.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Job Server",
                version = "1.0",
                description = "Async Job Processing Service"
        )
)
public class OpenApiConfig {
}
