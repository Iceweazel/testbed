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
import io.nats.streaming.Message;
import io.nats.client.Nats;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import io.nats.streaming.SubscriptionOptions;
import  io.nats.streaming.MessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "nats")
public class NatsConsumer extends AbstractConsumer {
    

    private final String uri;
    // private Connection natsConnection;
    // private Dispatcher disPatcher;
    private StreamingConnection streamingConnection;

    NatsConsumer() {
        this.uri = "nats://localhost:4222";
        // this.natsConnection = initConnection();
        this.subscribe();
    }

    // private Connection initConnection() {

    //     try {
    //         return Nats.connect(uri);
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     } catch (InterruptedException e) {
    //         e.printStackTrace();
    //     }
    //     return null;
    // }

    private void subscribe() {

        StreamingConnectionFactory cf = new StreamingConnectionFactory("nats", "consumer");

        MessageHandler messageHandler = m -> this.handleContent(m);
        
        try {
            streamingConnection = cf.createConnection();

            SubscriptionOptions options = new SubscriptionOptions.Builder().manualAcks().durableName("ledger-1").build();

            streamingConnection.subscribe("ledger-1", messageHandler, options);
        } catch (Exception e) {
            e.printStackTrace();
        }


        // disPatcher = natsConnection.createDispatcher(msg -> {});

        // disPatcher.subscribe(topicName, msg -> handleContent(msg));
    }

    private void handleContent(Message msg) {
        if(msg != null && msg.getData() != null) {
           handleContent(msg.getData());
        }
    }
}
