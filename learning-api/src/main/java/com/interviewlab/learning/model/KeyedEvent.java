package com.interviewlab.learning.model;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

/**
 * Evento con key explícita. La key no garantiza orden global del topic: solo agrupa registros
 * relacionados en una partición y preserva el orden dentro de ese fragmento del log.
 */
public record KeyedEvent(@NotBlank String key, @NotBlank String payload, Instant occurredAt) { }
