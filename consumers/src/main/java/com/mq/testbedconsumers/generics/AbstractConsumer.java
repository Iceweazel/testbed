package com.mq.testbedconsumers.generics;

public abstract class AbstractConsumer {

    protected static final String topicName = "ledger-1";
    protected static final String START_TEST = "start_test";
    protected static final String END_TEST = "end_test";
    protected static final String WARM_UP = "warm_up";

    protected long totalLatency;
    protected int repetitions;
    protected long testStart;
    protected int payloadSize;
}
