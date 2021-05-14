package com.mq.testbedconsumers.active;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;

import org.apache.activemq.command.ActiveMQBytesMessage;
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
            ActiveMQBytesMessage objectMessage = (ActiveMQBytesMessage) message;
            byte[] payload = new byte[(int) objectMessage.getBodyLength()];
            objectMessage.readBytes(payload);
            handleContent(payload);
            //do additional processing
            log.debug("Received Message: "+ payload);
        } catch(Exception e) {
            log.error("Received Exception : "+ e);
        }
    }
}
