package com.mq.testbedconsumers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@EnableJms
public class TestbedConsumersApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestbedConsumersApplication.class, args);
    }

}
