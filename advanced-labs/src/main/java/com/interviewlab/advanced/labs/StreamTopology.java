package com.interviewlab.advanced.labs;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

/**
 * Topología declarativa: al iniciar el módulo, Kafka Streams crea consumidores y productores
 * para transformar el input en output. El estado y escalado siguen el modelo de Kafka.
 */
@Configuration
@EnableKafkaStreams
class StreamTopology {

    /** Kafka Streams procesa cerca del dato: transforma cada valor sin un clúster externo de Spark. */
    @Bean KStream<String, String> uppercaseStream(StreamsBuilder builder) {
        KStream<String, String> stream = builder.stream("labs.stream.input.v1");
        // mapValues conserva la key y transforma solo el valor; mirá ambos topics en Kafka UI.
        stream.mapValues(value -> value == null ? null : value.toUpperCase()).to("labs.stream.output.v1");
        return stream;
    }
}
