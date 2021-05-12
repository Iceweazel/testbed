package com.mq.testbedproducers.generics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
public abstract class AbstractGenericProducer implements ProducerInterface {

    protected static int REPETITIONS = 1000;

    protected static final String START_TEST = "start_test";
    protected static final String END_TEST = "end_test";
    protected static final String KEY = "test";
    protected static final String WARM_UP = "warm_up";

    @Value("classpath:payloads/payload_8_bytes.txt")
    protected Resource PAYLOAD_8_BYTES;

    @Value("classpath:payloads/payload_64_bytes.txt")
    protected Resource PAYLOAD_64_BYTES;

    @Value("classpath:payloads/payload_512_bytes.txt")
    protected Resource PAYLOAD_512_BYTES;

    @Value("classpath:payloads/payload_4096_bytes.txt")
    protected Resource PAYLOAD_4096_BYTES;

    @Value("classpath:payloads/payload_32768_bytes.txt")
    protected Resource PAYLOAD_32768_BYTES;

    protected String payload;

    public String addTimeStamp(String message) {
        long now = System.currentTimeMillis();
        return new String(now + "-" + message);
    }

    public void loadPayload(Resource r) {
        try {
            InputStream in = r.getInputStream();
            byte[] data = FileCopyUtils.copyToByteArray(in);
            payload = new String(data, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener(ApplicationStartedEvent.class)
    private void produce() {
        produceWithPayload(PAYLOAD_8_BYTES, 8, 100);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 90);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 80);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 70);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 60);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 50);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 40);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 30);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 20);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 10);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 5);
        produceWithPayload(PAYLOAD_8_BYTES, 8, 2);

        // produceWithPayload(PAYLOAD_64_BYTES, 64, 100);
        // produceWithPayload(PAYLOAD_512_BYTES, 512, 100);
        // produceWithPayload(PAYLOAD_4096_BYTES, 4096, 100);
        // produceWithPayload(PAYLOAD_32768_BYTES, 32678, 100);
    }
}
