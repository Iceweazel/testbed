package com.mq.testbedconsumers.kafka;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bouncycastle.util.test.Test;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaConsumer extends AbstractConsumer {

    @KafkaListener(topics = topicName, containerFactory = "kafkaListenerContainerFactory")
    public void consume(byte[] consumerRecord) {
        // log.debug("received {} {}", consumerRecord.offset(), consumerRecord.value());
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
