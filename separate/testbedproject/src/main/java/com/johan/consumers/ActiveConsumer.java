package com.johan.consumers;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import com.johan.generic.Consumer;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveConsumer implements Consumer {
    
    private Connection connection;
    private Session session;
    private Destination topic;
    private ActiveListener listener;

    public ActiveConsumer() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");

        try {
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic("ledger-1");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void startListener() {
        ActiveListener listener = new ActiveListener();
        try {
            session.setMessageListener(listener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleContent(byte[] payload) {
        //should not need     
    }

    @Override
    public void stopListener() {
        try {
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
