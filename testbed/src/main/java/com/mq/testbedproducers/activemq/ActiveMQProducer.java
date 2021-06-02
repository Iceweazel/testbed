package com.mq.testbedproducers.activemq;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.ConnectionFactory;

import static java.util.stream.IntStream.range;

@Slf4j
// @Component
// @ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "active")
public class ActiveMQProducer extends AbstractGenericProducer {

    private JmsTemplate jmsTemplate;

    private String topic;
    private String brokerUrl;
    private String userName;
    private String password;

    public ActiveMQProducer() {
        topic = "ledger-1";
        brokerUrl = "tcp://localhost:61616";
        userName = "admin";
        password = "admin";
        this.jmsTemplate = jmsTemplate();
    }

    @Override
    public void flush() { return;}

    @Override
    public void publish(byte[] payload) {
        try{
            log.debug("Attempting Send message to Topic: "+ topic);
            jmsTemplate.convertAndSend(topic, payload);
        } catch(Exception e){
            log.error("Recieved Exception during send Message: ", e);
        }
    }

    public ConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory activeMQConnectionFactory  = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        activeMQConnectionFactory.setUserName(userName);
        activeMQConnectionFactory.setPassword(password);
	    activeMQConnectionFactory.setUseAsyncSend(true);
        return  activeMQConnectionFactory;
    }

    public JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setPubSubDomain(true);  // enable for Pub Sub to topic. Not Required for Queue.
        //jmsTemplate.setDeliveryDelay(10);
        return jmsTemplate;
    }
}
