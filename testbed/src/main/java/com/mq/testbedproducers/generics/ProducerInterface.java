package com.mq.testbedproducers.generics;

import org.springframework.core.io.Resource;

public interface ProducerInterface {

    void flush();
    void publish(String message);
    void publish(byte[] payload);
    void warmUp(int currentThroughput);
    void produceWithPayload(int payloadSize, int maxThroughPut);
}
