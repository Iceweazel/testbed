package com.mq.testbedconsumers.pulsar;

import com.mq.testbedconsumers.generics.AbstractConsumer;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@ConditionalOnProperty(prefix = "testing", value = "mq", havingValue = "pulsar")
public class PulsarConsumer extends AbstractConsumer implements MessageListener {

    Consumer<byte[]> consumer;

    private final String serviceUrl;
    private final String topicName;
    private final String subName;

    private PulsarClient client;

    public PulsarConsumer(@Value("${pulsar.service-url}") String url, 
        @Value("${pulsar.topic}") String t, 
        @Value("${pulsar.subscription-name}") String sub) {

        super();

        this.serviceUrl = url;
        this.topicName = t;
        this.subName = sub;
        try {
            client = PulsarClient.builder()
                    .serviceUrl(serviceUrl)
                    .build();

            consumer = client
                .newConsumer(Schema.BYTES)
                .subscriptionName(subName)
                .subscriptionType(SubscriptionType.Exclusive)
                .messageListener(this).topic(topicName)
                .subscribe();

        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    // private void handleContent(String message) {

    //     if(message.startsWith(WARM_UP)) {
    //         log.debug("warmpup");
    //         return;
    //     } else if(message.startsWith(START_TEST)) {
    //         startTest(message);
    //         return;
    //     } else if(message.startsWith(END_TEST)) {
    //         endTest();
    //         return;
    //     } else {
    //         messageReceived++;
    //     }
    // }

	@Override
	public void received(Consumer arg0, Message arg1) {
        //String content = new String(arg1.getData());
        handleContent(arg1.getData());
        log.debug("Received message '"+arg1.getData()+"' with ID "+arg1.getMessageId());
        try {
            extracted(arg0, arg1);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
		
	}

    private void extracted(Consumer arg0, Message arg1) throws PulsarClientException {
        arg0.acknowledge(arg1);
    }

}
