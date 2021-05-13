package com.mq.testbedproducers.generics;

import org.springframework.core.io.Resource;

public interface ProducerInterface {

    void publish(String message);
    void publish(byte[] payload);
    void warmUp();
    void produceWithPayload(Resource resource, int payloadSize, int maxThroughPut);
}
