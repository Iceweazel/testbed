package com.mq.testbedconsumers.generics;

import java.time.Instant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractConsumer {

    protected static final String topicName = "ledger-1";
    protected static final String START_TEST = "start_test";
    protected static final String END_TEST = "end_test";
    protected static final String WARM_UP = "warm_up";

    protected static boolean testStarted = false;

    private static TestData testData = new TestData();
    
    protected void endTest() {
        testStarted = false;
        log.info("DONE MEASURING DATA \n -------------");
        log.info(testData.getData());
	    testData.writeToFile();
    }

    protected void startTest() {
        testStarted = true;
    }

    protected void handleContent(byte[] message) {
        
        if (message.length == 1) {
            //either start or end test sent
            if (message[0] == '1') {
                log.info(END_TEST);
                endTest();
            } else if (message[0] == '2') {
                log.info(START_TEST);
                startTest();
            } else if (message[0] == '3') {
                log.info("WARM UP DONE--------------");
                testStarted = false;
                if (testData == null)
                    testData = new TestData();

                testData.reset();
            } else {
		        log.info("END TEST---------------------");
		    }
	        return;
        }
	

	if (testStarted)
            testData.addMessage(message);
    }

    public AbstractConsumer() {
    }
}
