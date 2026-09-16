package com.interviewlab.wikimediaproducer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Microservicio de ingesta: solo conoce la fuente Wikimedia y el topic de Kafka, no MySQL.
 * Esa separación permite que la velocidad de la fuente no bloquee la persistencia final.
 */
@SpringBootApplication
public class WikimediaProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WikimediaProducerApplication.class, args);
    }
}
