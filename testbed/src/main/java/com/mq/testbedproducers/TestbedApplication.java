package com.mq.testbedproducers;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class TestbedApplication {

    private static final String topicName = "ledger-1";
    private static final int numPartitions = 1;
    private static short replicas = 1;

    @Bean
    NewTopic moviesTopic() {
        return new NewTopic(topicName, numPartitions, replicas);
    }

    public static void main(String[] args) {
        SpringApplication.run(TestbedApplication.class, args);
    }

}
