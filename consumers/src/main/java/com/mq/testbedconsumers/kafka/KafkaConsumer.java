package com.mq.testbedconsumers.kafka;

import java.util.List;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "kafka")
public class KafkaConsumer extends AbstractConsumer {

    private static final String topicName = "ledger-1";

    @KafkaListener(topics = topicName)
    public void consumer(byte[] payload) {
	 // log.info("payload received is {}", new String(payload));
         handleContent(payload);
    }

    //@KafkaListener(topics = topicName, containerFactory = "kafkaListenerContainerFactory")
    //public void consume(@Payload List<byte[]> messages) {
    //    if(messages != null)
    //        for (byte[] consumerRecord : messages)
    //            handleContent(consumerRecord);
    //}

}
