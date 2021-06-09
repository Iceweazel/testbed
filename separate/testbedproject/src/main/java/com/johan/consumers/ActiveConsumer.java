package com.johan.consumers;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class ActiveConsumer {
    
    private Connection connection;
    private Session session;
    private Destination topic;
    private ActiveListener listener;

    public ActiveConsumer() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        ActiveListener listener = new ActiveListener();

        try {
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic("ledger-1");
            session.setMessageListener(listener);
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

}
