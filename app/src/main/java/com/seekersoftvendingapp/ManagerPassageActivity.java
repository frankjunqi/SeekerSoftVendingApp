package com.seekersoftvendingapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordObj;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordReqBody;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.DataFormat;
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

    private ManagerPassageAdapter managerPassageAdapter;

    private PassageDao passageDao;
    private List<Passage> passageListMain = new ArrayList<>();
    private List<Passage> passageListA = new ArrayList<>();
    private List<Passage> passageListB = new ArrayList<>();
    private List<Passage> passageListC = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_passage);
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

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        passageDao = daoSession.getPassageDao();

        recyclerView = (EmptyRecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        managerPassageAdapter = new ManagerPassageAdapter(new ManagerPassageAdapter.ManagerPassageClickListener() {
            @Override
            public void onNoteClick(int position) {
                Passage passage = managerPassageAdapter.getPassage(position);
                alertRadioListDialog(passage);
            }
        });
        recyclerView.setAdapter(managerPassageAdapter);
        recyclerView.setEmptyView(rl_empty);
        updatePassageList();
        // 默认主柜信息列表
        managerPassageAdapter.setPassageList(passageListMain);
    }


    /**
     * 当前库存
     *
     * @param passage 货道信息
     */
    private void alertRadioListDialog(final Passage passage) {
        if (passage == null) {
            Toast.makeText(ManagerPassageActivity.this, "货道信息为null.", Toast.LENGTH_SHORT).show();
            return;
        }
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
        new AlertDialog.Builder(ManagerPassageActivity.this)
                .setTitle("货道补货")
                .setSingleChoiceItems(intlist, currentStock - 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        asyncSupplyRecordRequest(passage, which + 1);
                        updatePassageList();
                    }
                }).setNegativeButton("取消", null).show();
    }

    /**
     * 提交补货记录 POST
     */
    private void asyncSupplyRecordRequest(final Passage passage, final int selecteStock) {
        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        SupplyRecordReqBody supplyRecordReqBody = new SupplyRecordReqBody();
        supplyRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        // 处理补货记录
        SupplyRecordObj supplyRecordObj = new SupplyRecordObj();
        supplyRecordObj.passage = (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo();
        supplyRecordObj.card = SeekerSoftConstant.ADMINCARD;
        supplyRecordObj.count = selecteStock;
        supplyRecordObj.time = DataFormat.getNowTime();
        supplyRecordReqBody.record.add(supplyRecordObj);

        Gson gson = new Gson();
        String josn = gson.toJson(supplyRecordReqBody);
        Log.e("json", josn);

        Call<SupplyRecordResBody> postAction = service.supplyRecord(supplyRecordReqBody);
        postAction.enqueue(new Callback<SupplyRecordResBody>() {
            @Override
            public void onResponse(Call<SupplyRecordResBody> call, Response<SupplyRecordResBody> response) {
                if (response != null && response.body() != null) {
                    passage.setStock(passage.getStock() + selecteStock);
                    passageDao.insertOrReplaceInTx(passage);
                    Toast.makeText(ManagerPassageActivity.this, "补货成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagerPassageActivity.this, "supply Record: Failure", Toast.LENGTH_SHORT).show();
                    Log.e("request", "supply Record: Failure");
                }
            }

            @Override
            public void onFailure(Call<SupplyRecordResBody> call, Throwable throwable) {
                Toast.makeText(ManagerPassageActivity.this, "supply Record:  Error", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 读取最新数据库数据
     */
    private void updatePassageList() {
        List<Passage> passageList = passageDao.queryBuilder().where(PassageDao.Properties.IsDel.eq(false)).orderAsc(PassageDao.Properties.SeqNo).list();
        passageListMain.clear();
        passageListA.clear();
        passageListB.clear();
        passageListC.clear();
        for (Passage passage : passageList) {
            if (TextUtils.isEmpty(passage.getFlag())) {
                passageListMain.add(passage);
            } else {
                switch (passage.getFlag()) {
                    case "A":
                        passageListA.add(passage);
                        break;
                    case "B":
                        passageListB.add(passage);
                        break;
                    case "C":
                        passageListC.add(passage);
                        break;
                    default:
                        passageListMain.add(passage);
                        break;
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main:
                managerPassageAdapter.setPassageList(passageListMain);
                break;
            case R.id.btn_a:
                managerPassageAdapter.setPassageList(passageListA);
                break;
            case R.id.btn_b:
                managerPassageAdapter.setPassageList(passageListB);
                break;
            case R.id.btn_c:
                managerPassageAdapter.setPassageList(passageListC);
                break;
            case R.id.btn_return_mainpage:
                this.finish();
                return;
        }
        managerPassageAdapter.notifyDataSetChanged();
    }
}
