package com.interviewlab.learning.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Define la infraestructura que la aplicación necesita antes de publicar mensajes.
 * Spring Kafka usa AdminClient al arrancar y crea estos topics si aún no existen.
 */
@Configuration
public class KafkaTopics {

    // Un topic es un log append-only. Tres particiones dejan observar paralelismo y orden por key.
    @Bean
    NewTopic textTopic(@Value("${app.kafka.topics.text}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    // Separar texto y JSON evita el schema mismatch: un consumer String no debe leer objetos JSON.
    @Bean
    NewTopic usersTopic(@Value("${app.kafka.topics.users}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }

    @Bean
    NewTopic keyedEventsTopic(@Value("${app.kafka.topics.keyed-events}") String name) {
        return TopicBuilder.name(name).partitions(3).replicas(1).build();
    }
}
