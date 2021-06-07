package com.johan.generic;

import java.nio.ByteBuffer;
import java.util.Arrays;

import com.johan.consumers.RabbitConsumer;
import com.johan.producers.RabbitProducer;

public class TestRunner {
    
    private static final byte[] START_PAYLOAD = {'2'};
    private static final byte[] END_PAYLOAD = {'1'};
    private static final byte[] WARM_UP_DONE = {'3'};
    private static final byte[] END_TESTS = {'4'};
    private static final int RUN_TIME_MS = 10000;
    private static final int ONE_SECOND_MS = 1000;

    private byte[] payload;

    private Producer producer;
    private Consumer consumer;

    public TestRunner(String messageQueue) {
        if(messageQueue.equals("rabbit")) {
            producer = new RabbitProducer();
            consumer = new RabbitConsumer();
        }
        System.out.println("Runner - Consumer and producer started");
    }

    protected void initPayloadWithSize(int size) {
        payload = new byte[size];
        Arrays.fill(payload, (byte) 7);
    }

    public void startTests() {
        System.out.println("Runner - start tests");
        consumer.startListener();
        threadWait(1000);
        produceWithPayloadSize(8, 100000);
        // produceWithPayloadSize(64, 50000);
        // produceWithPayloadSize(512, 10000);
        // produceWithPayloadSize(4096, 2500);
        // produceWithPayloadSize(32768, 1500);
        // produceWithPayloadSize(1000000, 500);
    }

    public void endTests() {
        producer.publish(END_TESTS);
        threadWait(1000);
        consumer.stopListener();
        producer.close();
    }

    private void produceWithPayloadSize(int payloadSize, int maxThroughput) {
        int minThroughput = (int) (maxThroughput * 0.10);
        int incrementThroughPut = (maxThroughput - minThroughput) / 40;
        initPayloadWithSize(payloadSize);
        runTestUntilMaxLoad(minThroughput, maxThroughput, incrementThroughPut);
    }

    private void runTestUntilMaxLoad(int currentThroughPut, int maxThroughput, int incrementThroughPut) {
        System.out.printf("Runner - run with TP :%d \n", currentThroughPut);
        while(currentThroughPut < maxThroughput) {
            warmUp(currentThroughPut);
            producer.publish(START_PAYLOAD);
            testWithPayload(currentThroughPut);
            producer.publish(END_PAYLOAD);
            try {
                Thread.sleep(5000);
                //wait to print results to csv file
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
       
            currentThroughPut += incrementThroughPut;
        }
    }

    private void testWithPayload(int currentThroughPut) {
        long testStart = System.currentTimeMillis();
        long lastTimeStamp = testStart;

        long currentTime;
	    long eachRoundTime;
	    long timeTaken;

        do {
            currentTime = System.currentTimeMillis();

            for(int i = 0; i < currentThroughPut; i++) {
                sendWithTimeStamp();
            }

            if (currentTime - lastTimeStamp > RUN_TIME_MS) {
                break;
            }

	        eachRoundTime = System.currentTimeMillis();
	        timeTaken = eachRoundTime - currentTime;

	        if (timeTaken < ONE_SECOND_MS)
		        threadWait(ONE_SECOND_MS - timeTaken); //wait rest of the second

        } while (currentTime - testStart < RUN_TIME_MS);
    }

    private void sendWithTimeStamp() {
        getTimestampedPayload();
        producer.publish(payload);
    }

    private void getTimestampedPayload() {
        long now  = System.nanoTime();
        byte[] date = longToBytes(now);
        for(int i = 0; i < date.length; i++)
            payload[i] = date[i];
    }

    private void threadWait(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void warmUp(int currentThroughput) {
        testWithPayload(currentThroughput);
        threadWait(1000);
        producer.publish(WARM_UP_DONE);
    }

    /**
     * both of these long functions are from https://stackoverflow.com/questions/4485128/how-do-i-convert-long-to-byte-and-back-in-java
     * @param x
     * @return
     */
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }
    
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip 
        return buffer.getLong();
    }

}
