package com.interviewlab.advanced.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import java.util.Map;

/** Configura los topics y el productor transaccional de los ejercicios avanzados. */
@Configuration
public class AdvancedKafkaConfig {

    @Bean
    NewTopic idempotentTopic() { return TopicBuilder.name("labs.idempotent.v1").partitions(3).replicas(1).build(); }

    @Bean
    NewTopic transactionInputTopic() { return TopicBuilder.name("labs.transaction.input.v1").partitions(1).replicas(1).build(); }

    @Bean
    NewTopic transactionAuditTopic() { return TopicBuilder.name("labs.transaction.audit.v1").partitions(1).replicas(1).build(); }

    /** cleanup.policy=compact conserva la última versión por key, no cada cambio histórico. */
    @Bean
    NewTopic compactedTopic() { return TopicBuilder.name("labs.state.v1").partitions(1).replicas(1).config("cleanup.policy", "compact").build(); }

    @Bean
    NewTopic streamInputTopic() { return TopicBuilder.name("labs.stream.input.v1").partitions(1).replicas(1).build(); }

    @Bean
    NewTopic streamOutputTopic() { return TopicBuilder.name("labs.stream.output.v1").partitions(1).replicas(1).build(); }

    /**
     * Idempotencia evita duplicados causados por reintentos del producer. El prefijo transaccional
     * identifica a esta instancia frente al coordinador de transacciones.
     */
    @Bean
    ProducerFactory<String, String> transactionalProducerFactory(
            @Value("${app.kafka.bootstrap-servers}") String bootstrap) {
        var factory = new DefaultKafkaProducerFactory<String, String>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.ACKS_CONFIG, "all",
                ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true
        ));
        factory.setTransactionIdPrefix("roadmap-course-tx-");
        return factory;
    }

    @Bean
    KafkaTemplate<String, String> transactionalKafkaTemplate(
            ProducerFactory<String, String> transactionalProducerFactory) {
        return new KafkaTemplate<>(transactionalProducerFactory);
    }
}
