package com.mq.testbedconsumers.generics;

import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractConsumer {

    protected static final String topicName = "ledger-1";
    protected static final String START_TEST = "start_test";
    protected static final String END_TEST = "end_test";
    protected static final String WARM_UP = "warm_up";

    protected long totalLatency;
    protected long delay;
    protected long testStart;
    protected int payloadSize;
    protected int lastSecond;
    protected int messageReceived;
    protected int msgPerSecond;
    protected int numSeconds;

    protected boolean testStarted = false;

    protected static TestData testData = new TestData();

    protected void endTest() {
        testStarted = false;
        log.info("DONE MEASURING DATA \n -------------");
        log.info(testData.getData());
    }

    protected void startTest(String value) {
        testStarted = true;
        numSeconds = 0;
        messageReceived = 0;
        lastSecond = 0;
        totalLatency = 0L;
        testStart = System.currentTimeMillis();
        String[] args = value.split("-");
        delay = Long.valueOf(args[1]);
        payloadSize = Integer.parseInt(args[2]);
        log.info("{} with {} delay and payloadsize {}", START_TEST, delay, payloadSize);
    }

    protected void startTest() {
        testData.reset();
        testStarted = true;
    }

    protected void handleContent(byte[] message) {

        if (message.length == 1) {
            //either start or end test sent
            if (message[0] == '1') {
                log.info(END_TEST);
                endTest();
            } else {
		        log.info("WARM_UP_DONE---------");
                log.info(START_TEST);
                startTest();
            }
	    return;
        }

        if(testStarted) {
            testData.addMessage(message);
            return;
        }
    }

    public AbstractConsumer() {
        this.numSeconds = 0;
        this.messageReceived = 0;
        this.totalLatency = 0L;
        this.testStart = System.currentTimeMillis();
        this.delay = 0;
        this.payloadSize = 0;
        this.lastSecond = 0;
    }
}
