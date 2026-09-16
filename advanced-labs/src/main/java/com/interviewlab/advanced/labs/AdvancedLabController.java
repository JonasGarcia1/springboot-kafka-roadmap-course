package com.interviewlab.advanced.labs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

/** Puertas HTTP opt-in para ejecutar cada laboratorio avanzado de manera independiente. */
@RestController
@RequestMapping("/labs")
public class AdvancedLabController {

    private final AdvancedLabService service;

    public AdvancedLabController(AdvancedLabService service) {
        this.service = service;
    }

    @PostMapping("/idempotent/{key}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> idempotent(@PathVariable String key, @RequestBody String value) {
        service.sendIdempotent(key, value);
        return Map.of("lab", "idempotency");
    }

    @PostMapping("/transaction/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> transaction(@PathVariable String id, @RequestBody String value) {
        service.sendTransaction(id, value);
        return Map.of("lab", "transaction");
    }

    /** Enviar body vacío crea un tombstone; la compactación es asíncrona, no inmediata. */
    @PostMapping("/state/{key}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, String> state(@PathVariable String key, @RequestBody(required = false) String value) {
        service.updateState(key, value);
        return Map.of("lab", value == null ? "tombstone" : "compaction");
    }
}
