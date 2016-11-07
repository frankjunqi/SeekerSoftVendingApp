package com.seekersoftvendingapp.serialourtnative;

/**
 * Created by kjh08490 on 2016/11/7.
 */

public class SerialNativeInterface {

    /**
     * Open serial port use portName
     *
     * @param portName as "/dev/ttyMT1"
     * @return the long of "portHandle" about the Serialport Handle
     */
    public native long openPort(String portName);


    /**
     * Set Params for SerialPort
     *
     * @param baudRate as "9600"
     * @param dataBits as ""
     * @param stopBits as ""
     * @return Set Params For SerialPort is or not success.
     */
    public native boolean setParams(int baudRate, int dataBits, int stopBits);

    /**
     * Read The SerialPort Data
     *
     * @param portHandle The SerialPort Handle
     * @param byteCount  read byte count
     * @return The Data From The SerialPort
     */
    public native byte[] readBytes(long portHandle, int byteCount);


    /**
     * Write the SerialPort Data
     *
     * @param portHandle The SerialPort Handle
     * @param buffer     The byte Data of Serialport to write
     * @return Write SerialPort is or not success
     */
    public native boolean writeBytes(long portHandle, byte[] buffer);

    /**
     * Close SerialPort
     *
     * @param portHandle The SerialPort Handle
     * @return Close the SerialPort is or not success
     */
    public native boolean closePort(long portHandle);

}
