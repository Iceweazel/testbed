package com.mq.testbedproducers.activemq;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import static java.util.stream.IntStream.range;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "active")
public class ActiveMQProducer extends AbstractGenericProducer {

    @Autowired
    private JmsTemplate jmsTemplate;

    @Value("${active-mq.topic}")
    private String topic;

    public void publish(String message) {
        try{
            log.debug("Attempting Send message to Topic: "+ topic);
            jmsTemplate.convertAndSend(topic, message);
        } catch(Exception e){
            log.error("Recieved Exception during send Message: ", e);
        }
    }

    @Override
    public void publish(byte[] payload) {
        try{
            log.debug("Attempting Send message to Topic: "+ topic);
            jmsTemplate.convertAndSend(topic, payload);
        } catch(Exception e){
            log.error("Recieved Exception during send Message: ", e);
        }
    }
}
