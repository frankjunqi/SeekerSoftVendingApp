package com.seekersoftvendingapp.network.api;

import android.database.Observable;

import com.seekersoftvendingapp.network.entity.PostResBody;
import com.seekersoftvendingapp.network.entity.SynchroBaseDataResBody;
import com.seekersoftvendingapp.network.entity.TempResBody;
import com.seekersoftvendingapp.network.entity.UpdaeResBody;
import com.seekersoftvendingapp.network.entity.borrow.BorrowResBody;
import com.seekersoftvendingapp.network.entity.borrow.BorrowSuccessResBody;
import com.seekersoftvendingapp.network.entity.borrowrecord.BorrowRecordReqBody;
import com.seekersoftvendingapp.network.entity.borrowrecord.BorrowRecordResBody;
import com.seekersoftvendingapp.network.entity.error.ErrorReqBody;
import com.seekersoftvendingapp.network.entity.error.ErrorResBody;
import com.seekersoftvendingapp.network.entity.returnpro.ReturnProResBody;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordReqBody;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordResBody;
import com.seekersoftvendingapp.network.entity.takeout.TakeOutResBody;
import com.seekersoftvendingapp.network.entity.takeout.TakeOutSuccessResBody;
import com.seekersoftvendingapp.network.entity.takeoutrecord.TakeoutRecordReqBody;
import com.seekersoftvendingapp.network.entity.takeoutrecord.TakeoutRecordResBody;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
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


    /*
    http://smartbox.leanapp.cn/api/getData/[deviceid]/[timestamp] 获取基础数据 & 数据更新接口
    根据上一次同步的时间戳获取更新时间大于时间戳的数据
    参数：deviceid(设备ID)，timestamp(上一次同步的时间戳)
    返回值：data:{表1:[],表2:[]...}
    与初始化返回的数据表形式一致*/
    @GET("{api}/{serviceName}/{deviceId}/{timestamp}")
    Call<SynchroBaseDataResBody> getSynchroBaseData(
            @Path("api") String api,
            @Path("serviceName") String serviceName,
            @Path("deviceId") String deviceId,
            @Path("timestamp") String timestamp
    );


    /*
    http://smartbox.leanapp.cn/api/takeout/[deviceid]/[card]/[passage]
    根据设备号、卡号、货道号判断是否能出货
    参数：deviceid(设备ID)，card(员工卡号)，passage(货道号)
    返回值：data:{result:true/false,objectId:abc123}
    true:可以出货
    false:不能出货*/
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @GET("api/takeout/{deviceid}/{card}/{passage}")
    Call<TakeOutResBody> takeOut(
            @Path("deviceid") String deviceid,
            @Path("card") String card,
            @Path("passage") String passage
    );


    /*
    http://smartbox.leanapp.cn/api/takeout/success/[objectId]
    设备成功出货后回调此接口上传交易成功记录
    参数：objectId(取货记录ID)
    返回值：data:{result:true/false}
    true:成功
    false:失败
    */
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @GET("api/takeout/success/{objectId}")
    Call<TakeOutSuccessResBody> takeOutSuccess(
            @Path("objectId") String objectId
    );


    /*
    http://smartbox.leanapp.cn/api/borrow/[deviceid]/[card]/[passage]
    根据设备号、卡号、货道号判断是否借出货
    参数：deviceid(设备ID)，card(员工卡号)，passage(货道号)
    返回值：data:{result:true/false,objectId:abc123}
    true:可以出货
    false:不能出货*/
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @GET("api/borrow/{deviceid}/{card}/{passage}")
    Call<BorrowResBody> borrow(
            @Path("deviceid") String deviceid,
            @Path("card") String card,
            @Path("passage") String passage
    );


    /*
    http://smartbox.leanapp.cn/api/borrow/success/[objectId]
    设备成功出货后回调此接口上传交易成功记录
    参数：objectId(取货记录ID)
    返回值：data:{result:true/false}
    true:成功
    false:失败*/
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @GET("api/borrow/success/{objectId}")
    Call<BorrowSuccessResBody> borrowSuccess(
            @Path("objectId") String objectId
    );

    /*
    http://smartbox.leanapp.cn/api/return/[deviceid]/{card}/[passage]
    还货记录
    参数：deviceid(设备ID)，card(卡号),passage(货道号)
    返回值：data:{result:true/false}
    true:成功
    false:失败*/
    @GET("api/return/{deviceid}/{card}/{passage}")
    Call<ReturnProResBody> returnPro(
            @Path("deviceid") String deviceid,
            @Path("card") String crad,
            @Path("passage") String passage
    );

    /*
    http://smartbox.leanapp.cn/api/takeout_record
    提交取货记录
    参数：deviceId(设备号)，record:[passage(货道号)，card(卡号)，time(取货时间)]
    返回值：data:{result:true/false}
    true:成功
    false:失败*/
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("/api/takeout_record")
    Call<TakeoutRecordResBody> takeoutRecord(@Body TakeoutRecordReqBody postStr);


    /*
    http://smartbox.leanapp.cn/api/supply_record
    提交补货记录
    参数：deviceId(设备号)，record:[passage(货道号)，card(卡号)，count(补货数量)，time(补货时间)]
    返回值：data:{result:true/false}
    true:成功
    false:失败*/
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("/api/supply_record")
    Call<SupplyRecordResBody> supplyRecord(@Body SupplyRecordReqBody postStr);


    /*
    http://smartbox.leanapp.cn/api/borrow_record
    提交借还记录
    参数：deviceId(设备号)，record:[passage(货道号)，card(卡号)，borrow(true:借/false:还)，time(借还时间)]
    返回值：bool
    true:成功
    false:失败*/
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("/api/borrow_record")
    Call<BorrowRecordResBody> borrowRecord(@Body BorrowRecordReqBody postStr);


    /*
    http://smartbox.leanapp.cn/api/error
    提交异常记录
    参数：deviceId(设备号)，error:[passage(货道号)，card(卡号)，info(异常信息)，time(异常时间)]
    返回值：bool
    true:成功
    false:失败*/
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("/api/error")
    Call<ErrorResBody> error(@Body ErrorReqBody postStr);
}
