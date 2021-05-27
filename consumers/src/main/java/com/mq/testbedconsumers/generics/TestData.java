package com.mq.testbedconsumers.generics;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestData {

    public static final String FILE_NAME = "results.txt";

    private int numberOfMessagesReceived;
    private long totalLatency;
    private long currentTimeMillis;
    private long lastTimeStampMillis;
    private int sizeInBytes;
    private ArrayList<Double> list;
    private long minLatency;
    private long maxLatency;

    public TestData() {
        totalLatency = 0L;
        numberOfMessagesReceived = 0;
        sizeInBytes = 8;
        list = new ArrayList<>();
	minLatency = 1000L;
	maxLatency = 0L;
    }


    public long getMeanLatency() {
        if(numberOfMessagesReceived == 0) {
            return 0;
        } else {
            return totalLatency / numberOfMessagesReceived;
        }
    }

    public double getAvgMillisLatency(){
        return  (double)getMeanLatency()/Math.pow(10,6);
    }

    public double getMinMillisLatency() {
	return (double) minLatency/Math.pow(10,6);
    }

    public double getMaxMillisLatency() {
	return (double) maxLatency/Math.pow(10,6);
    }

    public void reset() {
        lastTimeStampMillis = currentTimeMillis;
        numberOfMessagesReceived = 0;
        totalLatency = 0L;
        list.clear();
	minLatency = 1000L;
	maxLatency = 0L;
    }

    public void addMessage(byte[] data) {

        sizeInBytes = data.length;
        long arrivalTime = System.nanoTime();
        long sentTime = getTimestamp(data);
        long latency = arrivalTime - sentTime;
        totalLatency += (latency);
	if (latency < minLatency)
	    minLatency = latency;

	if(latency > maxLatency)
            maxLatency = latency;

        numberOfMessagesReceived++;
        currentTimeMillis = System.currentTimeMillis();
        list.add((double) latency);
        if(currentTimeMillis - lastTimeStampMillis>30000){
            //reset();
        }
    }

    public int avgThroughput(){
        long now = System.currentTimeMillis();
        if (now - lastTimeStampMillis == 0) {
            return numberOfMessagesReceived / 1000;
        } else {
            return numberOfMessagesReceived / (int) ((now - lastTimeStampMillis) / 1000);
        }

    }

    public double calculateVariance(){

        double mean = getAvgMillisLatency();

        long variance = 0;
        for(double xi : list){
            variance += Math.pow((double)xi- mean,2);
        }
        return variance/(list.size()-1);
    }

    public String getData(){
        // throughput, latency(ms)
        return sizeInBytes + ","+ avgThroughput() + "," + getAvgMillisLatency() + "\n" + getMinMillisLatency() + "," + getMaxMillisLatency();
    }

    public void writeToFile() {
        try {
	    BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
	    writer.append(getData());
	    writer.append("\n");
            writer.close();
	} catch (IOException e) {
           e.printStackTrace();
	}
    }


    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.put(bytes);
        buffer.flip();//need flip
        return buffer.getLong();
    }

    public static long getTimestamp (byte[] data){
        byte[] timestamp = new byte[Long.BYTES];
        for(int i = 0; i < timestamp.length; i++){
            timestamp[i] = data[i];
        }
        return bytesToLong(timestamp);
    }
}
