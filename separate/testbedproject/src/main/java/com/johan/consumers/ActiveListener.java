package com.johan.consumers;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.johan.generic.TestData;

import org.apache.activemq.command.ActiveMQBytesMessage;

public class ActiveListener implements MessageListener {

    private TestData testData;

    public ActiveListener() {
        this.testData = new TestData();
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof BytesMessage) {
            ActiveMQBytesMessage bytesMessage = (ActiveMQBytesMessage) message;
            byte[] payload;
            try {
                payload = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(payload);
                testData.handleMessage(payload);
            } catch (JMSException e) {
                e.printStackTrace();
            }
        }
    }
    
}
