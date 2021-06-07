package com.johan.generic;

public interface Producer {

    void publish(byte[] payload);
    void close();
}
