package com.seekersoftvendingapp;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.seekersoftvendingapp.database.table.DaoMaster;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

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
        // Image Fresco Application Init
        Fresco.initialize(getApplicationContext());

        // Database Application Init
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, ENCRYPTED ? "notes-db-encrypted" : "notes-db");
        Database db = ENCRYPTED ? helper.getEncryptedWritableDb("super-secret") : helper.getWritableDb();
        daoSession = new DaoMaster(db).newSession();

        // 初始化设备信息
        SeekerSoftConstant.DEVICEID = "c45774e8b52619c9";

        // 初始化串口设备
        NewVendingSerialPort.SingleInit();

    }

    public static Application getInstance() {
        return mSeekersoftApp;
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

}
