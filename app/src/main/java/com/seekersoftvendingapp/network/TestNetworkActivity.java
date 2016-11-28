package com.seekersoftvendingapp.network;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.seekersoftvendingapp.R;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.PostResBody;
import com.seekersoftvendingapp.network.entity.UpdaeResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by kjh08490 on 2016/11/18.
 */

public class TestNetworkActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_activity_network);

        //asyncGetRequest();
        //asyncPostRequest();

        headerCheckGet();
        headerCheckPost();
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