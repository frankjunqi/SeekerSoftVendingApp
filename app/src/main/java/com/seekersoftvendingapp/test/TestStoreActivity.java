package com.seekersoftvendingapp.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seekersoftvendingapp.R;
import com.seekersoftvendingapp.serialport.StoreSerialPort;

/**
 * Created by Frank on 16/11/20.
 */

public class TestStoreActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = TestStoreActivity.class.getSimpleName();
    private EditText et_line, et_column, et_height;
    private Button btn_out, btn_check;
    private TextView tv_showdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_store);
        et_line = (EditText) findViewById(R.id.et_line);
        et_column = (EditText) findViewById(R.id.et_column);
        et_height = (EditText) findViewById(R.id.et_height);


        tv_showdata = (TextView) findViewById(R.id.tv_showdata);

        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(this);
        btn_check = (Button) findViewById(R.id.btn_check);
        btn_check.setOnClickListener(this);
        //StoreSerialPort.getInstance();

        /*StoreSerialPort.getInstance().setOnDataReceiveListener(new StoreSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {

            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {
                Log.e(TAG, "length is:" + size + ",data is:" + new String(buffer, 0, size));
            }
        });*/

    }

    @Override
    public void onClick(View v) {
        int line = 1;
        int column = 1;
        int height = 3;
        try {
            line = Integer.valueOf(et_line.getText().toString());
            column = Integer.valueOf(et_column.getText().toString());
            height = Integer.valueOf(et_height.getText().toString());
        } catch (Exception e) {

        }
        switch (v.getId()) {
            case R.id.btn_out:
                // 柜子
                //StoreSerialPort.getInstance().sendBuffer(StoreSerialPort.HexToByteArr(cmdOpenStoreDoor(0, 0, 16)));
                String storeCmd = cmdOpenStoreDoor(line, column, height);
                tv_showdata.setText("打开 {line=" + line + "}{column=" + column + "}{height=" + height + "} CMD：" + storeCmd);
                Log.e("TestStoreActivity", "打开 {line=" + line + "}{column=" + column + "}{height=" + height + "} CMD：" + storeCmd);
                break;

            case R.id.btn_check:
                String checkCmd = cmdCheckStoreDoor(line, column);
                tv_showdata.setText("Check {type=" + line + "}{number=" + column + "} CMD：" + checkCmd);
                Log.e("TestStoreActivity", "Check {type=" + line + "}{number=" + column + "} CMD：" + checkCmd);
                break;
        }
    }


    public static String cmdCheckStoreDoor(int type, int number) {
        return getStoreCommand(new StringBuilder(
                String.valueOf(String.format("%04x", new Object[]{Integer.valueOf(type)})))
                .append(String.format("%04x", new Object[]{Integer.valueOf(number)}))
                .append("0002000100").toString());
    }

    public static String cmdOpenStoreDoor(int type, int number, int door) {
        return getStoreCommand(
                new StringBuilder(
                        String.valueOf(
                                new StringBuilder(String.valueOf(String.format("%04x", new Object[]{Integer.valueOf(type)})))
                                        .append(String.format("%04x", new Object[]{Integer.valueOf(number)}))
                                        .append("00010001").toString()))
                        .append(String.format("%02x", new Object[]{Integer.valueOf(door)})).toString());
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
