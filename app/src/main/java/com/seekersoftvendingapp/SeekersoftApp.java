package com.seekersoftvendingapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by kjh08490 on 2016/11/18.
 */

public class SeekersoftApp extends Application {
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = false;


    private static SeekersoftApp mSeekersoftApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mSeekersoftApp = this;
        // 初始化bugly
        CrashReport.initCrashReport(getApplicationContext(), "900004362", true);

        // 初始化设备信息
        //SeekerSoftConstant.DEVICEID = DeviceInfoTool.getDeviceId();
        // 初始化串口设备
        NewVendingSerialPort.SingleInit();

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static Application getInstance() {
        return mSeekersoftApp;
    }


}
