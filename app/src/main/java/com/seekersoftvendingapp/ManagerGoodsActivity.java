package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by kjh08490 on 2016/11/28.
 */

public class ManagerGoodsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_onekeyinsert;
    private Button btn_onebyoneinsert;
    private Button btn_exit;
    private Button btn_backtomain;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managergoods);

        btn_onekeyinsert = (Button) findViewById(R.id.btn_onekeyinsert);
        btn_onebyoneinsert = (Button) findViewById(R.id.btn_onebyoneinsert);
        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_backtomain = (Button) findViewById(R.id.btn_backtomain);

        btn_onekeyinsert.setOnClickListener(this);
        btn_onebyoneinsert.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_backtomain.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onekeyinsert:
                break;
            case R.id.btn_onebyoneinsert:
                break;
            case R.id.btn_exit:

                break;
            case R.id.btn_backtomain:
                Intent intent = new Intent(ManagerGoodsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}