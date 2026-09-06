package com.visitorfastpass.notification.event;

import com.visitorfastpass.notification.service.NotificationProcessingService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PassGeneratedConsumer {
    private final NotificationProcessingService processingService;

    public PassGeneratedConsumer(NotificationProcessingService processingService) {
        this.processingService = processingService;
    }

    @KafkaListener(topics = "${app.kafka.topics.pass-generated}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PassGeneratedEvent event) {
        processingService.process(event);
    }
}
