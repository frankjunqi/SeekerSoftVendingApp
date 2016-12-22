package com.seekersoftvendingapp.track;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.database.table.TakeoutRecord;
import com.seekersoftvendingapp.database.table.TakeoutRecordDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.takeout.TakeOutSuccessResBody;
import com.seekersoftvendingapp.network.entity.takeoutrecord.TakeoutRecordReqBody;
import com.seekersoftvendingapp.network.entity.takeoutrecord.TakeoutRecordResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by kjh08490 on 2016/12/20.
 */

public class TakeOutNTrack implements InterfaceTrack {
    private Context mContext;

    // 等待提交的出货记录list
    private List<TakeoutRecord> takeOutRecordList = new ArrayList<>();
    private TakeoutRecordDao takeoutRecordDao;

    public TakeOutNTrack(Context ctx) {
        this.mContext = ctx;
        DaoSession daoSession = ((SeekersoftApp) mContext.getApplicationContext()).getDaoSession();
        takeoutRecordDao = daoSession.getTakeoutRecordDao();
    }


    /**
     * 1. 成功的消费记录直接falg=true进行记录本地，无须同步服务端
     * 2.设备离线或者出货失败，需要进行同步到服务端 takeoutRecord
     * 写数据库+addToList --- 请求网络记录接口 --- 成功（更新数据库）（remove缓存list）;失败（无需操作）（继续保留待提交信息）
     *
     * @param takeOutRecord
     */

    public void setTakeOutRecord(final TakeoutRecord takeOutRecord, final String objectId) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                if (takeOutRecord == null) {
                    return;
                }
                if (takeOutRecord.getIsDel()) {
                    // IsDel: true标识同步；
                    takeoutRecordDao.insertOrReplaceInTx(takeOutRecord);
                } else if (!takeOutRecord.getIsDel() && !TextUtils.isEmpty(objectId)) {
                    // // IsDel: true标识未同步；串口失败出货，进行失败提交接口
                    takeoutRecordDao.insertOrReplaceInTx(takeOutRecord);
                    takeOutFailed(takeOutRecord, objectId);
                } else {
                    // no network or fail need to send to server:(往数据库中更新这条记录 + 等待提交的出货记录list插入这条数据)
                    takeoutRecordDao.insertOrReplaceInTx(takeOutRecord);
                    takeOutRecordList.add(takeOutRecord);
                    takeoutRecordRequest();
                }
            }
        };
        this.mExecutor.execute(command);
    }


    /**
     * 提交取货记录 同步加载 POST
     */
    private void takeoutRecordRequest() {
        if (takeOutRecordList == null || takeOutRecordList.size() == 0) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        TakeoutRecordReqBody takeoutRecordReqBody = new TakeoutRecordReqBody();
        takeoutRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        takeoutRecordReqBody.record.addAll(takeOutRecordList);
        Gson gson = new Gson();
        String josn = gson.toJson(takeoutRecordReqBody);
        Log.e("json", "takeoutRecord = " + josn);
        Call<TakeoutRecordResBody> postAction = service.takeoutRecord(takeoutRecordReqBody);
        try {
            Response<TakeoutRecordResBody> response = postAction.execute();
            if (response != null && response.body() != null) {
                // 成功更新数据库标识
                setTaketOutRecordOk();
            } else {
                // 失败不需要更新数据库标识
                Log.e("request", "takeout Record: Failure");
            }
        } catch (IOException e) {
            // 失败不需要更新数据库标识
            Log.e("request", "takeout Record: IOException");
        }
    }

    /**
     * IsDel：
     * true标识同步完成；
     * false: 未同步；
     */
    public void setTaketOutRecordOk() {
        for (TakeoutRecord takeOutRecord : takeOutRecordList) {
            takeOutRecord.setIsDel(true);
        }
        takeoutRecordDao.insertOrReplaceInTx(takeOutRecordList);
        takeOutRecordList.clear();
    }

    /**
     * （接口）出货失败的通知接口 同步
     */
    private void takeOutFailed(TakeoutRecord takeOutRecord, String takeOutObjectId) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<TakeOutSuccessResBody> updateAction = service.takeOutFail(takeOutObjectId);
        Log.e("json", "takeOutFail = " + updateAction.request().url().toString());
        try {
            Response<TakeOutSuccessResBody> response = updateAction.execute();
            if (response != null && response.body() != null && response.body().data) {
                // 成功
                takeOutRecord.setIsDel(true);
                takeoutRecordDao.insertOrReplaceInTx(takeOutRecord);
            } else {
                // 失败
                setTakeOutRecord(takeOutRecord, "");
            }
        } catch (IOException e) {
            // 失败
            setTakeOutRecord(takeOutRecord, "");
        }
    }

    @Override
    public void synchroAllDataToServer() {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                if (takeOutRecordList == null || takeOutRecordList.size() == 0) {
                    takeOutRecordList = takeoutRecordDao.queryBuilder().where(TakeoutRecordDao.Properties.IsDel.eq(false)).list();
                }
                takeoutRecordRequest();
            }
        };
        this.mExecutor.execute(command);
    }
}
