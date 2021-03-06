package com.mq.testbedproducers.kafka;

import com.mq.testbedproducers.generics.AbstractGenericProducer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import com.mq.testbedproducers.generics.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
// @Component
// @ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaProducer extends AbstractGenericProducer {

    private KafkaTemplate<String, byte[]> producer;

    private NewTopic topic;

    public String topicName = "ledger-1";

    public String bootStrapServers = "localhost:9092";
    
    public String messageDelivery = "at-least-once";

    private KafkaProperties kafkaProperties;

    public Map<String, Object> producerConfigs() {
        kafkaProperties = new KafkaProperties();
        Map<String, Object> props =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                ByteArraySerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
               bootStrapServers);
        
        if (messageDelivery.equals(ConfigUtils.ONLY_ONCE)) {
            props.put(ProducerConfig.ACKS_CONFIG, "0");
            props.put(ProducerConfig.RETRIES_CONFIG, "0");
            props.put(ProducerConfig.LINGER_MS_CONFIG, "2");
        } else if (messageDelivery.equals(ConfigUtils.AT_LEAST_ONCE)) {
            props.put(ProducerConfig.ACKS_CONFIG, "1");
            props.put(ProducerConfig.LINGER_MS_CONFIG, "2");
        }
        return props;
    }

    public ProducerFactory<String, byte[]> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    public KafkaTemplate<String, byte[]> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    public NewTopic ledgerTopic() {
        return new NewTopic(topicName, 1, (short) 1);
    }

    public KafkaProducer() {
        this.topic = ledgerTopic();
        this.producer = kafkaTemplate();
    }

    @Override
    public void flush() {
	    producer.flush();
    }

    @Override
    public void publish(byte[] message) {
        producer.send(topic.name(), "ledger",message);
    }

    @Override
    public void close() {
        return;
    }
}
