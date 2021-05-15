package com.mq.testbedconsumers.generics;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestData {
    int numberOfMessagesReceived;
    long totalNano;
    long currentTimeMillis;
    long lastTimeStampMillis;

    int sizeInBytes;
    String settings;
    int avg;
    ArrayList<Double> list;

    public TestData() {
        numberOfMessagesReceived = 0;
        list = new ArrayList<>();
    }

    public int getNumberOfMessagesReceived() {
        return numberOfMessagesReceived;
    }

    public long getMeanLatency() {
        if(numberOfMessagesReceived == 0) {
            return 0;
        } else {
            return totalNano / numberOfMessagesReceived;
        }
    }

    public double getAvgMillisLatency(){
        return  (double)getMeanLatency()/Math.pow(10,6);
    }

    public void setNumberOfMessagesReceived(int numberOfMessagesReceived) {
        this.numberOfMessagesReceived = numberOfMessagesReceived;
    }

    public long getTotalNano() {
        return totalNano;
    }

    public void setTotalNano(long totalNano) {
        this.totalNano = totalNano;
    }

    public long getCurrentTimeMillis() {
        return currentTimeMillis;
    }

    public void setCurrentTimeMillis(long currentTimeMillis) {
        this.currentTimeMillis = currentTimeMillis;
    }

    public long getLastTimeStampMillis() {
        return lastTimeStampMillis;
    }

    public void setLastTimeStampMillis(long lastTimeStampMillis) {
        this.lastTimeStampMillis = lastTimeStampMillis;
    }

    public void reset() {
        lastTimeStampMillis = currentTimeMillis;
        numberOfMessagesReceived = 0;
        totalNano = 0;
        list.clear();
    }

    public void addMessage(byte[] data) {

        sizeInBytes = data.length;
        long arrivalTime = System.nanoTime();
        long sentTime = getTimestamp(data);
        long latency = arrivalTime-sentTime;
        totalNano += (latency);
        numberOfMessagesReceived++;
        currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis - lastTimeStampMillis>30000){
            //reset();
        }
    }

    public void endSubTest(){

    }

    public int avgThroughput(){
        int now = System.currentTimeMillis();
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

    String getData(){
        // throughput, latency(ms)
        return sizeInBytes + ","+ avgThroughput() + "," + getAvgMillisLatency();
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
