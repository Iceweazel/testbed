package com.mq.testbedproducers.kafka;

import com.mq.testbedproducers.generics.AbstractGenericProducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaProducer extends AbstractGenericProducer {

    private final KafkaTemplate<String, byte[]> producer;
    private final NewTopic topic;

    @Autowired
    KafkaProducer(KafkaTemplate<String, byte[]> template, NewTopic topic) {
        this.producer = template;
        this.topic = topic;
    }

    @Override
    public void flush() {
	    producer.flush();
    }

    @Override
    public void publish(byte[] message) {
        producer.send(topic.name(), "ledger",message);
        //producer.flush();
    }

    @Override
    public void publish(String message) {
        
    }
}
