package com.mq.testbedconsumers.rabbit;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "rabbit")
public class RabbitConsumer extends AbstractConsumer {

    public void receiveMessage(String message) {
        handleContent(message);
    }

    private void handleContent(String message) {
        if(message.startsWith(WARM_UP)) {
            log.debug("warmpup");
            return;
        } else if(message.startsWith(START_TEST)) {
            startTest(message);
            return;
        } else if(message.startsWith(END_TEST)) {
            endTest();
            return;
        } else {
            messageReceived++;
        }
        log.debug("Received <" + message + ">");
    }
}
