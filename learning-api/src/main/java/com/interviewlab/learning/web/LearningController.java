package com.interviewlab.learning.web;

import com.interviewlab.learning.kafka.LearningProducer;
import com.interviewlab.learning.model.KeyedEvent;
import com.interviewlab.learning.model.UserEvent;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.Map;

/**
 * Adaptador HTTP de entrada. REST resuelve la solicitud síncrona; Kafka continúa el trabajo
 * asíncrono. Por eso las operaciones devuelven 202 Accepted, no 201 Created.
 */
@RestController
@RequestMapping("/api")
@Tag(
        name = "Prácticas REST → Kafka",
        description = "Endpoints para publicar records de texto, JSON y eventos con key."
)
public class LearningController {
    private final LearningProducer producer;
    public LearningController(LearningProducer producer) {
        this.producer = producer;
    }

    /** Punto de partida: publica un String y permite observar serializers básicos. */
    @PostMapping("/messages")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Publicar un mensaje de texto",
            description = "Envía un String a learning.text.v1. Observá en los logs el topic, la partición y el offset."
    )
    public Map<String, String> message(@RequestBody @NotBlank String message) {
        producer.sendText(message);
        return Map.of("status", "accepted", "topic", "learning.text.v1");
    }

    /** @Valid protege el contrato HTTP antes de que un objeto inválido viaje al topic JSON. */
    @PostMapping("/users")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Publicar un usuario JSON validado",
            description = "Spring valida el DTO antes de serializarlo y enviarlo a learning.users.v1."
    )
    public Map<String, String> user(@Valid @RequestBody UserEvent user) {
        producer.sendUser(user);
        return Map.of("status", "accepted", "key", user.id());
    }

    /** La key entra por URL para practicar cómo una entidad de negocio define el orden. */
    @PostMapping("/events/{key}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(
            summary = "Publicar un evento con key",
            description = "Usá la misma key varias veces para observar que conserva el orden dentro de una partición."
    )
    public Map<String, String> event(@PathVariable String key, @RequestBody @NotBlank String payload) {
        producer.sendKeyed(new KeyedEvent(key, payload, Instant.now()));
        return Map.of("status", "accepted", "key", key);
    }
}
