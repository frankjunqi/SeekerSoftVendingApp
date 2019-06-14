package com.seekersoftvendingapp.newtakeoutserial;

import android.util.Log;

import com.seekersoftvendingapp.serialport.SeekerSoftSerialPort;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Card Read Serial Port Util
 * <p>
 * Created by kjh08490 on 2016/11/1.
 */

public class CardReadSerialPort {

    private String TAG = CardReadSerialPort.class.getSimpleName();

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
    private boolean isStop = false;

    // device & baudrate
    private String devicePath = "/dev/ttymxc3";
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

    }

    public void setOnDataReceiveListener(OnDataReceiveListener dataReceiveListener) {
        onDataReceiveListener = dataReceiveListener;
    }


    public CardReadSerialPort() {
        onCreate();
    }

    /**
     * 打开 Card Serial
     */
    private void onCreate() {
        try {
            //  打开 Card Serial
            mSerialPort = new SeekerSoftSerialPort(new File(devicePath), baudrate, 0);
            mOutputStream = mSerialPort.getOutputStream();
            mInputStream = mSerialPort.getInputStream();
        } catch (Exception e) {
            Log.e(TAG, "Init Card Serial Open Port Failed");
            mSerialPort = null;
        }
        // 开启 串口 读取 线程
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    /**
     * 关闭串口
     */
    public void closeReadSerial() {
        isStop = true;
        if (mReadThread != null) {
            mReadThread.interrupt();
        }
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }


    private class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            // 起始码 字节码 地址码 功能码 排序 Yn行 Xn列 转数 卸货时间S 开关状态 掉货状态 CRC低 CRC高 停止码
            byte[] backData = new byte[16];
            while (!isStop && !isInterrupted()) {
                // 串口开启，做读取数据
                int size = 1;
                try {
                    if (mInputStream == null) {
                        return;
                    }
                    size = mInputStream.read(backData);
                    String hexStr = bytesToHexString(backData);
                    Log.e("TAG", "byte to str= " + hexStr + ",size=" + size);
                    // 默认以 "0xFE" 结束读取
                    if (hexStr.contains("0D0A03")) {
                        if (null != onDataReceiveListener) {
                            String tmp = hexStr.replace("02", "").replace("0D0A03 ", "");
                            String[] strlist = new String[tmp.length() / 2];
                            for (int i = 0; i < tmp.length() / 2; i++) {
                                strlist[i] = tmp.substring(i * 2, (i + 1) * 2);
                            }
                            String out = "";
                            for (int i = strlist.length; i < strlist.length; i++) {
                                if ("30".equals(strlist[i])) {
                                    out = out + "0";
                                } else if ("31".equals(strlist[i])) {
                                    out = out + "1";
                                } else if ("32".equals(strlist[i])) {
                                    out = out + "2";
                                } else if ("33".equals(strlist[i])) {
                                    out = out + "3";
                                } else if ("34".equals(strlist[i])) {
                                    out = out + "4";
                                } else if ("35".equals(strlist[i])) {
                                    out = out + "5";
                                } else if ("36".equals(strlist[i])) {
                                    out = out + "6";
                                } else if ("37".equals(strlist[i])) {
                                    out = out + "7";
                                } else if ("38".equals(strlist[i])) {
                                    out = out + "8";
                                } else if ("39".equals(strlist[i])) {
                                    out = out + "9";
                                }
                            }
                            onDataReceiveListener.onDataReceiveString(out);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                    return;
                }
            }

        }
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
     * 数组转换成十六进制字符串
     *
     * @return HexString
     */
    public String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toUpperCase());
        }
        return sb.toString();
    }

    public String getStringIn(String str) {
        String regEx = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
