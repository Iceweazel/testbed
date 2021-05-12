package com.mq.testbedconsumers.active;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

import javax.jms.Message;
import javax.jms.MessageListener;
import java.time.Instant;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "active")
public class ActiveMQConsumer extends AbstractConsumer implements MessageListener {

    @Override
    @JmsListener(destination = "${active-mq.topic}")
    public void onMessage(Message message) {
        try{
            ActiveMQTextMessage objectMessage = (ActiveMQTextMessage) message;
            String msg = objectMessage.getText();
            handleContent(msg);
            //do additional processing
            log.debug("Received Message: "+ msg);
        } catch(Exception e) {
            log.error("Received Exception : "+ e);
        }
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
            //test data
            long sent = Long.valueOf(message.split("-")[0]);
            long latency = Instant.now().toEpochMilli() - sent;
            totalLatency += latency;
        }
        log.debug("Received <" + message + ">");
    }
}
