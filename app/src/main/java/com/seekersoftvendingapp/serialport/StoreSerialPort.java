package com.seekersoftvendingapp.serialport;

import android.util.Log;

import com.seekersoftvendingapp.util.KeyChangeUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Card Read Serial Port Util
 * <p>
 * Created by kjh08490 on 2016/11/1.
 */

public class StoreSerialPort {

    private String TAG = StoreSerialPort.class.getSimpleName();

    // single object
    private static StoreSerialPort portUtil;

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
    private String devicePath = "/dev/ttymxc2";// tty02--- ttymxc1   ; tty6---ttyES0

    private int baudrate = 38400;

    public interface OnDataReceiveListener {
        void onDataReceiveString(String IDNUM);

        void onDataReceiveBuffer(byte[] buffer, int size);
    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }

    public static StoreSerialPort getInstance() {
        if (null == portUtil) {
            portUtil = new StoreSerialPort();
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
     * @param cmd
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

    public static byte HexToByte(String inHex) {
        return (byte) Integer.parseInt(inHex, 16);
    }

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


    public static void main(String[] args) throws Exception {
        String cmdOpenStoreDoor = cmdOpenStoreDoor(2, 1, 3);
        System.out.print("cmdOpenStoreDoor = " + cmdOpenStoreDoor + "\n");

        String cmdCheckStoreDoor = cmdCheckStoreDoor(2, 1);
        System.out.print("cmdCheckStoreDoor = " + cmdCheckStoreDoor + "\n");

        String cmdCheckStoreStatus = cmdCheckStoreStatus(1, 1);
        System.out.print("cmdCheckStoreStatus = " + cmdCheckStoreStatus + "\n");
    }

    public static String cmdOpenStoreDoor(int type, int number, int door) {
        return getStoreCommand(new StringBuilder(
                String.valueOf(new StringBuilder(String.valueOf(String.format("%04x", new Object[]{Integer.valueOf(type)})))
                        .append(String.format("%04x", new Object[]{Integer.valueOf(number)}))
                        .append("00010001").toString()))
                .append(String.format("%02x", new Object[]{Integer.valueOf(door)})).toString());
    }

    public static String cmdCheckStoreDoor(int type, int number) {
        return getStoreCommand(new StringBuilder(
                String.valueOf(String.format("%04x", new Object[]{Integer.valueOf(type)})))
                .append(String.format("%04x", new Object[]{Integer.valueOf(number)}))
                .append("0002000100").toString());
    }

    public static String cmdCheckStoreStatus(int type, int number) {
        return getStoreCommand(new StringBuilder(String.valueOf(String.format("%04x", new Object[]{Integer.valueOf(type)})))
                .append(String.format("%04x", new Object[]{Integer.valueOf(number)}))
                .append("00E0000100").toString());
    }

    public static String getStoreCommand(String cmd) {
        return (cmd.replaceAll("\\s*", "") + String.format("%04x", new Object[]{Integer.valueOf(getSum(cmd))})).toUpperCase();
    }

    public static int getSum(String cmd) {
        int sum = 0;
        String inHex = cmd.replaceAll("\\s*", "");
        for (int i = 1; i <= cmd.length() / 2; i++) {
            sum += Integer.parseInt(inHex.substring((i * 2) - 2, i * 2), 16);
        }
        return sum;
    }

}
