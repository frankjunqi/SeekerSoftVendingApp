package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

/**
 * Created by kjh08490 on 2016/11/25.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_getproduct, btn_borrow, btn_back;
    private TextView btn_manage;

    private TextView tv_number, tv_phone, tv_versiondesc;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_getproduct = (Button) findViewById(R.id.btn_getproduct);
        btn_borrow = (Button) findViewById(R.id.btn_borrow);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_manage = (TextView) findViewById(R.id.btn_manage);

        tv_number = (TextView) findViewById(R.id.tv_number);
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        tv_versiondesc = (TextView) findViewById(R.id.tv_versiondesc);
        tv_number.setText(SeekerSoftConstant.machine);
        tv_phone.setText(SeekerSoftConstant.phoneDesc + ": " + SeekerSoftConstant.versionDesc);
        tv_versiondesc.setText("软件版本 1.0.0, 设备型号 " + SeekerSoftConstant.machine);

        btn_getproduct.setOnClickListener(this);
        btn_borrow.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_manage.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (tv_number != null) {
            tv_number.setText(SeekerSoftConstant.machine);
        }
        if (tv_phone != null) {
            tv_phone.setText(SeekerSoftConstant.phoneDesc + ": " + SeekerSoftConstant.versionDesc);
        }
        if (tv_versiondesc != null) {
            tv_versiondesc.setText("软件版本 1.0.0, 设备型号 " + SeekerSoftConstant.machine);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int exitFlag = intent.getIntExtra(SeekerSoftConstant.EXITAPP, 0);
        if (exitFlag == 1) {
            // 退出程序
            NewVendingSerialPort.SingleInit().closeSerialPort();
            this.finish();
        }
        // 更新说明
        if (tv_number != null) {
            tv_number.setText(SeekerSoftConstant.machine);
        }
        if (tv_phone != null) {
            tv_phone.setText(SeekerSoftConstant.phoneDesc);
        }
        if (tv_versiondesc != null) {
            tv_versiondesc.setText("软件版本 1.1.0, 设备型号 " + SeekerSoftConstant.versionDesc);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getproduct:
                getProduct();
                break;
            case R.id.btn_borrow:
                borrowProduct();
                break;
            case R.id.btn_back:
                backProduct();
                break;
            case R.id.btn_manage:
                manageProduct();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);//直接结束程序
    }

    /**
     * 取货
     */
    private void getProduct() {
        startActivity(new Intent(MainActivity.this, TakeOutInsertNumActivity.class));
    }

    /**
     * 借货
     */
    private void borrowProduct() {
        startActivity(new Intent(MainActivity.this, BorrowInsertNumActivity.class));
    }

    /**
     * 还货
     */
    private void backProduct() {
        startActivity(new Intent(MainActivity.this, ReturnInsertNumActivity.class));
    }

    /**
     * 管理货物
     */
    private void manageProduct() {
        startActivity(new Intent(MainActivity.this, ManagerCardReadActivity.class));
    }

}
