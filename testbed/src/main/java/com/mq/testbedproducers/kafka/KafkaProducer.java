package com.mq.testbedproducers.kafka;

import com.mq.testbedproducers.generics.AbstractGenericProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static java.util.stream.IntStream.range;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaProducer extends AbstractGenericProducer {

    private final KafkaTemplate<String, String> producer;
    private final NewTopic topic;

    @Override
    public void publish(String key, String message) {
        producer.send(topic.name(), "ledger",message);
        producer.flush();
    }

    public void warmUp() {
        long testStart = System.currentTimeMillis();
        while((int) ((System.currentTimeMillis() - testStart) / 1000) < 10) {
            publish(WARM_UP, WARM_UP);
        }
        log.info("warm up done");
    }

    @Override
    public void produceWithPayload(Resource resource, int payloadSize, long delay) {
        warmUp();
        loadPayload(resource);
        String startPayload = START_TEST + "-" +  delay + "-" + payloadSize;

        publish(START_TEST, startPayload);
        long testStart = System.currentTimeMillis();

        // run test for 10 seconds
        while((int) ((System.currentTimeMillis() - testStart) / 1000) < 10) {
            String message = addTimeStamp(payload);
            publish(KEY, message);
            try {
                Thread.sleep(delay);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        publish(END_TEST, END_TEST);

        log.info("{} messages were produced to topic {}", REPETITIONS, topic.name());
    }
}
