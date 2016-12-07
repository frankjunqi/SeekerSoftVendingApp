package com.seekersoftvendingapp.network;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.R;
import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.database.table.AdminCardDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.PostResBody;
import com.seekersoftvendingapp.network.entity.SynchroBaseDataResBody;
import com.seekersoftvendingapp.network.entity.UpdaeResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 消费流程查询：
 * <p>
 * 货道id ----> （查询货道表） ----> 得到产品id  ----> （查询权限表）----> 得到员工id ---->（查询员工卡关系表）----> 得到卡id列表 ----> 匹配读卡器读到的卡号进行匹配是否可以操作
 * <p>
 * Created by kjh08490 on 2016/11/18.
 */

public class TestNetworkActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView;
    private Button btn_getdata_base, btn_getdata_update, btn_takeout_record, btn_supply_record, btn_borrow_record, btn_error;

    private AdminCardDao adminCardDao;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_network);
        textView = (TextView) findViewById(R.id.textView);

        btn_getdata_base = (Button) findViewById(R.id.btn_getdata_base);
        btn_getdata_update = (Button) findViewById(R.id.btn_getdata_update);

        btn_takeout_record = (Button) findViewById(R.id.btn_takeout_record);
        btn_supply_record = (Button) findViewById(R.id.btn_supply_record);
        btn_borrow_record = (Button) findViewById(R.id.btn_borrow_record);
        btn_error = (Button) findViewById(R.id.btn_error);

        btn_getdata_base.setOnClickListener(this);
        btn_getdata_update.setOnClickListener(this);

        btn_takeout_record.setOnClickListener(this);
        btn_supply_record.setOnClickListener(this);
        btn_borrow_record.setOnClickListener(this);
        btn_error.setOnClickListener(this);

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        adminCardDao = daoSession.getAdminCardDao();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_getdata_base:
                asyncGetBaseDataRequest();
                break;
            case R.id.btn_getdata_update:
                asyncGetUpdateDataRequest();
                break;

            case R.id.btn_takeout_record:
                Toast.makeText(this,"btn_takeout_record",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_supply_record:
                Toast.makeText(this,"btn_supply_record",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_borrow_record:
                Toast.makeText(this,"btn_borrow_record",Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_error:
                Toast.makeText(this,"btn_error",Toast.LENGTH_SHORT).show();
                break;

        }
    }

    // Get
    private void asyncGetBaseDataRequest() {
        // 加载前
        // do something

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<SynchroBaseDataResBody> updateAction = service.getSynchroBaseData("api", "getData", "123", "");
        updateAction.enqueue(new Callback<SynchroBaseDataResBody>() {
            @Override
            public void onResponse(Call<SynchroBaseDataResBody> call, Response<SynchroBaseDataResBody> response) {
                textView.setText("AdminCard: " + response.body().data.AdminCard.size() + "\n"
                        + "Employee: " + response.body().data.Employee.size() + "\n"
                        + "EmpPower: " + response.body().data.EmpPower.size() + "\n"
                        + "Passage: " + response.body().data.Passage.size() + "\n"
                        + "Product: " + response.body().data.Product.size() + "\n"
                        + "RES = " + response.body().toString()
                );
                adminCardDao.insertOrReplaceInTx(response.body().getAdminCardList());
            }

            @Override
            public void onFailure(Call<SynchroBaseDataResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "asyncGetRequest Failure", Toast.LENGTH_LONG).show();
            }
        });
    }


    // Get
    private void asyncGetUpdateDataRequest() {
        // 加载前
        // do something

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<SynchroBaseDataResBody> updateAction = service.getSynchroBaseData("api", "getData", "123", "1480763417");
        updateAction.enqueue(new Callback<SynchroBaseDataResBody>() {
            @Override
            public void onResponse(Call<SynchroBaseDataResBody> call, Response<SynchroBaseDataResBody> response) {
                textView.setText("AdminCard: " + response.body().data.AdminCard.size() + "\n"
                        + "Employee: " + response.body().data.Employee.size() + "\n"
                        + "EmpPower: " + response.body().data.EmpPower.size() + "\n"
                        + "Passage: " + response.body().data.Passage.size() + "\n"
                        + "Product: " + response.body().data.Product.size() + "\n"
                        + "RES = " + response.body().toString()
                );
                List<AdminCard> list = response.body().getAdminCardList();
                adminCardDao.insertOrReplaceInTx(list);
            }

            @Override
            public void onFailure(Call<SynchroBaseDataResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "asyncGetRequest Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Get
    private void asyncGetRequest() {
        // 加载前
        // do something

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<UpdaeResBody> updateAction = service.updateVersion("Srv", "Base.svc", "CheckUpdate", "MubeaEWIST", "100");
        updateAction.enqueue(new Callback<UpdaeResBody>() {
            @Override
            public void onResponse(Call<UpdaeResBody> call, Response<UpdaeResBody> response) {
                Toast.makeText(TestNetworkActivity.this, response.message(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<UpdaeResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "asyncGetRequest Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Post
    private void asyncPostRequest() {
        // 加载前
        // do something

        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<PostResBody> postAction = service.sendNormal("post string body");
        postAction.enqueue(new Callback<PostResBody>() {
            @Override
            public void onResponse(Call<PostResBody> call, Response<PostResBody> response) {
                Toast.makeText(TestNetworkActivity.this, response.message(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<PostResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "asyncPostRequest Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    // Add Header Post
    private void headerCheckPost() {
        // 加载前
        // do something

        // 异步加载(post)  Add headers
        // Define the interceptor, add authentication headers
        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request newRequest = chain.request().newBuilder().addHeader("User-Agent", "okhttp/2.5.0").build();
                return chain.proceed(newRequest);
            }
        };

        // Add the interceptor to OkHttpClient
        OkHttpClient client = new OkHttpClient();
        client.newBuilder().addInterceptor(interceptor);

        // OkHttp3下OkHttpClient的  List<Interceptor>  interceptors()方法，返回的是一个不可编辑的列表，如果对其进行编辑会报出UnSupportedOperationException
        // Interceptor的典型使用场景，就是对request和response的Headers进行编辑
        // Wrong : client.interceptors().add(interceptor);

        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).client(client).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<PostResBody> postAction = service.sendNormal("post string body");
        postAction.enqueue(new Callback<PostResBody>() {
            @Override
            public void onResponse(Call<PostResBody> call, Response<PostResBody> response) {
                Toast.makeText(TestNetworkActivity.this, response.message(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<PostResBody> call, Throwable throwable) {

            }
        });
    }

    // Add Header Get
    private void headerCheckGet() {
        // 加载前
        // do something

        // 异步加载(post)  Add headers
        // Set the custom client when building adapter
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).client(genericClient()).build();

        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<UpdaeResBody> updateAction = service.updateVersion("Srv", "Base.svc", "CheckUpdate", "MubeaEWIST", "versioncode");
        updateAction.enqueue(new Callback<UpdaeResBody>() {
            @Override
            public void onResponse(Call<UpdaeResBody> call, Response<UpdaeResBody> response) {
                Toast.makeText(TestNetworkActivity.this, response.message(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<UpdaeResBody> call, Throwable throwable) {

            }
        });
    }

    // OkHttp3下OkHttpClient的  List<Interceptor>  interceptors()方法，返回的是一个不可编辑的列表，如果对其进行编辑会报出UnSupportedOperationException
    // Interceptor的典型使用场景，就是对request和response的Headers进行编辑
    public static OkHttpClient genericClient() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                                .addHeader("Accept-Encoding", "gzip, deflate")
                                .addHeader("Connection", "keep-alive")
                                .addHeader("Accept", "*/*")
                                .addHeader("Cookie", "add cookies here")
                                .build();
                        return chain.proceed(request);
                    }

                })
                .build();

        return httpClient;
    }


}
