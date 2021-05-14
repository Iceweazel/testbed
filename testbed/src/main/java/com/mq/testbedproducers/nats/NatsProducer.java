package com.mq.testbedproducers.nats;

import java.io.IOException;

import com.mq.testbedproducers.generics.AbstractGenericProducer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import io.nats.client.Options.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "nats")
public class NatsProducer extends AbstractGenericProducer {

    private final String uri;
    private Connection natsConnection;

    private static final String topicName = "ledger-1";

    NatsProducer() {
        this.uri = "nats://localhost:4222";
        this.natsConnection = initConnection();
    }

    private Connection initConnection() {

        try {
            return Nats.connect(uri);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void publish(String message) {
        try {
            natsConnection.publish(topicName, message.getBytes());
            log.info("message published: {}", message);
        } catch (Exception ioe) {
            log.error("Error publishing message: {} to {} ", message, topicName, ioe);
        }
        
    }

    @Override
    public void publish(byte[] payload) {
        try {
            natsConnection.publish(topicName, payload);
            log.debug("message published: {}", payload);
        } catch (Exception ioe) {
            log.error("Error publishing message: {} to {} ", payload.toString(), topicName, ioe);
        }   
    }
    
}
