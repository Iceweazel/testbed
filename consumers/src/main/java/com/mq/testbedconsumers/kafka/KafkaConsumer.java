package com.mq.testbedconsumers.kafka;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaConsumer extends AbstractConsumer {

    @KafkaListener(topics = topicName, containerFactory = "kafkaListenerContainerFactory")
    public void consume(final ConsumerRecord<String, String> consumerRecord) {
        log.debug("received {} {}", consumerRecord.offset(), consumerRecord.value());
        handleContent(consumerRecord);
    }

    private void handleContent(ConsumerRecord<String, String> consumerRecord) {
        String message = consumerRecord.value();

        if(message.startsWith(WARM_UP)) {
            log.debug("warmpup");
            return;
        } else if(message.startsWith(START_TEST)) {
            startTest(message);
            return;
        } else if(message.startsWith(END_TEST)) {
            endTest();
            return;
        } else {
            long latency = System.currentTimeMillis() - Long.valueOf(message.split("-")[0]);
            totalLatency += latency;
            messageReceived++;
        }
    }
}
