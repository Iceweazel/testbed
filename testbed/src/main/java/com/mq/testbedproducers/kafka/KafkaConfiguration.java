package com.mq.testbedproducers.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import com.mq.testbedproducers.generics.ConfigUtils;

@Configuration
public class KafkaConfiguration {

    @Value("${kafka.topic}")
    public String topicName;

    @Value("${kafka.bootstrap}")
    public String bootStrapServers;

    @Value("${kafka.sse-enabled}")
    public boolean sseEnabled;
    
    @Value("${message.delivery}")
    public String messageDelivery;

    @Autowired
    private KafkaProperties kafkaProperties;

    public Map<String, Object> producerConfigs() {
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

}
