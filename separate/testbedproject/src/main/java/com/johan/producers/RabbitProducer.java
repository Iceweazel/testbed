package com.johan.producers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.johan.generic.Producer;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;

public class RabbitProducer implements Producer {

    private static final String EXCHANGE = "ledger-exchange";
    private static final String TYPE = "direct";
    private static final String QUEUE = "ledger-1";
    private static final String ROUTING_KEY = "ledger-route";
    private static final boolean DURABLE = true;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_DELETE = true;
    private static final BasicProperties PROPERTIES = MessageProperties.MINIMAL_PERSISTENT_BASIC;

    private Connection connection;
    private Channel channel;

    public RabbitProducer() {
        //only using default java options for connection
        ConnectionFactory connectionFactory = new ConnectionFactory();

        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            channel.exchangeDeclare(EXCHANGE, TYPE, DURABLE);
            channel.queueDeclare(QUEUE, DURABLE, EXCLUSIVE, AUTO_DELETE, null);
            channel.queueBind(QUEUE, EXCHANGE, ROUTING_KEY);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void publish(byte[] payload) {
        try {
            channel.basicPublish(EXCHANGE, ROUTING_KEY, PROPERTIES, payload);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void close() {
        try {
            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
    
}
