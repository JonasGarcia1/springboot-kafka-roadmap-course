package com.interviewlab.learning.kafka;

import java.util.Collection;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Laboratorio de consumer groups y rebalanceo.
 *
 * <p>Kafka ejecuta estos callbacks cuando una instancia entra o sale de un mismo grupo.
 * Antes de procesar más mensajes, el coordinador redistribuye las particiones para que una
 * partición tenga como máximo un consumidor activo dentro de ese grupo. Abrí dos instancias
 * de {@code learning-api} con el mismo group id para observar estos logs.</p>
 */
@Component
public class LearningRebalanceListener implements ConsumerRebalanceListener {

    private static final Logger log = LoggerFactory.getLogger(LearningRebalanceListener.class);

    /**
     * Se invoca antes de ceder particiones. En sistemas reales este es el lugar para confirmar
     * trabajo en memoria o cerrar recursos asociados a una partición.
     */
    @Override
    public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
        log.info("REBALANCE: se revocan particiones {}. Otro miembro del grupo puede recibirlas.", partitions);
    }

    /**
     * Se invoca al finalizar el reparto. Desde ahora esta instancia hará polling solo de estas
     * particiones y continuará desde el offset confirmado por el grupo.
     */
    @Override
    public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
        log.info("REBALANCE: se asignan particiones {}. El consumo puede continuar.", partitions);
    }
}
