package com.johan.generic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class TestData {
    public static final String FILE_NAME = "results.txt";

    private int numberOfMessagesReceived;
    private long totalLatency;
    private long currentTimeMillis;
    private long lastTimeStampMillis;
    private int sizeInBytes;
    private ArrayList<Double> list;
    private boolean testStarted;

    public TestData() {
        totalLatency = 0L;
        numberOfMessagesReceived = 0;
        sizeInBytes = 8;
        list = new ArrayList<>();
	    lastTimeStampMillis = System.currentTimeMillis();
	    currentTimeMillis = System.currentTimeMillis();
        testStarted = false;
    }


    public double getMedianLatency() {
        Object[] numArray = list.toArray();
        Arrays.sort(numArray);
        double median;
	    if (numArray.length % 2 == 0)
    	    median = ((double)numArray[numArray.length/2] + (double)numArray[numArray.length/2 - 1])/2;
	    else
            median = (double) numArray[numArray.length/2];
	    return median;
    }

    public long getMeanLatency() {
        if(numberOfMessagesReceived == 0) {
            return 0;
        } else {
            return totalLatency / numberOfMessagesReceived;
        }
    }

    public double getAvgMillisLatency(){
        return  (double) getMeanLatency()/Math.pow(10,6);
    }

    public void reset() {
        lastTimeStampMillis = System.currentTimeMillis();
        numberOfMessagesReceived = 0;
        totalLatency = 0L;
        list.clear();
    }


    public void handleMessage(byte[] payload) {
        if (payload.length == 1) {
            //either start or end test sent
            if (payload[0] == '1') {
                System.out.println("END TEST---------");
                testStarted = false;
                writeToFile();
            } else if (payload[0] == '2') {
                System.out.println("START TEST-------------");
                testStarted = true;
            } else if (payload[0] == '3') {
                System.out.println("WARM UP DONE--------------");
                testStarted = false;
                reset();
            } else {
		        System.out.println("END ALL TEST---------------------");
		    }
	        return;
        }
	

        if (testStarted)
            addMessage(payload);
    }

    private void addMessage(byte[] data) {

        sizeInBytes = data.length;
        long arrivalTime = System.nanoTime();
        long sentTime = getTimestamp(data);
        long latency = arrivalTime - sentTime;
        totalLatency += (latency);

        numberOfMessagesReceived++;
        currentTimeMillis = System.currentTimeMillis();
        list.add((double) latency/Math.pow(10,6));

    }

    public int avgThroughput(){
       return numberOfMessagesReceived / 10;
    }

    public double calculateVariance(){

        double mean = getAvgMillisLatency();

        long variance = 0;
        for(double xi : list){
            variance += Math.pow((double)xi - mean,2);
        }
        return variance/(list.size() - 1);
    }

    public String getData(){
        // throughput, latency(ms)
        return sizeInBytes + ","+ avgThroughput() + "," + getMedianLatency() + "\n";
    }

    public void writeToFile() {
        try {
	        BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true));
            System.out.println(getData());
	        writer.append(getData());
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
