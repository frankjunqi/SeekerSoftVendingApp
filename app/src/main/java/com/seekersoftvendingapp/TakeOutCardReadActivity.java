package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.network.entity.seekwork.MRoad;
import com.seekersoftvendingapp.newtakeoutserial.CardReadSerialPort;
import com.seekersoftvendingapp.util.SeekerSoftConstant;


/**
 * 1. 取货 读卡 页面
 * Created by kjh08490 on 2016/11/25.
 */

public class TakeOutCardReadActivity extends BaseActivity {

    private RelativeLayout ll_keyboard;
    private EditText et_getcard;

    private MRoad passage;
    private int number = 1;
    private String cardId = "";
    private TextView tv_upup;

    private CardReadSerialPort cardReadSerialPort;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardread);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("刷卡确认");

        passage = (MRoad) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        number = getIntent().getIntExtra(SeekerSoftConstant.TakeoutNum, 1);

        ll_keyboard = (RelativeLayout) findViewById(R.id.ll_keyboard);
        ll_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeOutCardReadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                TakeOutCardReadActivity.this.finish();
            }
        });

        et_getcard = (EditText) findViewById(R.id.et_getcard);

        et_getcard.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (KeyEvent.KEYCODE_ENTER == i && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
                    cardId = et_getcard.getText().toString().replace("\n", "");
                    if (TextUtils.isEmpty(cardId)) {
                        // 读到的卡号为null or ""
                        Toast.makeText(TakeOutCardReadActivity.this, "卡号为空,请重新读卡...", Toast.LENGTH_SHORT).show();
                    } else {
                        // 处理业务
                        gotoResult();
                    }
                    return true;
                }
                return false;
            }
        });

        countDownTimer.start();

        tv_upup = (TextView) findViewById(R.id.tv_upup);
        tv_upup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        cardReadSerialPort = new CardReadSerialPort();
        cardReadSerialPort.setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(final String IDNUM) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_getcard.setText(IDNUM);
                        cardId = et_getcard.getText().toString().replace("\n", "");
                        if (TextUtils.isEmpty(cardId)) {
                            // 读到的卡号为null or ""
                            Toast.makeText(TakeOutCardReadActivity.this, "卡号为空,请重新读卡...", Toast.LENGTH_SHORT).show();
                        } else {
                            // 处理业务
                            gotoResult();
                        }
                    }
                });
            }

        });

    }


    public void gotoResult() {
        Intent intent = new Intent(TakeOutCardReadActivity.this, HandleTakeOutResultActivity.class);
        intent.putExtra(SeekerSoftConstant.PASSAGE, passage);
        intent.putExtra(SeekerSoftConstant.TakeoutNum, number);
        intent.putExtra(SeekerSoftConstant.CardNum, cardId);
        startActivity(intent);
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cardReadSerialPort != null) {
            cardReadSerialPort.closeReadSerial();
            cardReadSerialPort = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cardReadSerialPort != null) {
            cardReadSerialPort.closeReadSerial();
            cardReadSerialPort = null;
        }
    }
}
