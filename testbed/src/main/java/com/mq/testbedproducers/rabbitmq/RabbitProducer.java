package com.mq.testbedproducers.rabbitmq;

import javax.jms.Topic;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
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

    private RabbitTemplate rabbitTemplate;

    private String host;
    private int port;
    private String userName;
    private String password;
    private Queue queue;
    private TopicExchange exchange;
    private Binding binding;

    private static final String ROUTING_KEY = "foo.bar.baz";

    public RabbitProducer() {
        this.host = "localhost";
        this.port = 5672;
        this.userName = "guest";
        this.password = "guest";
        queue = queue();
        exchange = exchange();
        binding = binding(queue, exchange);

        try {
            rabbitTemplate = new RabbitTemplate(connectionFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }

    @Override
    public void flush() {
	    return;
    }

    @Override
    public void publish(byte[] payload) {
        rabbitTemplate.convertAndSend(topicExchangeName, ROUTING_KEY, payload);
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
        CachingConnectionFactory conn = new CachingConnectionFactory(host, port);
        conn.setUsername(userName);
        conn.setPassword(password);
        return conn;
    }

    Queue queue() {
        return new Queue(queueName, true);
    }

    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }
}
