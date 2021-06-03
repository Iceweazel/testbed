package com.mq.testbedproducers.activemq;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import static java.util.stream.IntStream.range;

@Slf4j
public class ActiveMQProducer extends AbstractGenericProducer {

    private String topic;
    private String brokerUrl;
    private String userName;
    private String password;

    private MessageProducer producer;
    private Session session;
    private Connection connection;

    public ActiveMQProducer() {
        topic = "ledger-1";
        brokerUrl = "tcp://localhost:61616";
        userName = "admin";
        password = "admin";
        startSession();
    }

    private void startSession() {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        try {
            connection = connectionFactory.createConnection();
            connection.start();
             
            //Creating a non transactional session to send/receive JMS message.
            session = connection.createSession(true,
                    Session.AUTO_ACKNOWLEDGE);  
             
            //Destination represents here our queue 'JCG_QUEUE' on the JMS server. 
            //The queue will be created automatically on the server.
            Destination destination = session.createTopic(topic); 
             
            // MessageProducer is used for sending messages to the queue.
            producer = session.createProducer(destination);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void flush() { return;}

    @Override
    public void publish(byte[] payload) {
        try{
            BytesMessage message = session.createBytesMessage();
            message.writeBytes(payload);
            producer.send(message);
            // jmsTemplate.convertAndSend(topic, payload);
        } catch(JMSException e){
            log.error("Recieved Exception during send Message: ", e);
        }
    }
    
    @Override
    public void close() {
        try {
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
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
