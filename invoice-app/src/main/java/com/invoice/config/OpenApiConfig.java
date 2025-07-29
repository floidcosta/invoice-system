package com.invoice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server localServer = new Server()
            .url("http://localhost:8080") // Local dev base URL
            .description("Local development server");

        Server dockerSwaggerServer = new Server()
            .url("http://localhost:8029") // If using external Swagger container
            .description("Swagger container proxy");

        return new OpenAPI()
            .info(new Info()
                .title("Invoice System API")
                .version("1.0.0")
                .description("REST API documentation for the Invoice Management System.")
                .termsOfService("https://example.com/terms")
                .contact(new Contact()
                    .name("Floid Costa")
                    .email("support@example.com")
                    .url("https://example.com"))
                .license(new License()
                    .name("Apache 2.0")
                    .url("http://springdoc.org")))
            .servers(List.of(localServer, dockerSwaggerServer));
    }
}
