package com.mq.testbedproducers.rabbitmq;

import com.mq.testbedproducers.generics.AbstractGenericProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    public void produceWithPayload(Resource resource, int payloadSize) {
        loadPayload(resource);
        String startPayload = START_TEST + "-" + REPETITIONS+ "-"+ payloadSize;
        publish(ROUTING_KEY, startPayload);
        // Produce sample data
        range(0, REPETITIONS).forEach(i -> {
            log.debug("sending new message");
            publish(ROUTING_KEY, payload);
        });
        publish(ROUTING_KEY, END_TEST);

        log.info("{} messages were produced to topic {}", REPETITIONS, RabbitConfig.topicExchangeName);
    }

    @Override
    public void publish(String key, String message) {
        rabbitTemplate.convertAndSend(RabbitConfig.topicExchangeName, key, message);
    }

    @Override
    public void warmUp() {
        range(0, REPETITIONS).forEach(i -> {
            publish(ROUTING_KEY, WARM_UP);
        });
    }
}
