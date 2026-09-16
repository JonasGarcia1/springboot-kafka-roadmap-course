package com.interviewlab.wikimediaconsumer.web;

import com.interviewlab.wikimediaconsumer.persistence.WikimediaEvent;
import com.interviewlab.wikimediaconsumer.persistence.WikimediaEventRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/** Adaptador de consulta: permite comprobar con HTTP el resultado del pipeline asíncrono. */
@RestController
@RequestMapping("/api/wikimedia-events")
@Tag(name = "Consulta Wikimedia", description = "Verifica que el consumidor persistió eventos de Kafka en MySQL.")
public class WikimediaEventController {

    private final WikimediaEventRepository repository;

    public WikimediaEventController(WikimediaEventRepository repository) {
        this.repository = repository;
    }

    /**
     * La paginación limita el resultado para no cargar todo el histórico. Ordenar por id descendente
     * muestra primero los últimos eventos persistidos por el listener.
     */
    @GetMapping
    @Operation(
            summary = "Consultar los últimos eventos persistidos",
            description = "La respuesta viene de MySQL; no lee directamente de Kafka. Probá distintos valores de limit."
    )
    public List<WikimediaEvent> latest(@RequestParam(defaultValue = "20") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 100);
        return repository.findAll(PageRequest.of(0, safeLimit, Sort.by("id").descending())).getContent();
    }
}
