package com.seekersoftvendingapp.serialourtnative;

import android.text.TextUtils;

/**
 * Created by kjh08490 on 2016/11/7.
 */

public class SerialPort {

    private SerialNativeInterface serialNativeInterface;
    private SerialPortEventListener serialPortEventListener;
    private String portName;
    private long portHandle;
    private boolean portOpened = false;

    /**
     * 构造函数
     *
     * @param portName SerialPortName
     */
    public SerialPort(String portName) {
        this.portName = portName;
        this.serialNativeInterface = new SerialNativeInterface();
    }

    /**
     * Open the SerialPort
     *
     * @return is or not Success
     * @throws SerialPortException SerialPortException
     */
    public boolean openPort() throws SerialPortException {
        if (portOpened) {
            // port already opened
            throw new SerialPortException(this.portName, "openPort()", SerialPortException.TYPE_PORT_ALREADY_OPENED);
        } else if (TextUtils.isEmpty(portName)) {
            // portName is empty
            throw new SerialPortException(this.portName, "openPort()", SerialPortException.TYPE_NULL_NOT_PERMITTED);
        } else {
            // native method openPort, return portHandle
            this.portHandle = this.serialNativeInterface.openPort(this.portName);

            // portHandle exception as '-1' '-2' '-3' '-4'
            if (this.portHandle == -1) {
                // port busy
                throw new SerialPortException(this.portName, "openPort()", SerialPortException.TYPE_PORT_BUSY);
            } else if (this.portHandle == -2) {
                // port not found
                throw new SerialPortException(this.portName, "openPort()", SerialPortException.TYPE_PORT_NOT_FOUND);
            } else if (this.portHandle == -3) {
                // permission denied
                throw new SerialPortException(this.portName, "openPort()", SerialPortException.TYPE_PERMISSION_DENIED);
            } else if (this.portHandle == -4) {
                // incorrect serial port
                throw new SerialPortException(this.portName, "openPort()", SerialPortException.TYPE_INCORRECT_SERIAL_PORT);
            } else {
                // open OK
                this.portOpened = true;
                return true;
            }
        }
    }

    /**
     * Setting SerialPort
     *
     * @param baudRate "9600"
     * @param dataBits ""
     * @param stopBits ""
     * @return is or not Success
     * @throws SerialPortException SerialPortException
     */
    public boolean setParams(int baudRate, int dataBits, int stopBits) throws SerialPortException {
        checkPortOpened("setParams()");
        return this.serialNativeInterface.setParams(baudRate, dataBits, stopBits);
    }


    /**
     * Close SerialPort
     *
     * @return is or not close success
     * @throws SerialPortException SerialPortException
     */
    public boolean closePort() throws SerialPortException {
        checkPortOpened("closePort()");
        return false;
    }


    /**
     * Check Port is or not Opened
     *
     * @param methodName methodName
     * @throws SerialPortException SerialPortException
     */
    private void checkPortOpened(String methodName) throws SerialPortException {
        if (!this.portOpened) {
            throw new SerialPortException(this.portName, methodName, SerialPortException.TYPE_PORT_NOT_OPENED);
        }
    }

}
