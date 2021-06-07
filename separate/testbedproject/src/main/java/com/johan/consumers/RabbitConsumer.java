package com.johan.consumers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.johan.generic.Consumer;
import com.johan.generic.TestData;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class RabbitConsumer implements Consumer {

    private static final String EXCHANGE = "ledger-exchange";
    private static final String TYPE = "direct";
    private static final String QUEUE = "ledger-1";
    private static final String ROUTING_KEY = "ledger-route";
    private static final boolean DURABLE = true;
    private static final boolean EXCLUSIVE = false;
    private static final boolean AUTO_DELETE = true;

    private Connection connection;
    private Channel channel;
    private TestData testData;
    private Thread thread = new Thread(() -> {
        try {
            channel.basicConsume(QUEUE, atMostOnceCallback(), consumerTag -> {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    });
    
    public RabbitConsumer() {
        //Using default values to connect
        ConnectionFactory factory = new ConnectionFactory();

        try {
            connection = factory.newConnection();
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

        testData = new TestData();

    }

    @Override
    public void startListener() {
        thread.start();
    }

    @Override
    public void handleContent(byte[] payload) {
        testData.handleMessage(payload);
    }

    @Override
    public void stopListener() {
        try {
            channel.close();
            connection.close();
        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private DeliverCallback atMostOnceCallback() {
        DeliverCallback callback = (consumerTag, delivery) -> {
            this.handleContent(delivery.getBody());
        };

        return callback;
    }
    
}
