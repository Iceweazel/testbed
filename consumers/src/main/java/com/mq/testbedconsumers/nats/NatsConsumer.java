package com.mq.testbedconsumers.nats;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Iterator;

import com.mq.testbedconsumers.generics.AbstractConsumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import io.nats.client.Connection;
import io.nats.client.Dispatcher;
import io.nats.client.Message;
import io.nats.client.Nats;
import io.nats.client.Subscription;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "nats")
public class NatsConsumer extends AbstractConsumer {
    

    private final String uri;
    private Connection natsConnection;
    private Dispatcher disPatcher;

    NatsConsumer() {
        this.uri = "nats://localhost:4222";
        this.natsConnection = initConnection();
        this.subscribe();
    }

    private Connection initConnection() {

        try {
            return Nats.connect(uri);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    private void subscribe() {
        Subscription subscription = natsConnection
            .subscribe(topicName);

        disPatcher = natsConnection.createDispatcher((msg) -> {
            log.info("Received message \"%s\" on subject \"%s\"\n", 
                                    new String(msg.getData(), StandardCharsets.UTF_8), 
                                    msg.getSubject());
        });

        disPatcher.subscribe(topicName);
    }
}
