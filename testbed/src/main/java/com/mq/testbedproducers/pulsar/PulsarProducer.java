package com.mq.testbedproducers.pulsar;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "pulsar")
public class PulsarProducer extends AbstractGenericProducer {

    
    private final String serviceUrl;
    private final String topicName;

    private PulsarClient client;
    private Producer<byte[]> producer;

    public PulsarProducer(@Value("${pulsar.service-url}") String url, 
        @Value("${pulsar.topic}") String t) {

        this.serviceUrl = url;
        this.topicName = t;
        try {
            client = PulsarClient.builder()
                    .serviceUrl(serviceUrl)
                    .build();
            producer = client.newProducer()
                    .topic(topicName)
                    .compressionType(CompressionType.LZ4)
                    .create();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(String key, String message) {
        // Send each message and log message content and ID when successfully received
        try {
            MessageId msgId = producer.send(message.getBytes());
            log.debug("Published message '"+message+"' with the ID "+msgId);
        } catch (PulsarClientException e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void warmUp() {
        range(0, REPETITIONS).forEach(i -> {
            log.debug("sending new message");
            publish("", WARM_UP);
        });
    }

    @Override
    public void produceWithPayload(Resource resource, int payloadSize) {
        loadPayload(resource);
        String startPayload = START_TEST + "-" + REPETITIONS + "-" + payloadSize;
        publish(START_TEST, startPayload);
        // Produce sample data
        range(0, REPETITIONS).forEach(i -> {
            log.debug("sending new message");
            publish("", payload);
        });
        publish(END_TEST, END_TEST);

        log.info("{} messages were produced to topic {}", REPETITIONS, topicName);
    }
}
