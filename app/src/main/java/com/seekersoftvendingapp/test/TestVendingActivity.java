package com.seekersoftvendingapp.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seekersoftvendingapp.R;
import com.seekersoftvendingapp.serialport.VendingSerialPort;

/**
 * Created by Frank on 16/11/20.
 */

public class TestVendingActivity extends AppCompatActivity implements View.OnClickListener {

    private String TAG = TestVendingActivity.class.getSimpleName();
    private EditText et_col, et_row;
    private Button btn_out;
    private TextView tv_showdata;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_vending);
        et_col = (EditText) findViewById(R.id.et_col);
        et_row = (EditText) findViewById(R.id.et_row);

        tv_showdata = (TextView) findViewById(R.id.tv_showdata);

        btn_out = (Button) findViewById(R.id.btn_out);
        btn_out.setOnClickListener(this);

        VendingSerialPort.getInstance();

        VendingSerialPort.getInstance().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
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
                String cmd = cmdOpenVender(Integer.parseInt(et_col.getText().toString()), Integer.parseInt(et_row.getText().toString()));
                tv_showdata.setText(cmd);
                VendingSerialPort.getInstance().sendBuffer(VendingSerialPort.HexToByteArr(cmd));
                break;
        }
    }

    /**
     * 业务层面的参数拼接
     *
     * @param col 列号
     * @param row 行号
     * @return
     */
    public static String cmdOpenVender(int col, int row) {
        return getVenderCommand("53" + String.format("%02x%02x000000", new Object[]{Integer.valueOf(col + 48), Integer.valueOf(row + 48)}));
    }

    /**
     * 获取打开螺纹柜的串口命令
     *
     * @param cmd
     * @return
     */
    public static String getVenderCommand(String cmd) {
        String cmdString = cmd.replaceAll("\\s*", "");
        return ("AA" + cmdString + Integer.toHexString(getBCC(cmdString)) + "AC").toUpperCase();
    }

    /**
     * 获取校验位
     *
     * @param cmd 原始命令
     * @return
     */
    public static int getBCC(String cmd) {
        int bcc = 0;
        for (int i = 1; i <= cmd.length() / 2; i++) {
            bcc ^= Integer.parseInt(cmd.substring((i * 2) - 2, i * 2), 16);
        }
        return bcc;
    }

}
