package com.visitorfastpass.pass.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class PassEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(PassEventPublisher.class);
    private final KafkaTemplate<String, PassGeneratedEvent> kafkaTemplate;
    private final String topic;

    public PassEventPublisher(KafkaTemplate<String, PassGeneratedEvent> kafkaTemplate,
                              @Value("${app.kafka.topics.pass-generated}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(PassGeneratedEvent event) {
        kafkaTemplate.send(topic, event.eventId(), event).whenComplete((result, error) -> {
            if (error != null) log.error("Unable to publish pass event {}", event.eventId(), error);
            else log.info("Published pass event {} for visit {}", event.eventId(), event.visitId());
        });
    }
}
