package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.util.SeekerSoftConstant;

/**
 * 2,输入数量
 * Created by kjh08490 on 2016/11/25.
 */

public class TakeOutNumActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout ll_keyboard;
    private TextView tv_num;
    private Button btn_down;
    private Button btn_up;
    private Button btn_sure;

    private int number = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertnum);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("取货数量");

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeOutNumActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        countDownTimer.start();

        ll_keyboard = (LinearLayout) findViewById(R.id.ll_keyboard);
        tv_num = (TextView) findViewById(R.id.tv_num);
        btn_down = (Button) findViewById(R.id.btn_down);
        btn_up = (Button) findViewById(R.id.btn_up);
        btn_sure = (Button) findViewById(R.id.btn_sure);
        btn_sure.setOnClickListener(this);
        btn_down.setOnClickListener(this);
        btn_up.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_down:
                if (number == 1) {
                    Toast.makeText(TakeOutNumActivity.this, "数量必须 >= 1 .", Toast.LENGTH_SHORT).show();
                } else {
                    number = number - 1;
                    tv_num.setText(String.valueOf(number));
                }
                break;
            case R.id.btn_up:
                number = number + 1;
                tv_num.setText(String.valueOf(number));
                break;

            case R.id.btn_sure:
                Intent intent = new Intent(TakeOutNumActivity.this, TakeOutCardReadActivity.class);
                intent.putExtra(SeekerSoftConstant.PASSAGE, getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE));
                intent.putExtra(SeekerSoftConstant.TakeoutNum, number);
                startActivity(intent);
                this.finish();
                break;
        }
    }
}
