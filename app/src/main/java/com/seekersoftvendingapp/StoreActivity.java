package com.seekersoftvendingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seekersoftvendingapp.serialport.StoreSerialPort;

/**
 * Created by Frank on 16/11/20.
 */

public class StoreActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = StoreActivity.class.getSimpleName();
    private EditText et_col, et_row;
    private Button btn_out;
    private TextView tv_showdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vending);
        et_col = (EditText) findViewById(R.id.et_col);
        et_row = (EditText) findViewById(R.id.et_row);

        tv_showdata = (TextView) findViewById(R.id.tv_showdata);

        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(this);

        StoreSerialPort.getInstance();

        StoreSerialPort.getInstance().setOnDataReceiveListener(new StoreSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {

            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {
                Log.e(TAG, "length is:" + size + ",data is:" + new String(buffer, 0, size));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_out:
                // 柜子
                StoreSerialPort.getInstance().sendBuffer(StoreSerialPort.HexToByteArr(cmdOpenStoreDoor(0, 0, 16)));

                break;
        }
    }


    // openStore(ConvertHelper.toInt(this.vendingChn.getVc1LineNum(), Integer.valueOf(0)).intValue(), ConvertHelper.toInt(this.vendingChn.getVc1ColumnNum(), Integer.valueOf(0)).intValue(), ConvertHelper.toInt(this.vendingChn.getVc1Height(), Integer.valueOf(0)).intValue());

    public static String cmdOpenStoreDoor(int type, int number, int door) {
        return getStoreCommand(new StringBuilder(String.valueOf(new StringBuilder(String.valueOf(String.format("%04x", new Object[]{Integer.valueOf(type)}))).append(String.format("%04x", new Object[]{Integer.valueOf(number)})).append("00 01 00 01").toString())).append(String.format("%02x", new Object[]{Integer.valueOf(door)})).toString());
    }

    public static String getStoreCommand(String cmd) {
        return (cmd.replaceAll("\\s*", "") + String.format("%04x", new Object[]{Integer.valueOf(getSum(cmd))})).toUpperCase();
    }

    public static int getSum(String cmd) {
        int sum = 0;
        String inHex = cmd.replaceAll("\\s*", "");
        for (int i = 1; i <= cmd.length() / 2; i++) {
            sum += Integer.parseInt(inHex.substring((i * 2) - 2, i * 2), 16);
        }
        return sum;
    }

}
