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

import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.util.KeyChangeUtil;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.view.KeyBordView;

import java.util.List;

/**
 * 1. 取货 输入货道号页面
 * Created by kjh08490 on 2016/11/25.
 */

public class TakeOutInsertNumActivity extends BaseActivity {

    private LinearLayout ll_keyboard;

    private KeyBordView keyBordView;

    private PassageDao passageDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insertgoodsnum);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("取货");

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        passageDao = daoSession.getPassageDao();

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

        countDownTimer.start();
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

        String isFirstFlag = keyPassage.substring(0, 1);
        String outNum = KeyChangeUtil.getFlagInt(isFirstFlag);
        if (!TextUtils.isEmpty(outNum)) {
            keyPassage = keyPassage.substring(1, keyPassage.length());
        }
        // 检查数据库是否有该货道的资源数据（唯一）
        // isDel = false & Stock > 0 & seqNo == keyPassage
        List<Passage> list = passageDao.queryBuilder()
                .where(PassageDao.Properties.IsDel.eq(false))
                .where(PassageDao.Properties.Stock.gt(0)) // 判断库存
                .where(PassageDao.Properties.IsSend.eq(true))// issend: "true:销售 false:借还"
                .where(PassageDao.Properties.SeqNo.eq(keyPassage))
                .where(PassageDao.Properties.Flag.eq(outNum))
                .list();
        if (list != null && list.size() > 0) {
            // 检查是否有该硬件货道??

            // 货道实体
            Passage passage = list.get(0);
            Intent intent = null;
            if (TextUtils.isEmpty(passage.getFlag())) {
                intent = new Intent(TakeOutInsertNumActivity.this, TakeOutNumActivity.class);
            } else {
                intent = new Intent(TakeOutInsertNumActivity.this, TakeOutCardReadActivity.class);
            }
            intent.putExtra(SeekerSoftConstant.PASSAGE, passage);
            intent.putExtra(SeekerSoftConstant.TakeoutNum, 1);
            startActivity(intent);
            this.finish();
        } else {
            Toast.makeText(TakeOutInsertNumActivity.this, "货道暂不能进行消费，可能无库存，请联系管理员。", Toast.LENGTH_LONG).show();
        }
    }
}
