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
// @Service
// @ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "pulsar")
public class PulsarProducer extends AbstractGenericProducer {

    private String serviceUrl;
    private String topicName;

    private PulsarClient client;
    private Producer<byte[]> producer;

    public PulsarProducer() {
        serviceUrl = "pulsar://localhost:6650";
        topicName = "ledger-1";

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
    public void flush() {
	    return;
    }
   
    @Override
    public void publish(byte[] payload) {
        try {
            CompletableFuture<MessageId> msgId = producer.sendAsync(payload);
            log.debug("Published message '"+payload+"' with the ID "+msgId);
        } catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    @Override
    public void close() {
        try {
            producer.close();
            client.close();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }
}
