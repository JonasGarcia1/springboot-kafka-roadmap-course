package com.interviewlab.learning;

import com.interviewlab.learning.kafka.LearningRebalanceListener;
import java.util.List;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

/** Verifica que los callbacks pedagógicos no interrumpan el rebalanceo del cliente Kafka. */
class LearningRebalanceListenerTest {

    @Test
    void registra_asignacion_y_revocacion_sin_fallar() {
        var listener = new LearningRebalanceListener();
        var partitions = List.of(new TopicPartition("learning.text.v1", 0));

        assertThatCode(() -> {
            listener.onPartitionsAssigned(partitions);
            listener.onPartitionsRevoked(partitions);
        }).doesNotThrowAnyException();
    }
}
