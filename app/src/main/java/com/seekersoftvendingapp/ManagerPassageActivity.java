package com.seekersoftvendingapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.database.table.Product;
import com.seekersoftvendingapp.database.table.ProductDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
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

    private PassageDao passageDao;
    private ProductDao productDao;

    private List<Passage> passageListMain = new ArrayList<>();
    private List<Passage> passageListA = new ArrayList<>();
    private List<Passage> passageListB = new ArrayList<>();
    private List<Passage> passageListC = new ArrayList<>();

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

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        passageDao = daoSession.getPassageDao();
        productDao = daoSession.getProductDao();

        recyclerView = (EmptyRecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        managerPassageAdapter = new ManagerPassageAdapter(ManagerPassageActivity.this);
        recyclerView.setAdapter(managerPassageAdapter);
        recyclerView.setEmptyView(rl_empty);

        updatePassageList();

        // 默认主柜信息列表
        managerPassageAdapter.setPassageList(passageListMain);


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
                count =0;
            }else{
                count = passage.getCapacity() - passage.getStock() + Integer.parseInt(passage.getKeeptwo());
            }
            // 设置库存
            passage.setStock(passage.getStock() + count);

            supplyRecordObj.count = count;
            supplyRecordObj.time = DataFormat.getNowTime();
            supplyRecordReqBody.record.add(supplyRecordObj);
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


    /**
     * 读取最新数据库数据
     */
    private void updatePassageList() {
        List<Passage> passageList = passageDao.queryBuilder().where(PassageDao.Properties.IsDel.eq(false)).orderAsc(PassageDao.Properties.SeqNo).list();
        List<Product> productList = productDao.queryBuilder().where(ProductDao.Properties.IsDel.eq(false)).list();

        passageListMain.clear();
        passageListA.clear();
        passageListB.clear();
        passageListC.clear();
        for (Passage passage : passageList) {

            // Keepone 商品名称
            for (Product product : productList) {
                if (product.getObjectId().equals(passage.getProduct())) {
                    passage.setKeepone(product.getProductName());
                    break;
                }
            }

            // Keeptwo 差异补货的数量的统计
            passage.setKeeptwo("0");


            if (TextUtils.isEmpty(passage.getFlag())) {
                passageListMain.add(passage);
            } else {
                switch (passage.getFlag()) {
                    case "1":
                        passageListA.add(passage);
                        break;
                    case "2":
                        passageListB.add(passage);
                        break;
                    case "3":
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
    }
}
