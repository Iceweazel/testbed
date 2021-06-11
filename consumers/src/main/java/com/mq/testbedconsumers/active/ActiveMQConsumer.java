package com.mq.testbedconsumers.active;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;

import java.time.Instant;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "active")
public class ActiveMQConsumer extends AbstractConsumer implements MessageListener {
    
    // @JmsListener(destination = "ledger-1")
    @Override
    public void onMessage(Message message) {
	    log.info("message rec");
        try{
            ActiveMQBytesMessage objectMessage = (ActiveMQBytesMessage) message;
            byte[] payload = new byte[(int) objectMessage.getBodyLength()];
            objectMessage.readBytes(payload);
            handleContent(payload);
            log.debug("Received Message: " + payload);
        } catch(Exception e) {
            log.error("Received Exception : " + e);
        }
    }
}
