package com.seekersoftvendingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seekersoftvendingapp.serialport.CardReadSerialPortUtil;

/**
 * Created by kjh08490 on 2016/11/2.
 */

public class CardReadSerialPortActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_senddata;

    private Button btn_send, btn_close;

    private TextView tv_getdata;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardread_serialport);
        et_senddata = (EditText) findViewById(R.id.et_senddata);
        tv_getdata = (TextView) findViewById(R.id.tv_getdata);
        btn_send = (Button) findViewById(R.id.btn_send);
        btn_close = (Button) findViewById(R.id.btn_close);
        btn_send.setOnClickListener(this);
        btn_close.setOnClickListener(this);

        CardReadSerialPortUtil.getInstance().setOnDataReceiveListener(new CardReadSerialPortUtil.OnDataReceiveListener() {
            @Override
            public void onDataReceive(byte[] buffer, int size) {
                String dataReceive = new String(buffer);
                tv_getdata.setText("Receive Date: " + dataReceive);
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                CardReadSerialPortUtil.getInstance().sendCmds(et_senddata.getText().toString());
                break;
            case R.id.btn_close:
                CardReadSerialPortUtil.getInstance().closeSerialPort();
                break;
        }
    }
}
