package com.mq.testbedproducers.activemq;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.jms.JmsException;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;

import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Session;
import javax.jms.JMSException;

@Slf4j
public class ActiveMQProducer extends AbstractGenericProducer {

    private String topic;
    private String brokerUrl;
    private String userName;
    private String password;

    private JmsTemplate jmsTemplate;

    public ActiveMQProducer() {
        topic = "ledger-1";
        brokerUrl = "tcp://localhost:61616";
        userName = "admin";
        password = "admin";
        startSession();
    }

    private void startSession() {
        jmsTemplate = new JmsTemplate(connectionFactory());
    }

    @Override
    public void flush() { return;}

    @Override
    public void publish(byte[] payload) {
        jmsTemplate.convertAndSend(topic, payload);
    }
    
    @Override
    public void close() {
    }

    public ConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory activeMQConnectionFactory  = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
        activeMQConnectionFactory.setUserName(userName);
        activeMQConnectionFactory.setPassword(password);
	    activeMQConnectionFactory.setUseAsyncSend(true);
        CachingConnectionFactory pubConnFactory = new CachingConnectionFactory(activeMQConnectionFactory);
        return  pubConnFactory;
    }

    public JmsTemplate jmsTemplate(){
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setConnectionFactory(connectionFactory());
        jmsTemplate.setPubSubDomain(true);  // enable for Pub Sub to topic. Not Required for Queue.
        jmsTemplate.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        jmsTemplate.setSessionTransacted(false);
        jmsTemplate.setDeliveryMode(DeliveryMode.PERSISTENT);
        return jmsTemplate;
    }
}
