package com.mq.testbedconsumers.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@EnableKafka
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaConfiguration {

    @Value("${kafka.topic}")
    public String topicName;

    @Value("${kafka.bootstrap}")
    public String bootStrapServers;

    @Value("${kafka.group}")
    public String consumerGroup;

    @Value("${kafka.offset-reset}")
    public String offsetReset;

    @Value("${kafka.sse-enabled}")
    public boolean sseEnabled;

    @Bean
    ConcurrentKafkaListenerContainerFactory<Integer, String>
                        kafkaListenerContainerFactory(ConsumerFactory<Integer, String> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory =
                                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        factory.getContainerProperties().setIdleBetweenPolls(4);
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConsumerFactory<Integer, String> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerProps());
    }


    @Bean
    public Map<String, Object> consumerProps() {
        Map<String, Object> props =
                new HashMap<>();
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                ByteArrayDeserializer.class);
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
               bootStrapServers);
        String groupId = consumerGroup + System.currentTimeMillis();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, offsetReset);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");

        if(sseEnabled) {
            props.put("security.protocol", "SSL");
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "path");
            props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "password");
            props.put(SslConfigs.SSL_ENABLED_PROTOCOLS_CONFIG, "TLSv1, TLSv1.1, TLSv1.2, TLSv1.3");
            
        }
        return props;
    }

}
