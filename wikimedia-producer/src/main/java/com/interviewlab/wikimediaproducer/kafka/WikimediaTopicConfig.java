package com.interviewlab.wikimediaproducer.kafka;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
/** Declara el canal compartido entre los dos microservicios sin depender de auto-create-topics. */
@Configuration
class WikimediaTopicConfig {

    @Bean
    NewTopic wikimediaTopic(@Value("${app.topic}") String topic) {
        // Tres particiones son un ejemplo de paralelismo; replicas=1 es la limitación del broker local.
        return TopicBuilder.name(topic).partitions(3).replicas(1).build();
    }
}
