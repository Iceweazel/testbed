package com.johan.producers;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.johan.generic.Producer;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveProducer implements Producer {

    private Connection connection;
    private Session session;
    private Destination topic;
    private MessageProducer producer;

    public ActiveProducer() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        try {
            connection = factory.createConnection();
            factory.setUseAsyncSend(true);
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic("ledger-1");
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(byte[] payload) {
        BytesMessage message;
        try {
            message = session.createBytesMessage();
            message.writeBytes(payload);
            producer.send(message);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }        
    }
}
