package com.seekersoftvendingapp;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.seekersoftvendingapp.database.table.DaoMaster;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.seekersoftvendingapp.util.DeviceInfoTool;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.tencent.bugly.crashreport.CrashReport;

import org.greenrobot.greendao.database.Database;

/**
 * Created by kjh08490 on 2016/11/18.
 */

public class SeekersoftApp extends Application {
    /**
     * A flag to show how easily you can switch from standard SQLite to the encrypted SQLCipher.
     */
    public static final boolean ENCRYPTED = false;

    private DaoSession daoSession;

    private static SeekersoftApp mSeekersoftApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mSeekersoftApp = this;
        // 初始化bugly
        CrashReport.initCrashReport(getApplicationContext(), "900004362", true);

        // Database Application Init
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

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

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
