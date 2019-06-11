package com.seekersoftvendingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekWorkService;
import com.seekersoftvendingapp.network.api.SrvResult;
import com.seekersoftvendingapp.network.entity.seekwork.MNum;
import com.seekersoftvendingapp.network.entity.seekwork.MReplenish;
import com.seekersoftvendingapp.network.entity.seekwork.MRoad;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.LogCat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by kjh08490 on 2016/12/15.
 */

public class ManagerPassageActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_main, btn_a, btn_b, btn_c, btn_return_mainpage;
    private EmptyRecyclerView recyclerView;
    private RelativeLayout rl_empty;

    private int currentFlag = 0;

    private ManagerPassageAdapter managerPassageAdapter;

    private List<MRoad> list;
    private ArrayList<MRoad> zhuList = new ArrayList<>();
    private ArrayList<MRoad> aList = new ArrayList<>();
    private ArrayList<MRoad> bList = new ArrayList<>();
    private ArrayList<MRoad> cList = new ArrayList<>();

    private int flag = 0;

    private String cardNo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_passage);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("补货差异");

        cardNo = getIntent().getStringExtra("cardNo");

        setRightTitle("保 存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushReplian();
            }
        });

        rl_empty = (RelativeLayout) findViewById(R.id.rl_empty);

        btn_main = (Button) findViewById(R.id.btn_main);
        btn_main.setOnClickListener(this);
        btn_a = (Button) findViewById(R.id.btn_a);
        btn_a.setOnClickListener(this);
        btn_b = (Button) findViewById(R.id.btn_b);
        btn_b.setOnClickListener(this);
        btn_c = (Button) findViewById(R.id.btn_c);
        btn_c.setOnClickListener(this);

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(this);

        recyclerView = (EmptyRecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        managerPassageAdapter = new ManagerPassageAdapter(ManagerPassageActivity.this);
        recyclerView.setAdapter(managerPassageAdapter);
        recyclerView.setEmptyView(rl_empty);

        getProList();

    }

    private void getProList() {
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
                    zhuList.clear();
                    aList.clear();
                    bList.clear();
                    cList.clear();

                    for (int i = 0; i < list.size(); i++) {
                        String cabNo = list.get(i).getCabNo();
                        if ("主柜".equals(cabNo)) {
                            zhuList.add(list.get(i));
                        } else if ("A".equals(cabNo)) {
                            aList.add(list.get(i));
                        } else if ("B".equals(cabNo)) {
                            bList.add(list.get(i));
                        } else if ("C".equals(cabNo)) {
                            cList.add(list.get(i));
                        }
                    }

                    managerPassageAdapter.setPassageList(zhuList);

                } else {
                    // 无数据
                    Toast.makeText(ManagerPassageActivity.this, "提示：此货柜没有配置货道信息，不可进行任何操作。", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SrvResult<List<MRoad>>> call, Throwable throwable) {
                // 异常
                Toast.makeText(ManagerPassageActivity.this, "提示：网络异常。", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void pushReplian() {

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);

        MReplenish mReplenish = new MReplenish();
        mReplenish.setCardNo(cardNo);
        mReplenish.setMachineCode(SeekerSoftConstant.machine);
        List<MNum> RodeList = new ArrayList<>();

        if (flag == 0) {
            // 主柜
            for (int i = 0; i < zhuList.size(); i++) {
                MNum mNum = new MNum();
                mNum.setNo(zhuList.get(i).getNo());
                mNum.setQty(zhuList.get(i).getChaLackNum());
                mNum.setRoadCode(zhuList.get(i).getRoadCode());
                RodeList.add(mNum);
            }

        } else if (flag == 1) {
            // A
            for (int i = 0; i < aList.size(); i++) {
                MNum mNum = new MNum();
                mNum.setNo(aList.get(i).getNo());
                mNum.setQty(aList.get(i).getChaLackNum());
                mNum.setRoadCode(aList.get(i).getRoadCode());
                RodeList.add(mNum);
            }
        } else if (flag == 2) {
            // B
            for (int i = 0; i < bList.size(); i++) {
                MNum mNum = new MNum();
                mNum.setNo(bList.get(i).getNo());
                mNum.setQty(bList.get(i).getChaLackNum());
                mNum.setRoadCode(bList.get(i).getRoadCode());
                RodeList.add(mNum);
            }
        } else if (flag == 3) {
            // C
            for (int i = 0; i < cList.size(); i++) {
                MNum mNum = new MNum();
                mNum.setNo(cList.get(i).getNo());
                mNum.setQty(cList.get(i).getChaLackNum());
                mNum.setRoadCode(cList.get(i).getRoadCode());
                RodeList.add(mNum);
            }
        }

        mReplenish.setRodeList(RodeList);

        Call<SrvResult<Boolean>> mRoadAction = service.replenish(mReplenish);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getData() != null && response.body().getData()) {
                    Toast.makeText(ManagerPassageActivity.this, "提示：补货成功。", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ManagerPassageActivity.this, "提示：补货失败。", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                Toast.makeText(ManagerPassageActivity.this, "提示：网络异常。", Toast.LENGTH_LONG).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main:
                currentFlag = 0;
                managerPassageAdapter.setPassageList(zhuList);
                break;
            case R.id.btn_a:
                currentFlag = 1;
                managerPassageAdapter.setPassageList(aList);
                break;
            case R.id.btn_b:
                currentFlag = 2;
                managerPassageAdapter.setPassageList(bList);
                break;
            case R.id.btn_c:
                currentFlag = 3;
                managerPassageAdapter.setPassageList(cList);
                break;
            case R.id.btn_return_mainpage:
                this.finish();
                return;
        }
    }
}
