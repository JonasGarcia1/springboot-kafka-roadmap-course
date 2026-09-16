package com.interviewlab.wikimediaproducer.stream;

import com.interviewlab.wikimediaproducer.kafka.WikimediaChangesProducer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Perfil seguro para estudiar. Produce siempre el mismo evento, por lo que podés repetir la
 * práctica sin depender de red ni llenar MySQL con el stream público de Wikimedia.
 */
@Configuration
@Profile("local")
class LocalSamplePublisher {

    @Bean ApplicationRunner sample(WikimediaChangesProducer producer) {
        // ApplicationRunner se ejecuta cuando Spring terminó de construir el contexto y sus beans.
        return args -> producer.send(
                "local-article-1",
                "{\"source\":\"local\",\"title\":\"Kafka para entrevista\",\"change\":\"sample\"}"
        );
    }
}
