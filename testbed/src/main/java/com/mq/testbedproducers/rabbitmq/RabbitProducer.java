package com.mq.testbedproducers.rabbitmq;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import com.rabbitmq.client.ConfirmCallback;

import lombok.extern.slf4j.Slf4j;

import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType;

@Slf4j
public class RabbitProducer extends AbstractGenericProducer {

    private RabbitTemplate rabbitTemplate;

    private Queue queue;
    private TopicExchange exchange;
    private Binding binding;
    private RabbitAdmin rabbitAdmin;
    private ConnectionFactory connectionFactory;

    private static final String ROUTING_KEY = "foo.bar.baz";

    public RabbitProducer() {
        try {
            connectionFactory = connectionFactory();
            connectionFactory.createConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        queue = queue();
        exchange = exchange();
        binding = binding(queue, exchange);
        rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(binding);

        rabbitTemplate = rabbitAdmin.getRabbitTemplate();
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback(){

            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if(ack) {
                    
                } else {

                }             
            }
            
        });
    }

    @Override
    public void flush() {
	    return;
    }

    @Override
    public void publish(byte[] payload) {
        CorrelationData cd = new CorrelationData();
        rabbitTemplate.convertAndSend(exchange.getName(), ROUTING_KEY, payload, cd);
    }

    @Override
    public void close() {
        return;
    }

    static final String topicExchangeName = "spring-boot-exchange";

    static final String queueName = "spring-boot";

    /**
     * Connection factory which established a rabbitmq connection used from a connection factory
     * @param connectionFactoryBean Connection factory bean to create connection.
     * @return A connection factory to create connections.
     * @throws Exception If wrong parameters are used for connection.
     */
    public ConnectionFactory connectionFactory() throws Exception {
        CachingConnectionFactory conn = new CachingConnectionFactory();
        CachingConnectionFactory publisherFactory = (CachingConnectionFactory) conn.getPublisherConnectionFactory();
        publisherFactory.setChannelCacheSize(1);
        publisherFactory.setPublisherConfirmType(ConfirmType.CORRELATED);
        return publisherFactory;
    }

    private Queue queue() {
        return new Queue(queueName, true);
    }

    private TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    private Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

}
