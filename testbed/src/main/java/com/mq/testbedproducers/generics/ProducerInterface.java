package com.mq.testbedproducers.generics;

import org.springframework.core.io.Resource;

public interface ProducerInterface {

    void publish(String key, String message);
    void warmUp();
    void produceWithPayload(Resource resource, int payloadSize);
}
