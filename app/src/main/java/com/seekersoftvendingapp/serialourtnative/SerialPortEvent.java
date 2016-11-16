package com.seekersoftvendingapp.serialourtnative;

/**
 * Created by kjh08490 on 2016/11/7.
 */

public class SerialPortEvent {
    private int eventType;
    private int eventValue;
    private String portName;

    public SerialPortEvent(String portName, int eventType, int eventValue) {
        this.portName = portName;
        this.eventType = eventType;
        this.eventValue = eventValue;
    }

    public String getPortName() {
        return this.portName;
    }

    public int getEventType() {
        return this.eventType;
    }

    public int getEventValue() {
        return this.eventValue;
    }
}
