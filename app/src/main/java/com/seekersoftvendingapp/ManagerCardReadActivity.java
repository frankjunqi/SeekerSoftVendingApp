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

import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekWorkService;
import com.seekersoftvendingapp.network.api.SrvResult;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.newtakeoutserial.CardReadSerialPort;
import com.seekersoftvendingapp.util.LogCat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 4. 管理员 读卡 页面
 * Created by kjh08490 on 2016/11/25.
 */

public class ManagerCardReadActivity extends BaseActivity {

    private EditText et_getcard;
    private RelativeLayout ll_keyboard;
    private TextView tv_upup;

    private CardReadSerialPort cardReadSerialPort;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_cardread);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("刷卡确认");

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
                Intent intent = new Intent(ManagerCardReadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ManagerCardReadActivity.this.finish();
            }
        });

        et_getcard = (EditText) findViewById(R.id.et_getcard);


        et_getcard.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (KeyEvent.KEYCODE_ENTER == i && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
                    String cardId = et_getcard.getText().toString().replace("\n", "");
                    // 处理业务
                    loginValidate(cardId);
                    return true;
                }
                return false;
            }
        });

        tv_upup = (TextView) findViewById(R.id.tv_upup);
        tv_upup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });

        countDownTimer.start();

        cardReadSerialPort = new CardReadSerialPort();
        cardReadSerialPort.setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(final String IDNUM) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        et_getcard.setText(IDNUM);
                        String cardId = et_getcard.getText().toString().replace("\r\n", "");
                        // 处理业务
                        loginValidate(cardId);
                    }
                });
            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {

            }
        });
    }

    private void loginValidate(final String cardNo) {

        if (TextUtils.isEmpty(cardNo)) {
            Toast.makeText(ManagerCardReadActivity.this, "卡号为空，请重新刷卡。", Toast.LENGTH_SHORT).show();
            return;
        }

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> updateAction = service.loginValidate(SeekerSoftConstant.machine, cardNo);
        LogCat.e("url = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getStatus() == 1 && response.body().getData()) {
                    // 打开管理员页面
                    Intent intent = new Intent(ManagerCardReadActivity.this, ManagerGoodsActivity.class);
                    intent.putExtra("cardNo", cardNo);
                    startActivity(intent);
                    ManagerCardReadActivity.this.finish();
                } else {
                    // 不是管理员卡，不需要做任何操作
                    Toast.makeText(ManagerCardReadActivity.this, "提示信息：" + response.body().getMsg(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                et_getcard.setText("");
                // 此人不是管理员则提示他不是管理员，并且重新打开串口
                Toast.makeText(ManagerCardReadActivity.this, "此卡:" + cardNo + ". 不是管理卡,请您换管理卡，重新刷卡...", Toast.LENGTH_SHORT).show();
            }
        });

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
