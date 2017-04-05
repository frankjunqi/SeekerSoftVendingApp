package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.AdminCardDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpCardDao;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.database.table.ProductDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.SynchroBaseDataResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.track.Track;
import com.seekersoftvendingapp.util.DeviceInfoTool;
import com.seekersoftvendingapp.util.LogCat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 0. 获取设备号；
 * 1. 初始化页面；
 * 2. 机器绑定接口；
 * - 绑定失败，显示绑定设备号；
 * - 绑定成功，保存机器的基本信息 & 并且请求基础数据；
 * 3. 数据库基础数据初始化；
 * - 成功之后进行首页，初始化首页；
 * - 失败，停留下在当前页面，并且提示失败信息；
 * Created by kjh08490 on 2016/11/25.
 */

public class StartAppActivity extends BaseActivity {

    // 初始化失败
    private static final int RequestError = -1;

    private static ProgressBar pb_loadingdata;
    private static Button btn_tryagain;
    private static TextView tv_resultdata;

    private AdminCardDao adminCardDao;
    private EmpCardDao empCardDao;
    private EmpPowerDao empPowerDao;
    private PassageDao passageDao;
    private ProductDao productDao;


    private static Handler mHander = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RequestError:
                    handleError();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startapp);
        pb_loadingdata = (ProgressBar) findViewById(R.id.pb_loadingdata);
        btn_tryagain = (Button) findViewById(R.id.btn_tryagain);
        tv_resultdata = (TextView) findViewById(R.id.tv_resultdata);

        // 初始化网络状态
        DeviceInfoTool.handleConnect(getApplicationContext());

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        adminCardDao = daoSession.getAdminCardDao();
        empPowerDao = daoSession.getEmpPowerDao();
        passageDao = daoSession.getPassageDao();
        productDao = daoSession.getProductDao();
        empCardDao = daoSession.getEmpCardDao();

        asyncGetBaseDataRequest();

        btn_tryagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLoading();
                asyncGetBaseDataRequest();
            }
        });
    }

    /**
     * 基础数据 GET
     */
    private void asyncGetBaseDataRequest() {
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<SynchroBaseDataResBody> updateAction = service.getSynchroBaseData(SeekerSoftConstant.DEVICEID, "");
        LogCat.e("getSynchroBaseData = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<SynchroBaseDataResBody>() {
            @Override
            public void onResponse(Call<SynchroBaseDataResBody> call, Response<SynchroBaseDataResBody> response) {
                if (response != null && response.body() != null && response.body().status != 201) {
                    adminCardDao.insertOrReplaceInTx(response.body().getAdminCardList());
                    empCardDao.insertOrReplaceInTx(response.body().getEmpCardList());
                    empPowerDao.insertOrReplaceInTx(response.body().getEmpPowerList());
                    // 第一次请求，直接全部更新
                    passageDao.insertOrReplaceInTx(response.body().getPassageList());
                    productDao.insertOrReplaceInTx(response.body().getProductList());
                    // 成功初始化基础数据
                    successInit();
                } else {
                    mHander.sendEmptyMessageDelayed(RequestError, SeekerSoftConstant.BASEDATALOOPER);

                    Toast.makeText(StartAppActivity.this, "【" + ((response != null && response.body() != null && !TextUtils.isEmpty(response.body().message)) ? response.body().message : "服务端无描述信息.") + "】", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SynchroBaseDataResBody> call, Throwable throwable) {
                if (passageDao.queryBuilder().list().size() > 0) {
                    successInit();
                } else {
                    mHander.sendEmptyMessageDelayed(RequestError, SeekerSoftConstant.BASEDATALOOPER);
                    Toast.makeText(StartAppActivity.this, "基础数据获取失败. Failure", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static void handleError() {
        tv_resultdata.setText("网络处理失败,设备号：\n" + SeekerSoftConstant.DEVICEID + " \n 请联系管理员进行初始化设备基础信息。");
        pb_loadingdata.setVisibility(View.INVISIBLE);
        btn_tryagain.setVisibility(View.VISIBLE);
    }


    private static void handleLoading() {
        tv_resultdata.setText("正在初始化系统....");
        btn_tryagain.setVisibility(View.INVISIBLE);
        pb_loadingdata.setVisibility(View.VISIBLE);
    }

    private void successInit() {
        Toast.makeText(StartAppActivity.this, "初始化基础数据成功", Toast.LENGTH_LONG).show();
        startActivity(new Intent(StartAppActivity.this, MainActivity.class));
        this.finish();
    }
}
