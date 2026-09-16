package com.interviewlab.learning.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Describe la API para Swagger UI. Swagger no sustituye el curso: es el lugar para ejecutar los
 * endpoints después de entender el flujo en la plataforma principal.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    OpenAPI learningLabOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Spring Boot + Kafka — Laboratorio guiado")
                .version("1.0")
                .description("Probá los endpoints del curso. Abrí primero la plataforma en / para entender qué observar."));
    }
}
