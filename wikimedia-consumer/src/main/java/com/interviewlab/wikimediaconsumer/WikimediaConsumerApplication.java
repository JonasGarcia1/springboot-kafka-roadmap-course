package com.interviewlab.wikimediaconsumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de salida: consume el log de Kafka y proyecta sus eventos en MySQL.
 * Puede detenerse y volver a arrancar sin pedir datos al productor: continúa desde su offset.
 */
@SpringBootApplication
public class WikimediaConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WikimediaConsumerApplication.class, args);
    }
}
