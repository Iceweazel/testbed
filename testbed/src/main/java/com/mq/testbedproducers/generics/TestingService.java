package com.mq.testbedproducers.generics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;

import com.mq.testbedproducers.kafka.*;
import com.mq.testbedproducers.rabbitmq.*;
import com.mq.testbedproducers.activemq.*;
import com.mq.testbedproducers.pulsar.*;
import com.mq.testbedproducers.nats.*;

@Component
public class TestingService {

    private String testingMQ;
    private AbstractGenericProducer producer;

    TestingService(@Value("${testing.mq}") String testingMQ) {
        this.testingMQ = testingMQ;
    }

    @EventListener(ApplicationStartedEvent.class)
    private void startProducer() {
        switch (testingMQ) {
            case "kafka": 
                producer = new KafkaProducer();
                break;
            case "rabbit":
                producer = new RabbitProducer();
                break;
            case "active":
                producer = new ActiveMQProducer();
                break;
            case "pulsar":
                producer = new PulsarProducer();
                break;
            case "nats":
                producer = new NatsProducer();
                break;
            default:
                break;
        }
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            //TODO: handle exception
        }
        
        if(producer != null)
            producer.produce();
    }
}