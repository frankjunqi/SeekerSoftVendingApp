package com.seekersoftvendingapp.serialport;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Card Read Serial Port Util
 * <p>
 * Created by kjh08490 on 2016/11/1.
 */

public class CardReadSerialPort {

    private String TAG = CardReadSerialPort.class.getSimpleName();

    // single object
    private static CardReadSerialPort cardReadSerialPort;

    // serial port JNI object
    private SeekerSoftSerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    // serial port about read thread listen for listen the data from the serial port device
    private OnDataReceiveListener onDataReceiveListener = null;

    // serial port read thrad & running flag
    private ReadThread mReadThread;

    // serial port thread interrupt and close serial port:
    // true : close ; false : open
    private boolean isStop = true;

    // device & baudrate
    private String devicePath = "/dev/ttymxc4";
    // tty02--- ttymxc1   ; ttyo3---ttymxc2  ;  tty04---ttymxc3  ;  tty05---ttymxc4  ;
    //   ICCard is OK        ICCard is Ok       ICCrad is not BAD     ICCard is OK
    // tty06---ttyES0  ; tty07---ttyES1 ;
    //  ICCard is OK     ICCard is BAD(IDCardReadSerialPortUtil: length=1; regionStart=0; regionLength=-1)

    // tty02--- ttymxc1   ; ttyo3---ttymxc2  ;  tty04---ttymxc3  ;  tty05---ttymxc4  ;
    //   IDCard is OK        IDCard is Ok       IDCrad is not BAD     IDCard is OK
    // tty06---ttyES0  ; tty07---ttyES1 ;
    //  IDCard is OK     IDCard is BAD(IDCardReadSerialPortUtil: length=1; regionStart=0; regionLength=-1)

    private int baudrate = 9600;

    public interface OnDataReceiveListener {
        void onDataReceiveString(String IDNUM);

        void onDataReceiveBuffer(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    public static CardReadSerialPort getCradSerialInstance() {
        if (null == cardReadSerialPort) {
            cardReadSerialPort = new CardReadSerialPort();
            cardReadSerialPort.startReadThread();
        }
        return cardReadSerialPort;
    }

    /**
     * 开启 串口 读取 线程
     */
    private void startReadThread() {
        // 开启 串口 读取 线程
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    /**
     * 打开 Card Serial
     */
    public void openReadSerial() {
        try {
            //  打开 Card Serial
            if (isStop && mSerialPort == null) {
                mSerialPort = new SeekerSoftSerialPort(new File(devicePath), baudrate, 0);
                mOutputStream = mSerialPort.getOutputStream();
                mInputStream = mSerialPort.getInputStream();
                isStop = false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Init Card Serial Open Port Failed");
            mSerialPort = null;
            isStop = true;
        }
    }

    /**
     * 关闭串口
     */
    public void closeReadSerial() {
        isStop = true;
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }

    /**
     * 发送指令到串口
     *
     * @param cmd 　应该是原始指令的字符串
     * @return
     */
    public boolean sendCmds(String cmd) {
        boolean result = true;
        byte[] mBuffer = (cmd + "\r\n").getBytes();
        //注意：我得项目中需要在每次发送后面加\r\n，大家根据项目项目做修改，也可以去掉，直接发送mBuffer
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBuffer);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    /**
     * 发送指令到串口
     *
     * @param mBuffer 原始命令的二进制流
     * @return
     */
    public boolean sendBuffer(byte[] mBuffer) {
        boolean result = true;
        byte[] mBufferTemp = new byte[mBuffer.length];
        System.arraycopy(mBuffer, 0, mBufferTemp, 0, mBuffer.length);
        //注意：我得项目中需要在每次发送后面加\r\n，大家根据项目项目做修改，也可以去掉，直接发送mBuffer
        try {
            if (mOutputStream != null) {
                mOutputStream.write(mBufferTemp);
            } else {
                result = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            String IDNUM = "";
            while (!isInterrupted()) {
                Log.e(TAG, "isStop" + isStop);
                if (isStop) {
                    // 串口关闭的话，逻辑上不做处理。
                } else {
                    // 串口开启，做读取数据
                    try {
                        int size;
                        if (mInputStream == null) {
                            return;
                        }
                        byte[] buffer = new byte[1];
                        size = mInputStream.read(buffer);
                        IDNUM = IDNUM + new String(buffer, 0, size);

                        // 实时传出buffer,让业务进行处理。什么时候开始,什么时候结束
                        onDataReceiveListener.onDataReceiveBuffer(buffer, size);
                        Log.e(TAG, "length is:" + size + ",data is:" + new String(buffer, 0, size));

                        // 默认以 "\r\n" 结束读取
                        if (IDNUM.endsWith("\r\n")) {
                            if (null != onDataReceiveListener) {
                                onDataReceiveListener.onDataReceiveString(IDNUM);
                                IDNUM = "";
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        return;
                    }
                }
            }
        }
    }

    public static int isOdd(int num) {
        return num & 1;
    }


    /**
     * 16进制转成byte
     *
     * @param inHex 原始数据
     * @return
     */
    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

    /**
     * 16进制转成byte[]
     *
     * @param inHex 原始数据字符串
     * @return
     */
    public static byte[] HexToByteArr(String inHex) {
        byte[] result;
        int hexlen = inHex.length();
        if (isOdd(hexlen) == 1) {
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        } else {
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for (int i = 0; i < hexlen; i += 2) {
            result[j] = HexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }

}
