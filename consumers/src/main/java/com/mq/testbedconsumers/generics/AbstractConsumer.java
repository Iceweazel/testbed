package com.mq.testbedconsumers.generics;

import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractConsumer {

    protected static final String topicName = "ledger-1";
    protected static final String START_TEST = "start_test";
    protected static final String END_TEST = "end_test";
    protected static final String WARM_UP = "warm_up";

    protected boolean testStarted = false;

    private static TestData testData = new TestData();

    protected void endTest() {
        testStarted = false;
        log.info("DONE MEASURING DATA \n -------------");
        log.info(testData.getData());
    }

    protected void startTest() {
        testStarted = true;
    }

    protected void handleContent(byte[] message) {
        log.info("Message Received: {}", new String(message));
        if (message.length == 1) {
            //either start or end test sent
            if (message[0] == '1') {
                log.info(END_TEST);
                endTest();
            } else if (message[1] == '2') {
                log.info(START_TEST);
                startTest();
            } else {
                log.info("WARM UP DONE--------------");
                if (testData == null)
                    testData = new TestData();

                testData.reset();
            }
	        return;
        }

        if(testStarted) {
            testData.addMessage(message);
            return;
        }
    }

    public AbstractConsumer() {
    }
}
