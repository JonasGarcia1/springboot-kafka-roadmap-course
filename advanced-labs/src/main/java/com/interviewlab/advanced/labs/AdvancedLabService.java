package com.interviewlab.advanced.labs;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Cada método aísla una garantía Kafka para poder probarla desde un endpoint sin mezclarla con
 * el flujo básico. No sustituye la idempotencia de una base de datos externa.
 */
@Service
public class AdvancedLabService {

    private final KafkaTemplate<String, String> template;

    public AdvancedLabService(KafkaTemplate<String, String> transactionalKafkaTemplate) {
        this.template = transactionalKafkaTemplate;
    }

    /** Idempotencia: el broker descarta reintentos duplicados del mismo productor. */
    public void sendIdempotent(String key, String value) {
        template.send("labs.idempotent.v1", key, value);
    }

    /**
     * Ambas escrituras quedan visibles juntas para consumidores configurados con read_committed,
     * o ninguna si falla el bloque. Esto es atomicidad dentro de Kafka, no una transacción MySQL.
     */
    public void sendTransaction(String id, String payload) {
        template.executeInTransaction(operations -> {
            operations.send("labs.transaction.input.v1", id, payload);
            operations.send("labs.transaction.audit.v1", id, "AUDIT:" + payload);
            return true;
        });
    }

    /** Un tombstone (value null) permite que compactación elimine el estado anterior de una key. */
    public void updateState(String key, String value) {
        template.send("labs.state.v1", key, value);
    }
}
