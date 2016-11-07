package com.seekersoftvendingapp.serialourtnative;

/**
 * Created by kjh08490 on 2016/11/7.
 */

public class SerialPortTimeoutException extends Exception {
    private String methodName;
    private String portName;
    private int timeoutValue;

    public SerialPortTimeoutException(String portName, String methodName, int timeoutValue) {
        super("Port name - " + portName + "; Method name - " + methodName + "; Serial port operation timeout (" + timeoutValue + " ms).");
        this.portName = portName;
        this.methodName = methodName;
        this.timeoutValue = timeoutValue;
    }

    public String getPortName() {
        return this.portName;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public int getTimeoutValue() {
        return this.timeoutValue;
    }
}
