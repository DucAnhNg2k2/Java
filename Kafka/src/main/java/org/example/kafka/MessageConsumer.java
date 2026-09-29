package org.example.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MessageConsumer {

    private static final Logger log = LoggerFactory.getLogger(MessageConsumer.class);

    // Publisher đẩy 1 message/giây nên listener cũng nhận và log ~1 event/giây
    @KafkaListener(topics = "${app.kafka.topic}")
    public void consume(ConsumerRecord<String, String> record) {
        log.info("[CONSUME] partition={} offset={} key={} value={}",
                record.partition(), record.offset(), record.key(), record.value());
    }
}
