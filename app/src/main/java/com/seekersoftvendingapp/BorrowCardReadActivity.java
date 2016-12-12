package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.borrow.BorrowResBody;
import com.seekersoftvendingapp.network.entity.borrow.BorrowSuccessResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 2. 借 读卡 页面
 * Created by kjh08490 on 2016/11/25.
 */

public class BorrowCardReadActivity extends AppCompatActivity {

    private Button btn_return_goods;
    private Button btn_return_mainpage;

    // 货道的产品
    private String productId = "";
    private String pasageId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardread);
        productId = getIntent().getStringExtra(SeekerSoftConstant.PRODUCTID);
        pasageId = getIntent().getStringExtra(SeekerSoftConstant.PASSAGEID);
        btn_return_goods = (Button) findViewById(R.id.btn_return_goods);
        btn_return_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTakeOutPro(SeekerSoftConstant.CARDID);
            }
        });
        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BorrowCardReadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // TODO 打开串口读卡器  -- 串口读到数据后关闭串口 -- 判断能否进行借接口
        // Open Serial Port Codeing Here

    }

    /**
     * （接口）判断是否能借
     */
    private void isTakeOutPro(String cardId) {
        // 加载前
        // do something

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<BorrowResBody> updateAction = service.borrow(SeekerSoftConstant.DEVICEID, cardId, pasageId);
        updateAction.enqueue(new Callback<BorrowResBody>() {
            @Override
            public void onResponse(Call<BorrowResBody> call, Response<BorrowResBody> response) {
                if (response != null && response.body() != null && response.body().data.result) {
                    Toast.makeText(BorrowCardReadActivity.this, "可以借,true", Toast.LENGTH_LONG).show();
                    // TODO 本地数据库进行借还的消耗


                    // TODO 本地数据库借还消费记录 默认提交到服务端的falg为 fasle


                    // TODO 串口 柜子 打开passageID


                    // TODO 提交成功接口
                    borrowSuccess(response.body().data.objectId);

                    // TODO 到取货成功页面
                    startActivity(new Intent(BorrowCardReadActivity.this, HandleResultActivity.class));

                } else {
                    Toast.makeText(BorrowCardReadActivity.this, "不可以借,false", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BorrowResBody> call, Throwable throwable) {
                Toast.makeText(BorrowCardReadActivity.this, "basedate :  Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * （接口）借成功的通知接口
     */
    private void borrowSuccess(String borrowObjectId) {
        // 加载前
        // do something

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<BorrowSuccessResBody> updateAction = service.borrowSuccess(borrowObjectId);
        updateAction.enqueue(new Callback<BorrowSuccessResBody>() {
            @Override
            public void onResponse(Call<BorrowSuccessResBody> call, Response<BorrowSuccessResBody> response) {
                if (response != null && response.body() != null && response.body().data) {
                    Toast.makeText(BorrowCardReadActivity.this, "出货成功标识提交服务端成功,true", Toast.LENGTH_LONG).show();
                    // TODO 本地数据库消费记录 默认提交到服务端的falg为 true


                } else {
                    // TODO DO Nothing
                    Toast.makeText(BorrowCardReadActivity.this, "提交失败,false", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BorrowSuccessResBody> call, Throwable throwable) {
                // TODO DO Nothing
                Toast.makeText(BorrowCardReadActivity.this, "网络问题", Toast.LENGTH_LONG).show();
            }
        });
    }

}
