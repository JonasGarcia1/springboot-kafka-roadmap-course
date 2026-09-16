package com.interviewlab.learning.kafka;

import com.interviewlab.learning.model.KeyedEvent;
import com.interviewlab.learning.model.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Tres consumidores para comparar payloads y consumer groups.
 * Spring mantiene un hilo de polling y llama al método adecuado al deserializar cada registro.
 */
@Service
public class LearningConsumers {
    private static final Logger log = LoggerFactory.getLogger(LearningConsumers.class);
    // Cada grupo conserva su offset: otro grupo puede releer el mismo log independientemente.
    @KafkaListener(topics = "${app.kafka.topics.text}", containerFactory = "textListenerFactory")
    public void onText(String message) {
        log.info("Consumer texto recibió: {}", message);
    }

    // El listener recibe directamente UserEvent porque userListenerFactory configuró JsonDeserializer.
    @KafkaListener(topics = "${app.kafka.topics.users}", containerFactory = "userListenerFactory")
    public void onUser(UserEvent user) {
        log.info("Consumer JSON recibió usuario id={}", user.id());
    }

    // Compará estas trazas con el mensaje enviado: key igual implica partición y orden relativos iguales.
    @KafkaListener(topics = "${app.kafka.topics.keyed-events}", containerFactory = "keyedListenerFactory")
    public void onKeyed(KeyedEvent event) {
        log.info("Consumer keyed recibió key={} payload={}", event.key(), event.payload());
    }
}
