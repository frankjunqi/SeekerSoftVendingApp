package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekWorkService;
import com.seekersoftvendingapp.network.api.SrvResult;
import com.seekersoftvendingapp.network.entity.seekwork.MRoad;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.LogCat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.view.KeyBordView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 1. 取货 输入货道号页面
 * Created by kjh08490 on 2016/11/25.
 */

public class TakeOutInsertNumActivity extends BaseActivity {

    private LinearLayout ll_keyboard;
    private KeyBordView keyBordView;
    private List<MRoad> list;
    private MRoad mRoad = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertgoodsnum);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("取货");

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeOutInsertNumActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                TakeOutInsertNumActivity.this.finish();
            }
        });

        ll_keyboard = (LinearLayout) findViewById(R.id.ll_keyboard);
        keyBordView = new KeyBordView(this);
        keyBordView.setSureClickListen(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassageDirectUser(keyBordView.getKeyBoradStr());
            }
        });
        ll_keyboard.addView(keyBordView);

        queryRoad();

    }


    // 查询货道信息
    private void queryRoad() {
        showProgress();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<List<MRoad>>> mRoadAction = service.queryRoad(SeekerSoftConstant.machine);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<List<MRoad>>>() {
            @Override
            public void onResponse(Call<SrvResult<List<MRoad>>> call, Response<SrvResult<List<MRoad>>> response) {
                if (response != null && response.body() != null && response.body().getData() != null && response.body().getData().size() > 0) {
                    // 成功逻辑
                    list = response.body().getData();
                    // 可以让输入货道可点击
                    countDownTimer.start();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<SrvResult<List<MRoad>>> call, Throwable throwable) {
                // 异常
                Toast.makeText(TakeOutInsertNumActivity.this, "提示：网络异常。", Toast.LENGTH_LONG).show();
                hideProgress();
            }
        });

    }

    private void checkPassageDirectUser(String keyPassage) {
        // 检查用户输入的货道号的校验
        if (TextUtils.isEmpty(keyPassage)) {
            // 检验
            Toast.makeText(TakeOutInsertNumActivity.this, "提示：请输入货道号", Toast.LENGTH_LONG).show();
            return;
        }

        for (int i = 0; list != null && i < list.size(); i++) {
            if (!TextUtils.isEmpty(keyPassage) && keyPassage.equals(list.get(i).getBorderRoad())) {
                mRoad = list.get(i);
                break;
            }
        }

        if (mRoad == null) {
            // 没有货道提示
            Toast.makeText(TakeOutInsertNumActivity.this, "提示：当前货道暂未启用。", Toast.LENGTH_SHORT).show();
        } else {
            // 货道实体
            Intent intent = null;
            if ("1".equals(mRoad.getCabType()) && mRoad.getQty() == 0) {
                // 格子柜 & 库存 ＝＝ 0 & 借操作
                // 调用查询被谁借走的接口
                getLastBorrowName();
            } else if ("1".equals(mRoad.getCabType()) && mRoad.getQty() > 0) {
                // 格子柜 & 库存 > 0 & 还操作
                Toast.makeText(TakeOutInsertNumActivity.this, "提示：此格子柜中物品已经归还。", Toast.LENGTH_SHORT).show();
            } else if (mRoad.getQty() == 0) {
                Toast.makeText(TakeOutInsertNumActivity.this, "提示：当前货道暂无库存。", Toast.LENGTH_SHORT).show();
            } else {
                if ("1".equals(mRoad.getCabType())) {
                    // 格子柜 消费
                    intent = new Intent(TakeOutInsertNumActivity.this, TakeOutCardReadActivity.class);
                } else {
                    intent = new Intent(TakeOutInsertNumActivity.this, TakeOutNumActivity.class);
                }
                intent.putExtra(SeekerSoftConstant.PASSAGE, mRoad);
                intent.putExtra(SeekerSoftConstant.TakeoutNum, 1);
                startActivity(intent);
                this.finish();
            }
        }
    }


    // 此货道被谁借走
    private void getLastBorrowName() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<String>> mRoadAction = service.getLastBorrowName(SeekerSoftConstant.machine, mRoad.getCabNo(), mRoad.getRoadCode());
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<String>>() {
            @Override
            public void onResponse(Call<SrvResult<String>> call, Response<SrvResult<String>> response) {
                // 提示 是谁借走 此货柜的货物
                if (response != null && response.body() != null) {
                    Toast.makeText(TakeOutInsertNumActivity.this, "提示：当前货道已借出，已被 " + response.body().getData() + "借出。", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TakeOutInsertNumActivity.this, "提示：查询最后一次借出信息失败。", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SrvResult<String>> call, Throwable throwable) {
            }
        });
    }

}