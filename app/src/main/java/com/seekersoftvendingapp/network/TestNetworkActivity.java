package com.seekersoftvendingapp.network;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seekersoftvendingapp.BaseActivity;
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
import com.seekersoftvendingapp.network.entity.borrow.BorrowSuccessResBody;
import com.seekersoftvendingapp.network.entity.borrowrecord.BorrowRecordReqBody;
import com.seekersoftvendingapp.network.entity.borrowrecord.BorrowRecordResBody;
import com.seekersoftvendingapp.network.entity.error.ErrorReqBody;
import com.seekersoftvendingapp.network.entity.error.ErrorResBody;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordObj;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordReqBody;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordResBody;
import com.seekersoftvendingapp.network.entity.takeout.TakeOutSuccessResBody;
import com.seekersoftvendingapp.network.entity.takeoutrecord.TakeoutRecordReqBody;
import com.seekersoftvendingapp.network.entity.takeoutrecord.TakeoutRecordResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;

import java.io.IOException;
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

public class TestNetworkActivity extends BaseActivity implements View.OnClickListener {

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
                asyncTakeoutRecordRequest();
                break;

            case R.id.btn_supply_record:
                asyncSupplyRecordRequest();
                break;

            case R.id.btn_borrow_record:
                asyncBorrowRecordRequest();
                break;

