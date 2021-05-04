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

    @KafkaListener(topics = topicName)
    public void consume(final ConsumerRecord<String, String> consumerRecord) {
        log.debug("received {} {}", consumerRecord.key(), consumerRecord.value());
        handleContent(consumerRecord);
    }

    private void handleContent(ConsumerRecord<String, String> consumerRecord) {

        switch (consumerRecord.key()) {
            case WARM_UP:
                break;
            case START_TEST:
                startTest(consumerRecord.value());
                break;
            case END_TEST:
                endTest();
                break;
            default:
                //test data
                long latency = Instant.now().toEpochMilli() - consumerRecord.timestamp();
                totalLatency += latency;
                break;
        }
    }

    private void endTest() {
        long avgLatency = totalLatency/repetitions;
        long timeTaken = Instant.now().toEpochMilli() - testStart;
        log.info("The test took {} ms with an avg. latency of {} using payloadsize {}"
                , timeTaken, avgLatency, payloadSize);
    }

    private void startTest(String value) {
        totalLatency = 0L;
        testStart = Instant.now().toEpochMilli();
        String[] args = value.split("-");
        repetitions = Integer.parseInt(args[0]);
        payloadSize = Integer.parseInt(args[1]);
        log.info("{} with {} repetitions and payloadsize {}", START_TEST, repetitions, payloadSize);
    }
}
