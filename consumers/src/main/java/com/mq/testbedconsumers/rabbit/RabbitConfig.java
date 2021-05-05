package com.mq.testbedconsumers.rabbit;

import java.io.IOException;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "rabbit")
public class RabbitConfig {

    static final String topicExchangeName = "spring-boot-exchange";

    static final String queueName = "spring-boot";

    @Value("${rabbit.host}")
    public String hostName;

    @Value("${rabbit.port}")
    public String hostPort;

    @Value("${rabbit.user}")
    public String userName;

    @Value("${rabbit.password}")
    public String password;

    @Value("${rabbit.sse-enabled}")
    public boolean sseEnabled;

    /**
     * Establish a connection to a rabbit mq server.
     * @return Rabbit connection factory for rabbitmq access.
     * @throws IOException If wrong parameters are used for connection.
     */
    @Bean
    public RabbitConnectionFactoryBean connectionFactoryBean() throws IOException {
        RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
        connectionFactoryBean.setHost(hostName);
        connectionFactoryBean.setPort(Integer.valueOf(hostPort));
        connectionFactoryBean.setUsername(userName);
        connectionFactoryBean.setPassword(password);

        // SSL-Configuration if set
        if(sseEnabled) {
            connectionFactoryBean.setUseSSL(true);
            //connectionFactoryBean.setSslAlgorithm(env.getProperty("rabbit.ssl"));

            // This information should be stored safely !!!
            //connectionFactoryBean.setKeyStore(env.getProperty("rabbit.keystore.name"));
            //connectionFactoryBean.setKeyStorePassphrase(env.getProperty("rabbit.keystore.password"));
            //connectionFactoryBean.setTrustStore(env.getProperty("rabbit.truststore"));
            //connectionFactoryBean.setTrustStorePassphrase(env.getProperty("rabbit.truststore.password"));
        }

        return connectionFactoryBean;
    }

    /**
     * Connection factory which established a rabbitmq connection used from a connection factory
     * @param connectionFactoryBean Connection factory bean to create connection.
     * @return A connection factory to create connections.
     * @throws Exception If wrong parameters are used for connection.
     */
    @Bean(name = "LEDGER_1")
    public ConnectionFactory connectionFactory(RabbitConnectionFactoryBean connectionFactoryBean) throws Exception {
        return new CachingConnectionFactory(connectionFactoryBean.getObject());
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter listenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(queueName);
        container.setMessageListener(listenerAdapter);
        return container;
    }

    @Bean
    MessageListenerAdapter listenerAdapter(RabbitConsumer consumer) {
        return new MessageListenerAdapter(consumer, "receiveMessage");
    }
}