            case R.id.btn_error:
                asyncErrorRequest();
                break;

        }
    }

    /**
     * 基础数据 GET
     */
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
                        + "EmpPower: " + response.body().data.EmpPower.size() + "\n"
                        + "Passage: " + response.body().data.Passage.size() + "\n"
                        + "Product: " + response.body().data.Product.size() + "\n"
                        + "RES = " + response.body().toString()
                );
                adminCardDao.insertOrReplaceInTx(response.body().getAdminCardList());
            }

            @Override
            public void onFailure(Call<SynchroBaseDataResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "basedate :  Failure", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 基础数据更新 GET
     */
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
                Toast.makeText(TestNetworkActivity.this, "base update date :   Failure", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 提交取货记录 POST
     */
    private void asyncTakeoutRecordRequest() {
        // 加载前
        // do something

        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);

        TakeoutRecordReqBody takeoutRecordReqBody = new TakeoutRecordReqBody();
        takeoutRecordReqBody.deviceId = "123";

        /*TakeoutRecordObj takeoutRecordObj = new TakeoutRecordObj();
        takeoutRecordObj.passage = "11";
        takeoutRecordObj.card = "987";
        takeoutRecordObj.time = "2016-12-3 19:10:17";
        takeoutRecordReqBody.record.add(takeoutRecordObj);


        TakeoutRecordObj takeoutRecordObj2 = new TakeoutRecordObj();
        takeoutRecordObj2.passage = "12";
        takeoutRecordObj2.card = "986";
        takeoutRecordObj2.time = "2016-12-3 19:11:17";
        takeoutRecordReqBody.record.add(takeoutRecordObj2);*/

        Gson gson = new Gson();
        String josn = gson.toJson(takeoutRecordReqBody);
        Log.e("json", josn);

        Call<TakeoutRecordResBody> postAction = service.takeoutRecord(takeoutRecordReqBody);
        postAction.enqueue(new Callback<TakeoutRecordResBody>() {
            @Override
            public void onResponse(Call<TakeoutRecordResBody> call, Response<TakeoutRecordResBody> response) {
                if (response != null && response.body() != null) {
                    textView.setText(textView.getText() + " \n" + "takeout error: " + response.body().data + "");
                } else {
                    Log.e("request", "takeout error:  Failure");
                }
            }

            @Override
            public void onFailure(Call<TakeoutRecordResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "takeout error: Failure", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 提交补货记录 POST
     */
    private void asyncSupplyRecordRequest() {
        // 加载前
        // do something

        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);

        SupplyRecordReqBody supplyRecordReqBody = new SupplyRecordReqBody();
        supplyRecordReqBody.deviceId = "123";

        SupplyRecordObj supplyRecordObj = new SupplyRecordObj();
        supplyRecordObj.passage = "11";
        supplyRecordObj.card = "987";
        supplyRecordObj.count = 10;
        supplyRecordObj.time = "2016-12-3 19:10:17";
        supplyRecordReqBody.record.add(supplyRecordObj);


        SupplyRecordObj supplyRecordObj2 = new SupplyRecordObj();
        supplyRecordObj2.passage = "12";
        supplyRecordObj2.card = "986";
        supplyRecordObj2.count = 10;
        supplyRecordObj2.time = "2016-12-3 19:11:17";
        supplyRecordReqBody.record.add(supplyRecordObj2);

        Gson gson = new Gson();
        String josn = gson.toJson(supplyRecordReqBody);
        Log.e("json", josn);

        Call<SupplyRecordResBody> postAction = service.supplyRecord(supplyRecordReqBody);
        postAction.enqueue(new Callback<SupplyRecordResBody>() {
            @Override
            public void onResponse(Call<SupplyRecordResBody> call, Response<SupplyRecordResBody> response) {
                if (response != null && response.body() != null) {
                    textView.setText(textView.getText() + " \n" + "supply Record: " + response.body().data + "");
                } else {
                    Log.e("request", "supply Record: Failure");
                }
            }

            @Override
            public void onFailure(Call<SupplyRecordResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "supply Record:  Failure", Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 提交借还记录 POST
     */
    private void asyncBorrowRecordRequest() {
        // 加载前
        // do something

        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);

        BorrowRecordReqBody borrowRecordReqBody = new BorrowRecordReqBody();
        borrowRecordReqBody.deviceId = "123";

        /*BorrowRecordObj borrowRecordObj = new BorrowRecordObj();
        borrowRecordObj.passage = "11";
        borrowRecordObj.card = "987";
        borrowRecordObj.borrow = true;
        borrowRecordObj.time = "2016-12-3 19:10:17";
        borrowRecordReqBody.record.add(borrowRecordObj);


        BorrowRecordObj borrowRecordObj2 = new BorrowRecordObj();
        borrowRecordObj2.passage = "12";
        borrowRecordObj2.card = "986";
        borrowRecordObj2.borrow = true;
        borrowRecordObj2.time = "2016-12-3 19:11:17";
        borrowRecordReqBody.record.add(borrowRecordObj2);*/

        Gson gson = new Gson();
        String josn = gson.toJson(borrowRecordReqBody);
        Log.e("json", josn);

        Call<BorrowRecordResBody> postAction = service.borrowRecord(borrowRecordReqBody);
        postAction.enqueue(new Callback<BorrowRecordResBody>() {
            @Override
            public void onResponse(Call<BorrowRecordResBody> call, Response<BorrowRecordResBody> response) {
                if (response != null && response.body() != null) {
                    textView.setText(textView.getText() + " \n" + "borrow Record: " + response.body().data + "");
                } else {
                    Log.e("request", "borrow Record: Failure");
                }
            }

            @Override
            public void onFailure(Call<BorrowRecordResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "borrow Record:  Failure", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 提交异常记录 POST
     */
    private void asyncErrorRequest() {
        // 加载前
        // do something

        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);

        ErrorReqBody errorRecordReqBody = new ErrorReqBody();
        errorRecordReqBody.deviceId = "123";

        /*ErrorObj errorRecordObj = new ErrorObj();
        errorRecordObj.passage = "11";
        errorRecordObj.card = "987";
        errorRecordObj.node = "出货";
        errorRecordObj.info = "出货失败";
        errorRecordObj.time = "2016-12-3 19:10:17";
        errorRecordReqBody.error.add(errorRecordObj);*/

        Gson gson = new Gson();
        String josn = gson.toJson(errorRecordReqBody);
        Log.e("json", josn);

        Call<ErrorResBody> postAction = service.error(errorRecordReqBody);
        postAction.enqueue(new Callback<ErrorResBody>() {
            @Override
            public void onResponse(Call<ErrorResBody> call, Response<ErrorResBody> response) {
                if (response != null && response.body() != null) {
                    textView.setText(textView.getText() + " \n" + "error : " + response.body().data + "");
                } else {
                    Log.e("request", "error : Failure");
                }
            }

            @Override
            public void onFailure(Call<ErrorResBody> call, Throwable throwable) {
                Toast.makeText(TestNetworkActivity.this, "error :  Failure", Toast.LENGTH_LONG).show();
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

    /**
     * （接口）出货成功的通知接口
     */
    private void takeOutSuccess(String takeOutObjectId) {
        // 加载前
        // do something

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<TakeOutSuccessResBody> updateAction = service.takeOutFail(takeOutObjectId);
        updateAction.enqueue(new Callback<TakeOutSuccessResBody>() {
            @Override
            public void onResponse(Call<TakeOutSuccessResBody> call, Response<TakeOutSuccessResBody> response) {
                if (response != null && response.body() != null && response.body().data) {
                    Toast.makeText(TestNetworkActivity.this, "出货成功标识提交服务端成功,true", Toast.LENGTH_LONG).show();
                    // TODO 本地数据库消费记录 默认提交到服务端的falg为 true

                } else {
                    // TODO DO Nothing
                    Toast.makeText(TestNetworkActivity.this, "提交失败,false", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<TakeOutSuccessResBody> call, Throwable throwable) {
                // TODO DO Nothing
                Toast.makeText(TestNetworkActivity.this, "网络问题", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * （接口）借成功的通知接口
     */
    private void borrowSuccess(String borrowObjectId) {
        // 加载前
        // do something

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<BorrowSuccessResBody> updateAction = service.borrowFail(borrowObjectId);
        updateAction.enqueue(new Callback<BorrowSuccessResBody>() {
            @Override
            public void onResponse(Call<BorrowSuccessResBody> call, Response<BorrowSuccessResBody> response) {
                if (response != null && response.body() != null && response.body().data) {
                    Toast.makeText(TestNetworkActivity.this, "出货成功标识提交服务端成功,true", Toast.LENGTH_LONG).show();
                    // TODO 本地数据库消费记录 默认提交到服务端的falg为 true


                } else {
                    // TODO DO Nothing
                    Toast.makeText(TestNetworkActivity.this, "提交失败,false", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<BorrowSuccessResBody> call, Throwable throwable) {
                // TODO DO Nothing
                Toast.makeText(TestNetworkActivity.this, "网络问题", Toast.LENGTH_LONG).show();
            }
        });
    }
}
