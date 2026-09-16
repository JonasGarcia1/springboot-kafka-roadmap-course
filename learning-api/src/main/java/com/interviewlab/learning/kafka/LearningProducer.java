package com.interviewlab.learning.kafka;

import com.interviewlab.learning.model.KeyedEvent;
import com.interviewlab.learning.model.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Puerto de salida de la API REST hacia Kafka.
 * El controller no usa KafkaTemplate directamente: esta clase concentra el nombre del topic,
 * la key y el tipo de payload para que la capa web no conozca detalles del broker.
 */
@Service
public class LearningProducer {
    private static final Logger log = LoggerFactory.getLogger(LearningProducer.class);
    private final KafkaTemplate<String, String> textTemplate;
    private final KafkaTemplate<String, Object> jsonTemplate;
    private final String textTopic;
    private final String usersTopic;
    private final String keyedTopic;

    public LearningProducer(KafkaTemplate<String, String> textKafkaTemplate,
                            KafkaTemplate<String, Object> jsonKafkaTemplate,
                            @Value("${app.kafka.topics.text}") String textTopic,
                            @Value("${app.kafka.topics.users}") String usersTopic,
                            @Value("${app.kafka.topics.keyed-events}") String keyedTopic) {
        this.textTemplate = textKafkaTemplate;
        this.jsonTemplate = jsonKafkaTemplate;
        this.textTopic = textTopic;
        this.usersTopic = usersTopic;
        this.keyedTopic = keyedTopic;
    }

    /** Mensaje básico: sin key Kafka reparte registros entre particiones según su particionador. */
    public void sendText(String message) {
        textTemplate.send(textTopic, message);
        log.info("Texto enviado al topic {}", textTopic);
    }

    /**
     * El id se usa como key de negocio. Todos los eventos del mismo usuario llegan a una misma
     * partición, y por eso conservan orden relativo para ese usuario.
     */
    public void sendUser(UserEvent user) {
        jsonTemplate.send(usersTopic, user.id(), user);
        log.info("Usuario {} enviado con key {}", user.name(), user.id());
    }

    /** Ejercicio visible de particionamiento: enviá varias veces la misma key desde REST. */
    public void sendKeyed(KeyedEvent event) {
        jsonTemplate.send(keyedTopic, event.key(), event);
        log.info("Evento enviado con key {}; Kafka elegirá siempre la misma partición para esa key", event.key());
    }
}
