package org.example.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class MessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(MessagePublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final AtomicLong counter = new AtomicLong();

    public MessagePublisher(KafkaTemplate<String, String> kafkaTemplate,
                            @Value("${app.kafka.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    @Scheduled(fixedRate = 1000)
    public void publish() {
        long id = counter.incrementAndGet();
        String message = "Message #" + id + " at " + LocalDateTime.now();
        kafkaTemplate.send(topic, String.valueOf(id), message);
        log.info("[PUBLISH] {}", message);
    }
}
