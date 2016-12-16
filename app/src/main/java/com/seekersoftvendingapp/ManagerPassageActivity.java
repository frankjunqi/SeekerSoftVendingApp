package com.seekersoftvendingapp;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.view.EmptyRecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/12/15.
 */

public class ManagerPassageActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_main, btn_a, btn_b, btn_c;
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

        // 数据做校验
        if (capacity <= 0 || currentStock > capacity) {
            Toast.makeText(ManagerPassageActivity.this, "最大库存和当前库存可能存在脏数据.", Toast.LENGTH_SHORT).show();
            return;
        }
        final String[] intlist = new String[capacity];
        for (int i = 0; i < capacity; i++) {
            intlist[i] = String.valueOf(i + 1);
        }
        new AlertDialog.Builder(ManagerPassageActivity.this)
                .setTitle("设置库存")
                .setSingleChoiceItems(intlist, currentStock - 1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 当前设置的库存 ,更新本地库存
                        int selecteStock = which + 1;
                        passage.setStock(selecteStock);
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passageDao.insertOrReplace(passage);
                        updatePassageList();
                    }
                }).setNegativeButton("取消", null).show();
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
        }
    }
}
