package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.util.TakeOutError;

/**
 * 成功 失败 页面（包括成功，失败描述信息页面） 共用
 * Created by kjh08490 on 2016/11/25.
 */

public class HandleResultActivity extends BaseActivity {

    private TextView tv_handle_result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handleresult);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("取货结果");

        tv_handle_result = (TextView) findViewById(R.id.tv_handle_result);

        TakeOutError takeOutError = (TakeOutError) getIntent().getSerializableExtra(SeekerSoftConstant.TAKEOUTERROR);
        if (takeOutError != null) {
            if (TextUtils.isEmpty(takeOutError.serverMsg)) {
                tv_handle_result.setText("服务器检测：" + takeOutError.getTakeOutMsg());
            } else {
                tv_handle_result.setText("本地检测：" + takeOutError.serverMsg);
            }
        }
        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HandleResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        countDownTimer.start();
    }

    @Override
    public int setEndTime() {
        return SeekerSoftConstant.ENDTIEMSHORT;
    }
}
