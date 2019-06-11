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
 * 3. 还 输入货道号页面
 * Created by kjh08490 on 2016/11/25.
 */

public class ReturnInsertNumActivity extends BaseActivity {

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

        setTitle("借还");

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnInsertNumActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                ReturnInsertNumActivity.this.finish();
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
            }

            @Override
            public void onFailure(Call<SrvResult<List<MRoad>>> call, Throwable throwable) {
            }
        });

    }


    /**
     * @param keyPassage
     */
    private void checkPassageDirectUser(String keyPassage) {

        // 检查用户输入的货道号的校验
        if (TextUtils.isEmpty(keyPassage)) {
            Toast.makeText(ReturnInsertNumActivity.this, "请输入货道号。", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(ReturnInsertNumActivity.this, "提示：当前货道暂未启用。", Toast.LENGTH_SHORT).show();
        } else {
            // 货道实体
            if ("1".equals(mRoad.getCabType()) && mRoad.getQty() == 0) {
                // 格子柜 & 库存 ＝＝ 0 & 还操作
                Intent intent = new Intent(ReturnInsertNumActivity.this, ReturnCardReadActivity.class);
                intent.putExtra(SeekerSoftConstant.PASSAGE, mRoad);
                startActivity(intent);
                this.finish();
            } else if ("1".equals(mRoad.getCabType()) && mRoad.getQty() > 0) {
                // 格子柜 & 库存 > 0 & 还操作
                Toast.makeText(ReturnInsertNumActivity.this, "提示：当前货道已经被还好。", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ReturnInsertNumActivity.this, "货道暂不能进行出借，请联系管理员。", Toast.LENGTH_SHORT).show();
            }
        }

    }

}
