package com.interviewlab.wikimediaconsumer.kafka;

import com.interviewlab.wikimediaconsumer.persistence.WikimediaEvent;
import com.interviewlab.wikimediaconsumer.persistence.WikimediaEventRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/** Consume el topic Wikimedia y guarda una proyección consultable en MySQL. */
@Service
public class WikimediaDatabaseConsumer {
    private static final Logger log = LoggerFactory.getLogger(WikimediaDatabaseConsumer.class);
    private final WikimediaEventRepository repository;
    public WikimediaDatabaseConsumer(WikimediaEventRepository repository) {
        this.repository = repository;
    }

    /**
     * ConsumerRecord conserva metadata que un String solo no tiene: key, partición y offset.
     * El commit del offset ocurre tras retornar; por eso se persiste antes de confirmar el
     * procesamiento. Una caída entre ambos pasos puede reprocesar: es at-least-once.
     */
    @KafkaListener(topics = "${app.topic}")
    public void consume(ConsumerRecord<String, String> record) {
        repository.save(new WikimediaEvent(record.key(), record.value()));
        log.info("Evento persistido: partition={}, offset={}, key={}", record.partition(), record.offset(), record.key());
    }
}
