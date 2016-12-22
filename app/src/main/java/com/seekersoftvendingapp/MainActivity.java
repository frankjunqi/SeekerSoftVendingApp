package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.AdminCardDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.EmployeeDao;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.database.table.ProductDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.SynchroBaseDataResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.track.Track;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by kjh08490 on 2016/11/25.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_getproduct, btn_borrow, btn_back, btn_manage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_getproduct = (Button) findViewById(R.id.btn_getproduct);
        btn_borrow = (Button) findViewById(R.id.btn_borrow);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_manage = (Button) findViewById(R.id.btn_manage);

        btn_getproduct.setOnClickListener(this);
        btn_borrow.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_manage.setOnClickListener(this);

        // 开启更新
        Track.getInstance(MainActivity.this).setBaseDataNTrackCommand();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int exitFlag = intent.getIntExtra(SeekerSoftConstant.EXITAPP, 0);
        if (exitFlag == 1) {
            // 退出程序
            Track.getInstance(MainActivity.this).removeBaseDataUpdateMessage();
            this.finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getproduct:
                getProduct();
                break;
            case R.id.btn_borrow:
                borrowProduct();
                break;
            case R.id.btn_back:
                backProduct();
                break;
            case R.id.btn_manage:
                manageProduct();
                break;
        }
    }

    /**
     * 取货
     */
    private void getProduct() {
        startActivity(new Intent(MainActivity.this, TakeOutInsertNumActivity.class));
    }

    /**
     * 借货
     */
    private void borrowProduct() {
        startActivity(new Intent(MainActivity.this, BorrowInsertNumActivity.class));
    }

    /**
     * 还货
     */
    private void backProduct() {
        startActivity(new Intent(MainActivity.this, ReturnInsertNumActivity.class));
    }

    /**
     * 管理货物
     */
    private void manageProduct() {
        startActivity(new Intent(MainActivity.this, ManagerCardReadActivity.class));
    }

}
