package com.seekersoftvendingapp.test;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seekersoftvendingapp.BaseActivity;
import com.seekersoftvendingapp.R;
import com.seekersoftvendingapp.serialport.CardReadSerialPort;

/**
 * Created by kjh08490 on 2016/11/2.
 */

public class TestCardReadActivity extends BaseActivity implements View.OnClickListener {

    // 读取卡中的数据的消息
    private static final int CARDRECEIVECODE = 1101;

    private String TAG = TestCardReadActivity.class.getSimpleName();

    private Button btn_open, btn_close;

    private static TextView tv_getdata;

    private static Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CARDRECEIVECODE:
                    tv_getdata.setText(tv_getdata.getText().toString() + "\n" + msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_cardread);
        tv_getdata = (TextView) findViewById(R.id.tv_getdata);
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_close = (Button) findViewById(R.id.btn_close);

        btn_open.setOnClickListener(this);
        btn_close.setOnClickListener(this);

        CardReadSerialPort.getCradSerialInstance().setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {
                Log.e("tag", IDNUM);
                Message message = new Message();
                message.what = CARDRECEIVECODE;
                message.obj = IDNUM;
                mHandle.sendMessage(message);
            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {
                // do nothing
                Log.e(TAG, "length is:" + size + ",data is:" + new String(buffer, 0, size));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:
                CardReadSerialPort.getCradSerialInstance();
                break;
            case R.id.btn_close:
                CardReadSerialPort.getCradSerialInstance().closeReadSerial();
                break;
        }
    }
}
