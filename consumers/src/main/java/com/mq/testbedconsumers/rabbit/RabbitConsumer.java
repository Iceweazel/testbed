package com.mq.testbedconsumers.rabbit;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "rabbit")
public class RabbitConsumer extends AbstractConsumer {

    public void receiveMessage(byte[] message) {
        handleContent(message);
    }

}
