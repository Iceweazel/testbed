package com.mq.testbedproducers.kafka;

import com.mq.testbedproducers.generics.AbstractGenericProducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Slf4j
// @Component
// @ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaProducer extends AbstractGenericProducer {

    @Autowired
    private KafkaTemplate<String, byte[]> producer;

    @Autowired
    private NewTopic topic;

    public KafkaProducer() {
    }

    @Override
    public void flush() {
	    producer.flush();
    }

    @Override
    public void publish(byte[] message) {
        producer.send(topic.name(), "ledger",message);
    }
}
