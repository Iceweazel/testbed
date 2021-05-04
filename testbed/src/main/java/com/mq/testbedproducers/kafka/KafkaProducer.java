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

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaProducer extends AbstractGenericProducer {

    private final KafkaTemplate<String, String> producer;
    private final NewTopic topic;

    @Override
    public void publish(String key, String message) {
        producer.send(topic.name(), key,message).addCallback(
                result -> {
                    if (result != null) {
                        log.debug("Succesful production to kafka");
                    }
                },
                exception -> log.error("Failed to produce to kafka", exception));
    }

    public void warmUp() {
        // Produce sample data
        range(0, REPETITIONS).forEach(i -> {
            publish(WARM_UP, WARM_UP);
        });

        producer.flush();
        log.info("warm up done");
    }

    @Override
    public void produceWithPayload(Resource resource, int payloadSize) {
        loadPayload(resource);
        String startPayload = REPETITIONS + "-" + payloadSize;
        publish(START_TEST, startPayload);
        // Produce sample data
        range(0, REPETITIONS).forEach(i -> {
            publish(KEY, payload);
        });
        publish(END_TEST, END_TEST);

        producer.flush();
        log.info("{} messages were produced to topic {}", REPETITIONS, topic.name());
    }
}
