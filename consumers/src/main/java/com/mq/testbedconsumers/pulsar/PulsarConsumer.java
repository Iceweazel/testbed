package com.mq.testbedconsumers.pulsar;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "pulsar")
public class PulsarConsumer extends AbstractConsumer {

    Consumer<byte[]> consumer;

    private final String serviceUrl;
    private final String topicName;
    private final String subName;

    private PulsarClient client;

    public PulsarConsumer(@Value("${pulsar.service-url}") String url, 
        @Value("${pulsar.topic}") String t, 
        @Value("${pulsar.subscription-name}") String sub) {

        this.serviceUrl = url;
        this.topicName = t;
        this.subName = sub;
        try {
            client = PulsarClient.builder()
                    .serviceUrl(serviceUrl)
                    .build();
            consumer = client.newConsumer()
                    .topic(topicName)
                    .subscriptionType(SubscriptionType.Shared)
                    .subscriptionName(subName)
                    .subscribe();;
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }


        do {
            // Wait until a message is available

            Message<byte[]> future = null;
            try {
                future = consumer.receive();
            } catch (PulsarClientException e) {
                log.error(e.getMessage());
            }
            // Message<byte[]> msg = null;
            // try {
            //     msg = future.get();
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // } catch (ExecutionException e) {
            //     e.printStackTrace();
            // }
            // Extract the message as a printable string and then log
            String content = new String(future.getData());
            log.debug("Received message '"+content+"' with ID "+future.getMessageId());

            handleContent(content);
            // Acknowledge processing of the message so that it can be deleted
            try {
                consumer.acknowledge(future);
            } catch (PulsarClientException e) {
                e.printStackTrace();
            }
        } while (true);
    }

    private void handleContent(String message) {
        if(message.startsWith(WARM_UP)) {
            log.debug("warmpup");
            return;
        } else if(message.startsWith(START_TEST)) {
            startTest(message);
            return;
        } else if(message.startsWith(END_TEST)) {
            endTest();
            return;
        }
        log.debug("Received <" + message + ">");
    }

    private void endTest() {
        long timeTaken = Instant.now().toEpochMilli() - testStart;
        log.info("{} took {} ms with a payload size of {} and {} messages",
                END_TEST, timeTaken, payloadSize, repetitions);

    }

    private void startTest(String message) {
        testStart = Instant.now().toEpochMilli();
        String[] args = message.split("-");
        repetitions = Integer.parseInt(args[1]);
        payloadSize = Integer.parseInt(args[2]);
        log.info("{} with {} repetitions and payloadsize {}", START_TEST, repetitions, payloadSize);
    }

}
