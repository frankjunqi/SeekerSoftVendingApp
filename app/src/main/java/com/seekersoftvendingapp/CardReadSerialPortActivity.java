package com.seekersoftvendingapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seekersoftvendingapp.serialourtnative.SerialPortEvent;
import com.seekersoftvendingapp.serialport.CardReadSerialPortUtil;

/**
 * Created by kjh08490 on 2016/11/2.
 */

public class CardReadSerialPortActivity extends AppCompatActivity implements View.OnClickListener {

    // 读取卡中的数据的消息
    private static final int CARDRECEIVECODE = 1101;

    private String TAG = CardReadSerialPortActivity.class.getSimpleName();

    private EditText et_senddata;

    private Button btn_open, btn_send, btn_close;

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
        setContentView(R.layout.activity_cardread_serialport);
        et_senddata = (EditText) findViewById(R.id.et_senddata);
        tv_getdata = (TextView) findViewById(R.id.tv_getdata);
        btn_open = (Button) findViewById(R.id.btn_open);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_open.setOnClickListener(this);
        btn_send.setOnClickListener(this);
        btn_close.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_open:
                CardReadSerialPortUtil.getInstance().setOnDataReceiveListener(new CardReadSerialPortUtil.OnDataReceiveListener() {
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
                break;
            case R.id.btn_send:
                CardReadSerialPortUtil.getInstance().sendBuffer(CardReadSerialPortUtil.HexToByteArr(et_senddata.getText().toString()));
                break;
            case R.id.btn_close:
                CardReadSerialPortUtil.getInstance().closeSerialPort();
                break;
        }
    }
}
