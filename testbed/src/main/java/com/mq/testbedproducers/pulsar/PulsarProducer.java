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

import java.util.concurrent.CompletableFuture;

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
                    .messageRoutingMode(MessageRoutingMode.SinglePartition)
                    .compressionType(CompressionType.LZ4)
                    .create();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(String message) {
        // Send each message and log message content and ID when successfully received
        try {
            CompletableFuture<MessageId> msgId = producer.sendAsync(message.getBytes());
            log.debug("Published message '"+message+"' with the ID "+msgId);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void publish(byte[] payload) {
        // TODO Auto-generated method stub
        
    }

    // @Override
    // public void warmUp() {
    //     range(0, REPETITIONS).forEach(i -> {
    //         log.debug("sending new message");
    //         publish(WARM_UP);
    //         if ( i % 10000 == 0) {
    //             try {
    //                 producer.flush();
    //             } catch (Exception e) {
    //                 log.error(e.getMessage());
    //             }
    //         }
    //     });
    //     try {
    //         producer.flush();
    //     } catch (Exception e) {
    //         log.error(e.getMessage());
    //     }
    // }

}
