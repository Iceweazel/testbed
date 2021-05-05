package com.mq.testbedproducers.generics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public abstract class AbstractGenericProducer implements ProducerInterface {

    protected static int REPETITIONS = 20000;

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
        produceWithPayload(PAYLOAD_8_BYTES, 8);
        try {
            Thread.sleep(100000);
        } catch (Exception e) {
            //TODO: handle exception
        }
        produceWithPayload(PAYLOAD_64_BYTES, 64);
        //produceWithPayload(PAYLOAD_512_BYTES, 512);
        //produceWithPayload(PAYLOAD_4096_BYTES, 4096);
        //produceWithPayload(PAYLOAD_32768_BYTES, 32678);
    }
}
