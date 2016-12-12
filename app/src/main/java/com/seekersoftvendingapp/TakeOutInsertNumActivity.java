package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.view.KeyBordView;

import java.util.List;

/**
 * 1. 取货 输入货道号页面
 * Created by kjh08490 on 2016/11/25.
 */

public class TakeOutInsertNumActivity extends AppCompatActivity {

    private Button btn_return_mainpage;

    private LinearLayout ll_keyboard;

    private KeyBordView keyBordView;

    private PassageDao passageDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertgoodsnum);

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        passageDao = daoSession.getPassageDao();

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TakeOutInsertNumActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
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
    }

    /**
     * 货道号 ----> 查询Passage表 ----> product编号（唯一） ---->  查询权限表 ----> 权限id（List<emppower>[权限的周期过滤]） ----> 查询用户表 ----> 得到crads的集合（List<Card>）
     *
     * @param keyPassage
     */
    private void checkPassageDirectUser(String keyPassage) {
        // 检查用户输入的货道号的校验
        if (TextUtils.isEmpty(keyPassage)) {
            Toast.makeText(TakeOutInsertNumActivity.this, "请输入货道号。", Toast.LENGTH_SHORT).show();
            return;
        }

        // 检查数据库是否有该货道的资源数据
        // isDel = false & Stock > 0 & seqNo == keyPassage
        List<Passage> list = passageDao.queryBuilder()
                .where(PassageDao.Properties.IsDel.eq(false))
                .where(PassageDao.Properties.Stock.gt(0))
                .where(PassageDao.Properties.SeqNo.eq(keyPassage)).list();
        if (list != null && list.size() > 0) {
            // 说明货道可以进行消费产品
            String productId = list.get(0).getProduct();
            // 周期消费次数??


            // 检查是否有该硬件货道??
            //
            //

            Intent intent = new Intent(TakeOutInsertNumActivity.this, TakeOutCardReadActivity.class);
            intent.putExtra(SeekerSoftConstant.PRODUCTID, productId);
            intent.putExtra(SeekerSoftConstant.PASSAGEID, keyPassage);
            startActivity(intent);
        } else {
            Toast.makeText(TakeOutInsertNumActivity.this, "货道暂不能进行消费，请联系管理员。", Toast.LENGTH_LONG).show();
            return;
        }


    }

}
