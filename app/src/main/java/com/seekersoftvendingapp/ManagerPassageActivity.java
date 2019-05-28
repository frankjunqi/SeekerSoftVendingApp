package com.seekersoftvendingapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekWorkService;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.api.SrvResult;
import com.seekersoftvendingapp.network.entity.seekwork.MRoad;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordObj;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordReqBody;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.DataFormat;
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

    private TextView tv_modify_down;

    private ManagerPassageAdapter managerPassageAdapter;


    private List<MRoad> list;
    private ArrayList<MRoad> zhuList = new ArrayList<>();

    private ArrayList<MRoad> aList = new ArrayList<>();

    private ArrayList<MRoad> bList = new ArrayList<>();

    private ArrayList<MRoad> cList = new ArrayList<>();


    private int flag = 0;
    // 单货道补货数量
    private int selecteStock = 0;
    private ManagerPassageAdapter.OnItemClickListener itemClickListener = new ManagerPassageAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            Toast.makeText(ManagerPassageActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();

            final MRoad passage = list.get(position);

            // 最大库存
            int capacity = passage.getCapacity();
            // 当前库存
            int currentStock = passage.getStock();
            // 可以补货的数量
            int canSupply = capacity - currentStock;
            // 数据做校验
            if (canSupply <= 0) {
                Toast.makeText(ManagerPassageActivity.this, "已经是最大库存数量", Toast.LENGTH_SHORT).show();
                return;
            }
            final String[] intlist = new String[canSupply];
            for (int i = 0; i < canSupply; i++) {
                intlist[i] = String.valueOf(i + 1);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(ManagerPassageActivity.this);
            builder.setTitle("货道补货: "+passage.getSeqNo());
            /**
             * 1、public Builder setItems(int itemsId, final OnClickListener
             * listener) itemsId表示字符串数组的资源ID，该资源指定的数组会显示在列表中。 2、public Builder
             * setItems(CharSequence[] items, final OnClickListener listener)
             * items表示用于显示在列表中的字符串数组
             */

            builder.setSingleChoiceItems(intlist, currentStock - 1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    selecteStock = which + 1;
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (selecteStock == 0) {
                        Toast.makeText(ManagerPassageActivity.this, "请选择补货数量.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    passage.setKeeptwo(String.valueOf(selecteStock));
                    // 重置
                    selecteStock = 0;
                    asyncSupplyRecordRequest(passage);
                }
            });
            builder.setNegativeButton("取消", null).show();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_passage);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("补货差异");

        setRightTitle("保 存", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncSupplyRecordRequest(managerPassageAdapter.getPassageList());
            }
        });

        tv_modify_down = (TextView) findViewById(R.id.tv_modify_down);
        tv_modify_down.setBackground(null);

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

        // 默认主柜信息列表
        managerPassageAdapter.setOnItemClickListener(itemClickListener);
    }

    /**
     * 提交补货记录 POST
     */
    private void asyncSupplyRecordRequest(final List<Passage> passageList) {
        if (passageList == null && passageList.size() == 0) {
            Toast.makeText(ManagerPassageActivity.this, "补货条数为 0 ! ", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress();
        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        SupplyRecordReqBody supplyRecordReqBody = new SupplyRecordReqBody();
        supplyRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        // 处理补货记录

        for (Passage passage : passageList) {
            SupplyRecordObj supplyRecordObj = new SupplyRecordObj();
            supplyRecordObj.passage = (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo();
            supplyRecordObj.card = SeekerSoftConstant.ADMINCARD;
            int count = 0;
            if (passage.getCapacity() - passage.getStock() <= 0) {
                count = 0;
            } else {
                count = passage.getCapacity() - passage.getStock() - Integer.parseInt(passage.getKeeptwo());
            }
            // 设置库存
            passage.setStock(passage.getStock() + count);

            supplyRecordObj.time = DataFormat.getNowTime();
            supplyRecordObj.count = count;

            // 过滤补货count==0的的情况
            if (count == 0) {
                continue;
            } else {
                supplyRecordReqBody.record.add(supplyRecordObj);
            }
        }

        Gson gson = new Gson();
        String josn = gson.toJson(supplyRecordReqBody);
        LogCat.e("supplyRecord = " + josn);

        Call<SupplyRecordResBody> postAction = service.supplyRecord(supplyRecordReqBody);
        postAction.enqueue(new Callback<SupplyRecordResBody>() {
            @Override
            public void onResponse(Call<SupplyRecordResBody> call, Response<SupplyRecordResBody> response) {
                if (response != null && response.body() != null) {
                    passageDao.insertOrReplaceInTx(passageList);
                    updatePassageList();
                    if (flag == 0) {
                        managerPassageAdapter.setPassageList(passageListMain);
                    } else if (flag == 1) {
                        managerPassageAdapter.setPassageList(passageListA);
                    } else if (flag == 2) {
                        managerPassageAdapter.setPassageList(passageListB);
                    } else if (flag == 3) {
                        managerPassageAdapter.setPassageList(passageListC);
                    }
                    Toast.makeText(ManagerPassageActivity.this, "补货成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagerPassageActivity.this, "supply Record: Failure", Toast.LENGTH_SHORT).show();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<SupplyRecordResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(ManagerPassageActivity.this, "supply Record:  Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void asyncSupplyRecordRequest(final Passage passageA) {

        final ArrayList<Passage> passageList = new ArrayList<>();
        passageList.add(passageA);

        if (passageList == null && passageList.size() == 0) {
            Toast.makeText(ManagerPassageActivity.this, "补货条数为 0 ! ", Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress();
        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        SupplyRecordReqBody supplyRecordReqBody = new SupplyRecordReqBody();
        supplyRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        // 处理补货记录

        for (Passage passage : passageList) {
            SupplyRecordObj supplyRecordObj = new SupplyRecordObj();
            supplyRecordObj.passage = (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo();
            supplyRecordObj.card = SeekerSoftConstant.ADMINCARD;
            int count = 0;
            if (passage.getCapacity() - passage.getStock() <= 0) {
                count = 0;
            } else {
                count = passage.getCapacity() - passage.getStock() - Integer.parseInt(passage.getKeeptwo());
            }
            // 设置库存
            passage.setStock(passage.getStock() + count);

            supplyRecordObj.time = DataFormat.getNowTime();
            supplyRecordObj.count = count;

            // 过滤补货count==0的的情况
            if (count == 0) {
                continue;
            } else {
                supplyRecordReqBody.record.add(supplyRecordObj);
            }
        }

        Gson gson = new Gson();
        String josn = gson.toJson(supplyRecordReqBody);
        LogCat.e("supplyRecord = " + josn);

        Call<SupplyRecordResBody> postAction = service.supplyRecord(supplyRecordReqBody);
        postAction.enqueue(new Callback<SupplyRecordResBody>() {
            @Override
            public void onResponse(Call<SupplyRecordResBody> call, Response<SupplyRecordResBody> response) {
                if (response != null && response.body() != null) {
                    passageDao.insertOrReplaceInTx(passageList);
                    updatePassageList();
                    if (flag == 0) {
                        managerPassageAdapter.setPassageList(passageListMain);
                    } else if (flag == 1) {
                        managerPassageAdapter.setPassageList(passageListA);
                    } else if (flag == 2) {
                        managerPassageAdapter.setPassageList(passageListB);
                    } else if (flag == 3) {
                        managerPassageAdapter.setPassageList(passageListC);
                    }
                    Toast.makeText(ManagerPassageActivity.this, "补货成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagerPassageActivity.this, "supply Record: Failure", Toast.LENGTH_SHORT).show();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<SupplyRecordResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(ManagerPassageActivity.this, "supply Record:  Error", Toast.LENGTH_LONG).show();
            }
        });
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main:
                flag = 0;
                managerPassageAdapter.setPassageList(zhuList);
                managerPassageAdapter.setOnItemClickListener(itemClickListener);
                break;
            case R.id.btn_a:
                flag = 1;
                managerPassageAdapter.setPassageList(aList);
                managerPassageAdapter.setOnItemClickListener(null);
                break;
            case R.id.btn_b:
                flag = 2;
                managerPassageAdapter.setPassageList(bList);
                managerPassageAdapter.setOnItemClickListener(null);
                break;
            case R.id.btn_c:
                flag = 3;
                managerPassageAdapter.setPassageList(cList);
                managerPassageAdapter.setOnItemClickListener(null);
                break;
            case R.id.btn_return_mainpage:
                this.finish();
                return;
        }
    }
}
