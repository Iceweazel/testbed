package com.mq.testbedconsumers.nats;

import com.mq.testbedconsumers.generics.AbstractConsumer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;


import io.nats.streaming.Message;

import io.nats.streaming.Options;
import io.nats.streaming.Options.Builder;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import io.nats.streaming.SubscriptionOptions;
import io.nats.streaming.MessageHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "nats")
public class NatsConsumer extends AbstractConsumer {
    

    private final String uri;
    private StreamingConnection streamingConnection;

    NatsConsumer() {
        this.uri = "nats://localhost:4222";
        this.subscribe();
    }

    private void subscribe() {
        Options options = new Options.Builder().natsUrl(uri).clientId("consumer").clusterId("nats").build();

        MessageHandler messageHandler = m -> this.handleContent(m);
        
        try {
            StreamingConnectionFactory cf = new StreamingConnectionFactory();
            cf.setOptions(options);
            streamingConnection = cf.createConnection();

            SubscriptionOptions subOpts = new SubscriptionOptions.Builder().manualAcks().durableName("ledger-1").build();

            streamingConnection.subscribe("ledger-1", messageHandler, subOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void handleContent(Message msg) {
        if(msg != null && msg.getData() != null) {
           handleContent(msg.getData());
        }
    }
}
