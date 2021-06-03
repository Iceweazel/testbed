package com.mq.testbedproducers.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;

import javax.jms.Topic;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

@Slf4j
public class RabbitProducer extends AbstractGenericProducer {

    // private RabbitTemplate rabbitTemplate;

    private String host;
    private int port;
    private String userName;
    private String password;
    // private Queue queue;
    // private TopicExchange exchange;
    // private Binding binding;

    private static final String ROUTING_KEY = "foo.bar.baz";

    // public RabbitProducer() {
        // this.host = "localhost";
        // this.port = 5672;
        // this.userName = "guest";
        // this.password = "guest";
    //     queue = queue();
    //     exchange = exchange();
    //     binding = binding(queue, exchange);
    //     try {
    //         rabbitTemplate = new RabbitTemplate(connectionFactory());
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

        
    // }

    @Override
    public void flush() {
	    return;
    }

    @Override
    public void publish(byte[] payload) {
        try {
            channel.basicPublish(topicExchangeName,ROUTING_KEY,properties,payload);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // rabbitTemplate.convertAndSend(exchange.getName(), ROUTING_KEY, payload);
    }

    // @Override
    // public void close() {
    //     return;
    // }

    static final String topicExchangeName = "spring-boot-exchange";

    static final String queueName = "spring-boot";

    // /**
    //  * Connection factory which established a rabbitmq connection used from a connection factory
    //  * @param connectionFactoryBean Connection factory bean to create connection.
    //  * @return A connection factory to create connections.
    //  * @throws Exception If wrong parameters are used for connection.
    //  */
    // public ConnectionFactory connectionFactory() throws Exception {
    //     CachingConnectionFactory conn = new CachingConnectionFactory(host, port);
    //     conn.setUsername(userName);
    //     conn.setPassword(password);
    //     return conn;
    // }

    // Queue queue() {
    //     return new Queue(queueName, true);
    // }

    // TopicExchange exchange() {
    //     return new TopicExchange(topicExchangeName);
    // }

    // Binding binding(Queue queue, TopicExchange exchange) {
    //     return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    // }

    private CachingConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private AMQP.BasicProperties properties;
    // private ConfirmListener confirmListener;
    // private int lastSize;
    // private ConcurrentNavigableMap<Long,Long> outstandingConfirms;


    public RabbitProducer() {
        this.host = "localhost";
        this.port = 5672;
        this.userName = "guest";
        this.password = "guest";
        factory = new CachingConnectionFactory(host, port);
        factory.setUsername(userName);
        factory.setPassword(password);

        properties = MessageProperties.MINIMAL_PERSISTENT_BASIC;
        try {
            connection = (Connection) factory.createConnection();
            channel = connection.createChannel();
            Map<String,Object> args = new HashMap<String,Object>();
            args.put("max-length-bytes",100000000);
            args.put("overflow","drop-head");
            channel.queueDeclare("test",true,false,true,args);
            
            channel.confirmSelect();
            // outstandingConfirms = new ConcurrentSkipListMap<>();
            // ConfirmCallback cleanOutstandingConfirms = (sequenceNumber, multiple) -> {
            //     if (multiple) {
            //         ConcurrentNavigableMap<Long, Long> confirmed = outstandingConfirms.headMap(
            //                 sequenceNumber, true
            //         );
            //         confirmed.clear();
            //     } else {
            //         outstandingConfirms.remove(sequenceNumber);
            //     }
            // };
            // channel.addConfirmListener(cleanOutstandingConfirms, (sequenceNumber, multiple) -> {
            //     long timestamp = outstandingConfirms.get(sequenceNumber);
            //     byte data[] = new byte[lastSize];
            //     .addTime(data,timestamp);
            // });
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            channel.queueDelete("test");
            
            channel.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
