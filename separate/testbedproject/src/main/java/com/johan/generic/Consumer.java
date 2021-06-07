package com.johan.generic;

public interface Consumer {
    
    void startListener();
    void handleContent(byte[] payload);
    void stopListener();
}
