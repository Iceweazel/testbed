package com.mq.testbedproducers.nats;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import com.mq.testbedproducers.generics.AbstractGenericProducer;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import io.nats.streaming.Options;
import io.nats.streaming.Options.Builder;
import io.nats.streaming.AckHandler;
import io.nats.streaming.StreamingConnection;
import io.nats.streaming.StreamingConnectionFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NatsProducer extends AbstractGenericProducer {

    private final String uri;
    private StreamingConnection streamingConnection;
    private AckHandler ackHandler;
    private CountDownLatch ackDoneSignal;

    private static final String topicName = "ledger-1";

    public NatsProducer() {
        this.uri = "nats://localhost:4222";
        startStreamingConnection();
    }

    private void startStreamingConnection() {
        Options options = new Options.Builder().natsUrl(uri).clientId("producer").clusterId("nats-streaming").build();
        StreamingConnectionFactory cf = new StreamingConnectionFactory(options);
        ackDoneSignal = new CountDownLatch(1);
        ackHandler = getAtLeastOnceAckHandler();

        try {
            streamingConnection = cf.createConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
    public void flush() {
	    return;
    }

    @Override
    public void publish(byte[] payload) {
        try {
            // natsConnection.publish(topicName, payload);
            streamingConnection.publish(topicName, payload, ackHandler);
            log.debug("message published: {}", payload);
        } catch (Exception ioe) {
            log.error("Error publishing message: {} to {} ", payload.toString(), topicName, ioe);
        }   
    }

    @Override
    public void close() {
        try {
            ackDoneSignal.await();
            streamingConnection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AckHandler getAtLeastOnceAckHandler(){
        return  new AckHandler() {
            @Override
            public void onAck(String nuid, String subject, byte[] data, Exception ex) {

                if(ex != null){
                    System.out.println("resend the message");
                    try {
                        streamingConnection.publish(topicName, data, ackHandler);
                    } catch (Exception e){

                    }
                }

                if(data.length == 1 && data[0] == '4') {
                    ackDoneSignal.countDown();
                }
            }
            @Override
            public void onAck(String s, Exception e) {
            }
        };
    }
    
}
