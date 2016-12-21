package com.seekersoftvendingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seekersoftvendingapp.database.table.AdminCard;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 4. 管理员 管理页面
 * Created by kjh08490 on 2016/11/28.
 */

public class ManagerGoodsActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_onekeyinsert;
    private Button btn_onebyoneinsert;
    private Button btn_exit;
    private Button btn_backtomain;

    private AdminCard adminCard;
    private PassageDao passageDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managergoods);

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        passageDao = daoSession.getPassageDao();

        adminCard = (AdminCard) getIntent().getSerializableExtra(SeekerSoftConstant.ADMINCARD);

        btn_onekeyinsert = (Button) findViewById(R.id.btn_onekeyinsert);
        btn_onebyoneinsert = (Button) findViewById(R.id.btn_onebyoneinsert);
        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_backtomain = (Button) findViewById(R.id.btn_backtomain);

        btn_onekeyinsert.setOnClickListener(this);
        btn_onebyoneinsert.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_backtomain.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onekeyinsert:
                plainDialogDemo();
                break;
            case R.id.btn_onebyoneinsert:
                startActivity(new Intent(ManagerGoodsActivity.this, ManagerPassageActivity.class));
                break;
            case R.id.btn_exit:
                exitDialog();
                break;
            case R.id.btn_backtomain:
                Intent intent = new Intent(ManagerGoodsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    /**
     * 退出系统弹框提醒
     */
    private void exitDialog() {
        new AlertDialog.Builder(ManagerGoodsActivity.this)
                .setTitle("退出系统")
                .setMessage("你确定要退出系统吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent exitIntent = new Intent(ManagerGoodsActivity.this, MainActivity.class);
                        exitIntent.putExtra(SeekerSoftConstant.EXITAPP, SeekerSoftConstant.EXITAPPFALG);
                        exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(exitIntent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();
    }

    /**
     * 一键补货
     */
    private void plainDialogDemo() {
        new AlertDialog.Builder(ManagerGoodsActivity.this)
                .setTitle("一键补货")
                .setMessage("确定把所有货道上的货品设置成最大库存？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一键补货,补货到最大库存
                        asyncSupplyRecordRequest();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();
    }

    /**
     * 提交补货记录 POST
     */
    private void asyncSupplyRecordRequest() {
        final List<Passage> passageList = passageDao.queryBuilder().where(PassageDao.Properties.IsDel.eq(false)).list();
        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        SupplyRecordReqBody supplyRecordReqBody = new SupplyRecordReqBody();
        supplyRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        // 处理补货记录
        for (Passage passage : passageList) {
            SupplyRecordObj supplyRecordObj = new SupplyRecordObj();
            supplyRecordObj.passage = passage.getFlag() + passage.getSeqNo();
            supplyRecordObj.card = SeekerSoftConstant.ADMINCARD;
            supplyRecordObj.count = passage.getCapacity() - passage.getStock();
            supplyRecordObj.time = DataFormat.getNowTime();
            supplyRecordReqBody.record.add(supplyRecordObj);
        }
        Gson gson = new Gson();
        String josn = gson.toJson(supplyRecordReqBody);
        Log.e("json", josn);

        Call<SupplyRecordResBody> postAction = service.supplyRecord(supplyRecordReqBody);
        postAction.enqueue(new Callback<SupplyRecordResBody>() {
            @Override
            public void onResponse(Call<SupplyRecordResBody> call, Response<SupplyRecordResBody> response) {
                if (response != null && response.body() != null) {
                    for (Passage passage : passageList) {
                        passage.setStock(passage.getCapacity());
                    }
                    passageDao.insertOrReplaceInTx(passageList);
                    Toast.makeText(ManagerGoodsActivity.this, "补货成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagerGoodsActivity.this, "supply Record: Failure", Toast.LENGTH_SHORT).show();
                    Log.e("request", "supply Record: Failure");
                }
            }

            @Override
            public void onFailure(Call<SupplyRecordResBody> call, Throwable throwable) {
                Toast.makeText(ManagerGoodsActivity.this, "supply Record:  Error", Toast.LENGTH_LONG).show();
            }
        });
    }

}
