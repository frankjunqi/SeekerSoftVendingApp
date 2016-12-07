package com.seekersoftvendingapp.network.api;

import android.database.Observable;

import com.seekersoftvendingapp.network.entity.PostResBody;
import com.seekersoftvendingapp.network.entity.SynchroBaseDataResBody;
import com.seekersoftvendingapp.network.entity.TempResBody;
import com.seekersoftvendingapp.network.entity.UpdaeResBody;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by kjh08490 on 2016/3/16.
 */
public interface SeekerSoftService {

    @GET("/{srv}/{svc}/{queryname}")
    Call<TempResBody> tempRequest(
            @Path("srv") String srv,
            @Path("svc") String svc,
            @Path("queryname") String queryname
    );

    @GET("/{srv}/{svc}/{queryname}")
    Call<UpdaeResBody> updateVersion(
            @Path("srv") String srv,
            @Path("svc") String svc,
            @Path("queryname") String queryname,
            @Query("Code") String code,
            @Query("Ver") String ver
    );

    @POST("/")
    Call<PostResBody> sendNormal(@Body String postStr);

    @FormUrlEncoded
    @POST("FundPaperTrade/AppUserLogin")
    Observable<Response> getTransData(@FieldMap Map<String, String> map);


    // 获取基础数据接口
    @GET("{api}/{serviceName}/{deviceId}/{timestamp}")
    Call<SynchroBaseDataResBody> getSynchroBaseData(
            @Path("api") String api,
            @Path("serviceName") String serviceName,
            @Path("deviceId") String deviceId,
            @Path("timestamp") String timestamp
    );

}
