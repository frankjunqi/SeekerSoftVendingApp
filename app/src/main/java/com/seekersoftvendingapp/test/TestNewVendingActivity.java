package com.seekersoftvendingapp.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.seekersoftvendingapp.BaseActivity;
import com.seekersoftvendingapp.R;
import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.seekersoftvendingapp.newtakeoutserial.ShipmentObject;

/**
 * Created by Frank on 16/11/20.
 */

public class TestNewVendingActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = TestNewVendingActivity.class.getSimpleName();
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


    }

    private int count = 0;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_out:
                int col = Integer.parseInt(et_col.getText().toString());
                int row = Integer.parseInt(et_row.getText().toString());
                tv_showdata.setText(col + " " + row);
                ShipmentObject shipmentObject = new ShipmentObject();
                shipmentObject.proNum = col * 10 + row;
                shipmentObject.objectId = count++;

                NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject).setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                    @Override
                    public void onCmdCallBack(boolean isSuccess) {
                        tv_showdata.setText(tv_showdata.getText() + " -- " + isSuccess);
                    }
                });
                break;
        }
    }


}
