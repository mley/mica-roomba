package com.m303.roomba;

/**
 * Created by mley on 31.05.15.
 */
public class BrainFactory {

    public Brain newBrain() {
        return new MappingBrain();
    }
}
