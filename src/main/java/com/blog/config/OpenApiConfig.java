package com.blog.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Blog API",
                version = "v1",
                description = "REST APIs for managing articles and authors",
                contact = @Contact(name = "Anais Support", email = "anais@gmail.com")
        )
)
public class OpenApiConfig {
}

