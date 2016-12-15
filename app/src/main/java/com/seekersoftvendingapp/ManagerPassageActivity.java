package com.seekersoftvendingapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/12/15.
 */

public class ManagerPassageActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_main, btn_a, btn_b, btn_c;
    private RecyclerView recyclerView;

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
        btn_main = (Button) findViewById(R.id.btn_main);
        btn_a = (Button) findViewById(R.id.btn_a);
        btn_b = (Button) findViewById(R.id.btn_b);
        btn_c = (Button) findViewById(R.id.btn_c);

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        passageDao = daoSession.getPassageDao();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        managerPassageAdapter = new ManagerPassageAdapter(new ManagerPassageAdapter.ManagerPassageClickListener() {
            @Override
            public void onNoteClick(int position) {
                Passage passage = managerPassageAdapter.getPassage(position);
            }
        });
        recyclerView.setAdapter(managerPassageAdapter);

        List<Passage> passageList = passageDao.queryBuilder().where(PassageDao.Properties.IsDel.eq(false)).list();
        for (Passage passage : passageList) {

        }

        // 默认主柜信息列表
        managerPassageAdapter.setPassageList(passageListMain);

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
