package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.util.SeekerSoftConstant;

/**
 * 0. 获取设备号；
 * 1. 初始化页面；
 * 2. 机器绑定接口；
 * - 绑定失败，显示绑定设备号；
 * - 绑定成功，保存机器的基本信息 & 并且请求基础数据；
 * 3. 数据库基础数据初始化；
 * - 成功之后进行首页，初始化首页；
 * - 失败，停留下在当前页面，并且提示失败信息；
 * Created by kjh08490 on 2016/11/25.
 */

public class StartAppActivity extends AppCompatActivity {

    // 初始化失败
    private static final int RequestError = -1;

    private static ProgressBar pb_loadingdata;
    private static Button btn_tryagain;
    private static TextView tv_resultdata;

    private static int count = 0;

    private static Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RequestError:
                    handleError();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startapp);
        pb_loadingdata = (ProgressBar) findViewById(R.id.pb_loadingdata);
        btn_tryagain = (Button) findViewById(R.id.btn_tryagain);
        tv_resultdata = (TextView) findViewById(R.id.tv_resultdata);
        requestInitData();
        btn_tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                if (count == 3) {
                    successInit();
                } else {
                    handleLoading();
                    mHander.sendEmptyMessageDelayed(RequestError, 3000);
                }
            }
        });
    }

    private void requestInitData() {
        Toast.makeText(StartAppActivity.this, "初始化基础数据", Toast.LENGTH_LONG).show();
        mHander.sendEmptyMessageDelayed(RequestError, 3000);
    }


    private static void handleError() {
        tv_resultdata.setText("网络处理失败,设备号：\n" + SeekerSoftConstant.DEVICEID + " \n 请联系管理员进行初始化设备基础信息。");
        pb_loadingdata.setVisibility(View.INVISIBLE);
        btn_tryagain.setVisibility(View.VISIBLE);
    }


    private static void handleLoading() {
        tv_resultdata.setText("正在初始化系统....");
        btn_tryagain.setVisibility(View.INVISIBLE);
        pb_loadingdata.setVisibility(View.VISIBLE);
    }

    private void successInit() {
        Toast.makeText(StartAppActivity.this, "初始化基础数据成功", Toast.LENGTH_LONG).show();
        startActivity(new Intent(StartAppActivity.this, MainActivity.class));
    }
}
