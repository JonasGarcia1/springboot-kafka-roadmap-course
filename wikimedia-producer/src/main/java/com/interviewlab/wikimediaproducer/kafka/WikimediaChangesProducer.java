package com.interviewlab.wikimediaproducer.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/** Publica cambios Wikimedia sin acoplar el adaptador SSE a los detalles del cliente Kafka. */
@Service
public class WikimediaChangesProducer {
    private final KafkaTemplate<String, String> template;
    private final String topic;

    public WikimediaChangesProducer(KafkaTemplate<String, String> template,
                                    @Value("${app.topic}") String topic) {
        this.template = template;
        this.topic = topic;
    }

    /**
     * Kafka desacopla la fuente de datos de la base: si MySQL se demora, el broker conserva el
     * evento. La key permite conservar orden relativo si se elige una identidad estable.
     */
    public void send(String key, String payload) {
        template.send(topic, key, payload);
    }
}
