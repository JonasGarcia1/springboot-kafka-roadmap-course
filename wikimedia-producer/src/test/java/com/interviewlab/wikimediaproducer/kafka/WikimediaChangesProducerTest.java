package com.interviewlab.wikimediaproducer.kafka;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import static org.mockito.Mockito.verify;

/**
 * Prueba unitaria del adaptador Kafka: no necesita Docker, broker ni una conexión SSE.
 * Mockito reemplaza KafkaTemplate por un objeto observable y permite verificar el contrato
 * topic + key + payload que el productor le entregaría al cliente Kafka real.
 */
@ExtendWith(MockitoExtension.class)
class WikimediaChangesProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void sendsThePayloadToTheConfiguredTopicWithItsBusinessKey() {
        WikimediaChangesProducer producer = new WikimediaChangesProducer(
                kafkaTemplate,
                "wikimedia.recentchange.v1"
        );

        producer.send("local-article-1", "{\"source\":\"local\"}");

        // Esta aserción no confirma que Kafka esté levantado; confirma que nuestro código arma
        // correctamente la intención de publicación. La integración real se practica en el módulo 18.
        verify(kafkaTemplate).send(
                "wikimedia.recentchange.v1",
                "local-article-1",
                "{\"source\":\"local\"}"
        );
    }
}
