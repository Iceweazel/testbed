package com.mq.testbedproducers.rabbitmq;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import static java.util.stream.IntStream.range;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "rabbit")
public class RabbitProducer extends AbstractGenericProducer {

    private final RabbitTemplate rabbitTemplate;

    private static final String ROUTING_KEY = "foo.bar.baz";

    public RabbitProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void flush() {
	    return;
    }

    @Override
    public void publish(String message) {
        rabbitTemplate.convertAndSend(RabbitConfig.topicExchangeName, ROUTING_KEY, message);
    }

    @Override
    public void publish(byte[] payload) {
        rabbitTemplate.convertAndSend(RabbitConfig.topicExchangeName, ROUTING_KEY, payload);
    }
}
