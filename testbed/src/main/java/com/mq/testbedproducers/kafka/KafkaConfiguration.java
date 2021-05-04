package com.mq.testbedproducers.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
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

@Configuration
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaConfiguration {

    @Value("${kafka.topic}")
    public String topicName;

    @Value("${kafka.bootstrap}")
    public String bootStrapServers;

    @Autowired
    private KafkaProperties kafkaProperties;

    @ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props =
                new HashMap<>(kafkaProperties.buildProducerProperties());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
               bootStrapServers);
        return props;
    }

    @ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
    @Bean
    public NewTopic ledgerTopic() {
        return new NewTopic(topicName, 3, (short) 1);
    }

}
