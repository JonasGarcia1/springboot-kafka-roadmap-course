package com.interviewlab.learning.config;

import com.interviewlab.learning.model.KeyedEvent;
import com.interviewlab.learning.model.UserEvent;
import com.interviewlab.learning.kafka.LearningRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuración explícita de los clientes Kafka usados por la API de learning.
 *
 * <p>Spring Boot puede autoconfigurar una gran parte de estos objetos. Aquí se los declara a
 * propósito para que se pueda ver la relación completa: propiedades de bajo nivel -&gt;
 * ProducerFactory/ConsumerFactory -&gt; KafkaTemplate o @KafkaListener.</p>
 */
@Configuration
public class KafkaClientConfig {

    private final String bootstrapServers;

    public KafkaClientConfig(@Value("${app.kafka.bootstrap-servers}") String bootstrapServers) {
        // bootstrap server es solo el punto inicial: el cliente obtiene del broker los metadatos del cluster.
        this.bootstrapServers = bootstrapServers;
    }

    /** Template tipado para texto plano; delega la conexión y el envío asíncrono al cliente Kafka. */
    @Bean
    KafkaTemplate<String, String> textKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(stringProducerProperties()));
    }

    /** Template separado para objetos: su value serializer transforma records Java en JSON. */
    @Bean
    KafkaTemplate<String, Object> jsonKafkaTemplate() {
        return new KafkaTemplate<>(new DefaultKafkaProducerFactory<>(jsonProducerProperties()));
    }

    private Map<String, Object> stringProducerProperties() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                // acks=all espera confirmación de las réplicas ISR; con un broker local equivale al líder.
                // define cuántas réplicas deben recibir el mensaje antes de que Kafka le responda al Producer diciendo "Listo, recibí el mensaje de forma segura"
                ProducerConfig.ACKS_CONFIG, "all"
        );
    }

    private Map<String, Object> jsonProducerProperties() {
        return Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class,
                ProducerConfig.ACKS_CONFIG, "all"
        );
    }

    /**
     * Contenedor que convierte el ciclo de polling del cliente Kafka en un método @KafkaListener.
     * El group id identifica el progreso compartido por esta familia de consumidores.
     */
    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> textListenerFactory(
            LearningRebalanceListener rebalanceListener) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(textConsumerProperties()));
        factory.getContainerProperties().setConsumerRebalanceListener(rebalanceListener);
        return factory;
    }

    private Map<String, Object> textConsumerProperties() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.GROUP_ID_CONFIG, "learning-text-group",
                // Si el grupo no tiene offset aún, earliest permite reproducir el log desde el inicio.
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, UserEvent> userListenerFactory(
            LearningRebalanceListener rebalanceListener) {
        return jsonFactory(UserEvent.class, "learning-user-group", rebalanceListener);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, KeyedEvent> keyedListenerFactory(
            LearningRebalanceListener rebalanceListener) {
        return jsonFactory(KeyedEvent.class, "learning-keyed-group", rebalanceListener);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> jsonFactory(
            Class<T> type, String group, LearningRebalanceListener rebalanceListener) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        // Nunca confiar en cualquier paquete: evita que JSON active clases arbitrarias al deserializar.
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.interviewlab.learning.model");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, type.getName());

        var factory = new ConcurrentKafkaListenerContainerFactory<String, T>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(props));
        if (rebalanceListener != null) {
            factory.getContainerProperties().setConsumerRebalanceListener(rebalanceListener);
        }
        return factory;
    }
}
