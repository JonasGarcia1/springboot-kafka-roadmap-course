package com.interviewlab.wikimediaconsumer.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * Proyección de lectura del evento crudo. No intenta modelar todo el JSON de Wikimedia: conserva
 * el payload para practicar la persistencia de eventos y permitir reprocesos posteriores.
 */
@Entity
@Table(name = "wikimedia_events")
public class WikimediaEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventKey;
    private Instant receivedAt;

    // @Lob evita truncar eventos JSON cuyo tamaño supere un VARCHAR convencional.
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String payload;

    protected WikimediaEvent() {
        // JPA necesita un constructor sin argumentos; no es parte del caso de uso.
    }

    public WikimediaEvent(String eventKey, String payload) {
        this.eventKey = eventKey;
        this.payload = payload;
        this.receivedAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getEventKey() { return eventKey; }
    public Instant getReceivedAt() { return receivedAt; }
    public String getPayload() { return payload; }
}
