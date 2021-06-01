package com.mq.testbedproducers.rabbitmq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import java.io.IOException;

@Configuration
@PropertySource("classpath:rabbit.properties")
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "rabbit")
public class RabbitConfig {

    static final String topicExchangeName = "spring-boot-exchange";

    static final String queueName = "spring-boot";

    /**
     * Environment properties file from rabbitmq configuration.
     */
    @Autowired
    private Environment env;

    /**
     * Establish a connection to a rabbit mq server.
     * @return Rabbit connection factory for rabbitmq access.
     * @throws IOException If wrong parameters are used for connection.
     */
    @Bean
    public RabbitConnectionFactoryBean connectionFactoryBean() throws IOException {
        RabbitConnectionFactoryBean connectionFactoryBean = new RabbitConnectionFactoryBean();
        connectionFactoryBean.setHost(env.getProperty("rabbit.host"));
        connectionFactoryBean.setPort(Integer.valueOf(env.getProperty("rabbit.port")));
        connectionFactoryBean.setUsername(env.getProperty("rabbit.username"));
        connectionFactoryBean.setPassword(env.getProperty("rabbit.password"));

        // SSL-Configuration if set
        if(env.getProperty("rabbit.ssl-enabled").equals("true")) {
            connectionFactoryBean.setUseSSL(true);
            connectionFactoryBean.setSslAlgorithm(env.getProperty("rabbit.ssl"));

            // This information should be stored safely !!!
            connectionFactoryBean.setKeyStore(env.getProperty("rabbit.keystore.name"));
            connectionFactoryBean.setKeyStorePassphrase(env.getProperty("rabbit.keystore.password"));
            connectionFactoryBean.setTrustStore(env.getProperty("rabbit.truststore"));
            connectionFactoryBean.setTrustStorePassphrase(env.getProperty("rabbit.truststore.password"));
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
    Queue queue() {
        return new Queue(queueName, true);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(topicExchangeName);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("foo.bar.#");
    }

}
