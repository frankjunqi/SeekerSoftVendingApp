package com.seekersoftvendingapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import java.util.List;

/**
 * 4. 管理员 管理页面
 * Created by kjh08490 on 2016/11/28.
 */

public class ManagerGoodsActivity extends AppCompatActivity implements View.OnClickListener {

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
                        List<Passage> passageList = passageDao.queryBuilder().where(PassageDao.Properties.IsDel.eq(false)).list();
                        for (Passage passage : passageList) {
                            passage.setStock(passage.getCapacity());
                        }
                        passageDao.insertOrReplaceInTx(passageList);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();
    }
}
