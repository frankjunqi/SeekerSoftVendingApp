package com.seekersoftvendingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seekersoftvendingapp.newtakeoutserial.CardReadSerialPort;

/**
 * Created by Frank on 18/1/7.
 */

public class TestReadActivity extends BaseActivity {

    private TextView tv_read_num;
    private Button btn_click;


    private CardReadSerialPort cardReadSerialPort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_read);
        tv_read_num = (TextView) findViewById(R.id.tv_read_num);
        btn_click = (Button)findViewById(R.id.btn_click);

        cardReadSerialPort = new CardReadSerialPort();

        cardReadSerialPort.setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(final String IDNUM) {
                Log.e("test", IDNUM);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_read_num.setText("test" + IDNUM.replace(" ",""));
                        cardReadSerialPort.closeReadSerial();
                        cardReadSerialPort = null;
                    }
                });

            }
        });

        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestReadActivity.this.finish();
            }
        });

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (cardReadSerialPort != null) {
            cardReadSerialPort.closeReadSerial();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardReadSerialPort != null) {
            cardReadSerialPort.closeReadSerial();
        }
    }
}
