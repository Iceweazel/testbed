package com.mq.testbedproducers.generics;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
public abstract class AbstractGenericProducer implements ProducerInterface {

    protected static int REPETITIONS = 1000;
    protected static int RUN_TIME_MS = 10000;
    protected static int ONE_SECOND_MS = 1000;

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

    // protected String payload;
    protected byte[] payload;
    private static final byte[] endPayload = {'1'};
    private static final byte[] startPayload = {'2'};
    private static final byte[] endWarmUp = {'3'};
    private static final byte[] endTest = {'4'};
	
    public String addTimeStamp(String message) {
        long now = System.currentTimeMillis();
        return new String(now + "-" + message);
    }

    public void loadPayload(int payloadSize) {
	    payload = new byte[payloadSize];
	    for(int i = 0; i < payloadSize; i++) {
		    payload[i] = 'a';
	    }
	    log.info("payload loaded with {}", new String(payload));
    }

    public void loadPayload(Resource r) {
        try {
            InputStream in = r.getInputStream();
            byte[] data = FileCopyUtils.copyToByteArray(in);
            payload = new byte[data.length];
            payload = data;
            log.info("Payload loaded with: {}", new String(payload, StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventListener(ApplicationStartedEvent.class)
    private void produce() {
        log.info("PRODUCE --------------");
        produceWithPayload(PAYLOAD_8_BYTES, 8, 100000);

        produceWithPayload(PAYLOAD_64_BYTES, 64, 100000);

        produceWithPayload(PAYLOAD_512_BYTES, 512, 25000);

        produceWithPayload(PAYLOAD_4096_BYTES, 4096, 10000);
        produceWithPayload(PAYLOAD_32768_BYTES, 32678, 1000);
	publish(endTest);
    }

    @Override
    public void produceWithPayload(Resource resource, int payloadSize, int maxThroughPut) {
        
        log.info("Produce with payload size {}", payloadSize);
        //load the neccessary test variables and payload first
        int minThroughput = (int) (maxThroughPut * 0.15);
        int currentThroughPut = minThroughput; //minThroughput;
        int incrementThroughPut = (int) (maxThroughPut - minThroughput) / 30;
        log.info("Max Through Put {} with payload size {}", maxThroughPut, payloadSize);
        log.info("Min Through Put {} with payload size {}", minThroughput, payloadSize);
	loadPayload(payloadSize);

        runTestUntilMaxLoad(currentThroughPut, maxThroughPut, incrementThroughPut, payloadSize);
    }

    private void runTestUntilMaxLoad(int currentThroughPut, int maxThroughPut, int incrementThroughPut, int payloadSize) {
        while(currentThroughPut < maxThroughPut) {
                warmUp(currentThroughPut);
		flush();
                publish(startPayload);
		flush();
                testWithPayload(currentThroughPut);
		flush();
                publish(endPayload);
		flush();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
           
            currentThroughPut += incrementThroughPut;
        }
    }

    private void testWithPayload(int currentThroughPut) {
        log.info("Produce with curr. TP  {}", currentThroughPut);
        long testStart = System.currentTimeMillis();
        long lastTimeStamp = testStart;

        long currentTime;
	long eachRoundTime;
	long timeTaken;

        int messagesSent = 0;

        do {
            currentTime = System.currentTimeMillis();

	    for(int i = 0; i < currentThroughPut; i++) {
                sendWithTimeStamp();
                messagesSent++;
            }
	    
            if (currentTime - lastTimeStamp > RUN_TIME_MS) {
		break;
            }

	    eachRoundTime = System.currentTimeMillis();
	    timeTaken = eachRoundTime - currentTime;

	    if (timeTaken < ONE_SECOND_MS)
		threadWait(ONE_SECOND_MS - timeTaken); //wait rest of the second

        } while (currentTime - testStart < RUN_TIME_MS);
	log.info("messages sent: {}", messagesSent);
    }

    private void sendWithTimeStamp() {
        addTimeStamp();
        publish(payload);
    }

    private void addTimeStamp() {
        long now  = System.nanoTime();
        byte[] date = longToBytes(now);
        for(int i = 0; i < date.length; i++)
            payload[i] = date[i];
    }

    @Override
    public void warmUp(int currentThroughPut) {
        long testStart = System.currentTimeMillis();
	testWithPayload(currentThroughPut);
        publish(endWarmUp);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        log.info("warm up done");
    }

    private void threadWait(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
    /**
     * both of these long functions are from https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     * @param x
     * @return
     */
    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    
    public long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }
}
