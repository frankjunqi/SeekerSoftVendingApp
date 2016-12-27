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

public class VendingSerialPort {

    private String TAG = VendingSerialPort.class.getSimpleName();

    // single object
    private static VendingSerialPort portUtil;

    // serial port JNI object
    private static SeekerSoftSerialPort mSerialPort;
    private OutputStream mOutputStream;
    private InputStream mInputStream;

    // serial port about read thread listen for listen the data from the serial port device
    private OnDataReceiveListener onDataReceiveListener = null;

    // serial port read thrad & running flag
    private ReadThread mReadThread;
    private boolean isStop = false;

    // device & baudrate
    private String devicePath = "/dev/ttymxc1";// tty02
    private int baudrate = 9600;

    public interface OnDataReceiveListener {
        void onDataReceiveString(String IDNUM);

        void onDataReceiveBuffer(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    public static VendingSerialPort getInstance() {
        if (null == portUtil) {
            portUtil = new VendingSerialPort();
            portUtil.onCreate();
        }
        return portUtil;
    }

    /**
     * 初始化串口信息
     */
    private void onCreate() {
        try {
            mSerialPort = new SeekerSoftSerialPort(new File(devicePath), baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();

            mReadThread = new ReadThread();
            mReadThread.start();
        } catch (Exception e) {
            Log.e(TAG, "Init Serial Port Failed");
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
        byte[] mBuffer = cmd.getBytes();
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
            while (!isStop && !isInterrupted()) {
                int size;
                try {
                    if (mInputStream == null)
                        return;
                    byte[] buffer = new byte[1];
                    size = mInputStream.read(buffer);
                    IDNUM = IDNUM + new String(buffer, 0, size);

                    // 实时传出buffer,让业务进行处理。什么时候开始,什么时候结束
                    onDataReceiveListener.onDataReceiveBuffer(buffer, size);
                    //Log.e(TAG, "length is:" + size + ",data is:" + new String(buffer, 0, size));

                    // 默认以 "\n" 结束读取
                    if (IDNUM.endsWith("\n")) {
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

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
            portUtil = null;
        }
    }


    /**
     * 业务层面的参数拼接
     *
     * @param col 列号
     * @param row 行号
     * @return
     */
    public static String cmdOpenVender(int col, int row) {
        return getVenderCommand("53" + String.format("%02x%02x000000", new Object[]{Integer.valueOf(col + 48), Integer.valueOf(row + 48)}));
    }

    /**
     * 获取打开螺纹柜的串口命令
     *
     * @param cmd
     * @return
     */
    public static String getVenderCommand(String cmd) {
        String cmdString = cmd.replaceAll("\\s*", "");
        return ("AA" + cmdString + Integer.toHexString(getBCC(cmdString)) + "AC").toUpperCase();
    }

    /**
     * 获取校验位
     *
     * @param cmd 原始命令
     * @return
     */
    public static int getBCC(String cmd) {
        int bcc = 0;
        for (int i = 1; i <= cmd.length() / 2; i++) {
            bcc ^= Integer.parseInt(cmd.substring((i * 2) - 2, i * 2), 16);
        }
        return bcc;
    }

}
