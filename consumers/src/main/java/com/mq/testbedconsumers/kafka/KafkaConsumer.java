package com.mq.testbedconsumers.kafka;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.nio.charset.*;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaConsumer extends AbstractConsumer {

    private CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = topicName, containerFactory = "kafkaListenerContainerFactory")
    public void consume(byte[] consumerRecord) {
        // log.debug("received {} {}", consumerRecord.offset(), consumerRecord.value());
	    log.info("Received consumerRec: {}", new String(consumerRecord, StandardCharsets.UTF_8));
        handleContent(consumerRecord);
    }

    private void handleContent(byte[] message) {
        // byte[] message = consumerRecord.value();

        if (message.length == 1) {
            //either start or end test sent
            if (message[0] == '1') {
                log.info(END_TEST);
                endTest();
            } else {
                log.info(START_TEST);
                startTest();
            }
        }

        if(testStarted) {
            testData.addMessage(message);
            return;
        }
    }
}
