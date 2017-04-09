package com.seekersoftvendingapp;

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

public class ManagerCheckPassageActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_main, btn_a, btn_b, btn_c, btn_return_mainpage;
    private EmptyRecyclerView recyclerView;
    private RelativeLayout rl_empty;

    private TextView tv_modify_down;

    private ManagerCheckPassageAdapter managerPassageAdapter;

    private PassageDao passageDao;
    private ProductDao productDao;

    private List<Passage> passageListMain = new ArrayList<>();
    private List<Passage> passageListA = new ArrayList<>();
    private List<Passage> passageListB = new ArrayList<>();
    private List<Passage> passageListC = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_check_passage);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("查看库存");

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

        managerPassageAdapter = new ManagerCheckPassageAdapter(ManagerCheckPassageActivity.this);
        recyclerView.setAdapter(managerPassageAdapter);
        recyclerView.setEmptyView(rl_empty);

        updatePassageList();

        // 默认主柜信息列表
        managerPassageAdapter.setPassageList(passageListMain);


    }

    /**
     * 读取最新数据库数据
     */
    private void updatePassageList() {
        List<Passage> passageList = passageDao.queryBuilder()
                .where(PassageDao.Properties.IsDel.eq(false))
                .orderAsc(PassageDao.Properties.SeqNo)
                .where(PassageDao.Properties.IsSend.eq(true))// issend: "true:销售 false:借还"
                .list();
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
