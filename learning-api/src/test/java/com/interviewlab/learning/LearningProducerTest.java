package com.interviewlab.learning;

import com.interviewlab.learning.kafka.LearningProducer;
import com.interviewlab.learning.model.UserEvent;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;
import static org.mockito.Mockito.*;

class LearningProducerTest {
    @Test void publica_usuario_con_su_id_como_key() {
        KafkaTemplate<String, String> text = mock(KafkaTemplate.class);
        KafkaTemplate<String, Object> json = mock(KafkaTemplate.class);
        LearningProducer producer = new LearningProducer(text, json, "text", "users", "keyed");
        producer.sendUser(new UserEvent("u-1", "Ana", "ana@example.com"));
        verify(json).send("users", "u-1", new UserEvent("u-1", "Ana", "ana@example.com"));
    }
}
