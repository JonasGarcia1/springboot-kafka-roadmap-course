package com.interviewlab.wikimediaconsumer.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Este Swagger vive en el consumidor, no en learning-api: una API documenta su propio contrato.
 * Es una forma concreta de ver que el productor y el consumidor son aplicaciones independientes.
 */
@Configuration
public class WikimediaOpenApiConfiguration {

    @Bean
    OpenAPI wikimediaConsumerOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Wikimedia consumer — verificación MySQL")
                .version("1.0")
                .description("Consultá la proyección almacenada por el @KafkaListener."));
    }
}
