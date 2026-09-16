package com.interviewlab.wikimediaconsumer.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data genera la implementación en tiempo de ejecución. Extender JpaRepository entrega
 * save, findById y paginación sin escribir SQL para esta práctica.
 */
public interface WikimediaEventRepository extends JpaRepository<WikimediaEvent, Long> {
}
