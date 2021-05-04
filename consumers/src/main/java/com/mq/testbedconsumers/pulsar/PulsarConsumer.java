package com.mq.testbedconsumers.pulsar;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
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

    private static final String SUBSCRIPTION_NAME = "test-subscription";
    private static final String SERVICE_URL = "pulsar://localhost:6650";
    private static final String TOPIC_NAME = "test-topic";

    private PulsarClient client;

    public PulsarConsumer() {

        try {
            client = PulsarClient.builder()
                    .serviceUrl(SERVICE_URL)
                    .build();
            consumer = client.newConsumer()
                    .topic(TOPIC_NAME)
                    .subscriptionType(SubscriptionType.Shared)
                    .subscriptionName(SUBSCRIPTION_NAME)
                    .subscribe();;
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }


        do {
            // Wait until a message is available
            CompletableFuture<Message<byte[]>> future = consumer.receiveAsync();

            Message<byte[]> msg = null;
            try {
                msg = future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            // Extract the message as a printable string and then log
            String content = new String(msg.getData());
            log.debug("Received message '"+content+"' with ID "+msg.getMessageId());

            handleContent(content);
            // Acknowledge processing of the message so that it can be deleted
            try {
                consumer.acknowledge(msg);
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
