package com.seekersoftvendingapp.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.seekersoftvendingapp.StartAppActivity;

/**
 * Created by kjh08490 on 2017/1/4.
 */

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 开机启动
        Intent i = new Intent(context, StartAppActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}