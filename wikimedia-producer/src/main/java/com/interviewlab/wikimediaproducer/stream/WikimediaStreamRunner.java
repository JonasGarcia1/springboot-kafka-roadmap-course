package com.interviewlab.wikimediaproducer.stream;

import com.launchdarkly.eventsource.EventSource;
import com.launchdarkly.eventsource.MessageEvent;
import com.launchdarkly.eventsource.background.BackgroundEventHandler;
import com.launchdarkly.eventsource.background.BackgroundEventSource;
import com.interviewlab.wikimediaproducer.kafka.WikimediaChangesProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.net.URI;

/**
 * Adaptador de entrada para el stream SSE real. Este perfil no se activa por defecto porque es
 * infinito y con alto volumen: usalo solo después de comprender el perfil local.
 */
@Configuration
@Profile("wikimedia")
class WikimediaStreamRunner {

    private static final Logger log = LoggerFactory.getLogger(WikimediaStreamRunner.class);

    @Bean
    ApplicationRunner wikimediaStream(WikimediaChangesProducer producer,
                                      org.springframework.core.env.Environment environment) {
        return args -> {
            String url = environment.getRequiredProperty("app.wikimedia-url");
            // Desde okhttp-eventsource 4.x, EventSource lee en primer plano y
            // BackgroundEventSource aporta el hilo asíncrono que necesita Spring.
            BackgroundEventHandler handler = new BackgroundEventHandler() {
                @Override
                public void onOpen() {
                    log.info("Conectado al stream de Wikimedia");
                }

                @Override
                public void onClosed() {
                    log.info("Stream de Wikimedia cerrado");
                }

                @Override
                public void onComment(String comment) {
                    // Los comentarios SSE no son eventos de negocio; se ignoran deliberadamente.
                }

                @Override
                public void onMessage(String event, MessageEvent messageEvent) {
                    // El stream llega de forma asíncrona; publicarlo en Kafka evita bloquear por MySQL.
                    producer.send(event == null ? "wikimedia" : event, messageEvent.getData());
                }

                @Override
                public void onError(Throwable t) {
                    log.warn("Error en stream Wikimedia", t);
                }
            };

            EventSource.Builder source = new EventSource.Builder(URI.create(url));
            new BackgroundEventSource.Builder(handler, source).build().start();
        };
    }
}
