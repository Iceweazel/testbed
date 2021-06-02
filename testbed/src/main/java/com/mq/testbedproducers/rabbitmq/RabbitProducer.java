package com.mq.testbedproducers.rabbitmq;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;

import java.io.IOException;

import static java.util.stream.IntStream.range;

@Slf4j
public class RabbitProducer extends AbstractGenericProducer {

    private RabbitTemplate rabbitTemplate;

    private String host;
    private int port;
    private String userName;
    private String password;

    private static final String ROUTING_KEY = "foo.bar.baz";

    public RabbitProducer() {
        this.host = "localhost";
        this.port = 5672;
        this.userName = "guest";
        this.password = "guest";
        Queue queue = queue();
        TopicExchange exchange = exchange();
        binding(queue, exchange);

        try {
            rabbitTemplate = new RabbitTemplate(connectionFactory());

        } catch (Exception e) {
            //TODO: handle exception
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

    static final String topicExchangeName = "spring-boot-exchange";

    static final String queueName = "spring-boot";

    // /**
    //  * Establish a connection to a rabbit mq server.
    //  * @return Rabbit connection factory for rabbitmq access.
    //  * @throws IOException If wrong parameters are used for connection.
    //  */
    // public RabbitConnectionFactoryBean connectionFactoryBean() throws IOException {
    //     RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
    //     connectionFactoryBean.setHost(host);
    //     connectionFactoryBean.setPort(port);
    //     connectionFactoryBean.setUsername(userName);
    //     connectionFactoryBean.setPassword(password);
    //     return connectionFactoryBean;
    // }

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
