package com.seekersoftvendingapp.track;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.BorrowRecordDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.borrow.BorrowSuccessResBody;
import com.seekersoftvendingapp.network.entity.borrowrecord.BorrowRecordReqBody;
import com.seekersoftvendingapp.network.entity.borrowrecord.BorrowRecordResBody;
import com.seekersoftvendingapp.network.entity.returnpro.ReturnSuccessResBody;
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

public class BorrowReturnNTrack implements InterfaceTrack {
    private Context mContext;

    // 借还记录接口
    private List<BorrowRecord> borrowRecordList = new ArrayList<>();
    private BorrowRecordDao borrowRecordDao;

    public BorrowReturnNTrack(Context ctx) {
        this.mContext = ctx;
        DaoSession daoSession = ((SeekersoftApp) mContext.getApplicationContext()).getDaoSession();
        borrowRecordDao = daoSession.getBorrowRecordDao();
    }

    /**
     * 1. 设备成功出货 本地记录一下无须同步； 2.设备离线或者出货失败，需要进行同步到服务端 takeoutRecord
     * 1. 同步记录为true，直接本地保存； 2. 写数据库+addToList --- 请求网络记录接口 --- 成功（更新数据库）（remove缓存list）;失败（无需操作）（继续保留待提交信息）
     *
     * @param borrowRecord
     */
    public void setBorrrowReturnRecordCommand(final BorrowRecord borrowRecord, final String objectId) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                if (borrowRecord == null) {
                    return;
                }
                if (borrowRecord.getIsFlag()) {
                    // IsFlag: true标识同步；
                    borrowRecordDao.insertOrReplaceInTx(borrowRecord);
                } else if (!borrowRecord.getIsFlag() && !TextUtils.isEmpty(objectId)) {
                    // // IsDel: true标识未同步；串口失败出货，进行失败提交接口
                    borrowRecordDao.insertOrReplaceInTx(borrowRecord);
                    if (borrowRecord.getBorrow()) {
                        borrowFail(borrowRecord, objectId);
                    } else {
                        returnFail(borrowRecord, objectId);
                    }
                } else {
                    // no network or fail need to send to server:(往数据库中更新这条记录 + 等待提交的出货记录list插入这条数据)
                    borrowRecordDao.insertOrReplaceInTx(borrowRecord);
                    borrowRecordList.add(borrowRecord);
                    borrowReturnRecordRequest();
                }
            }
        };
        this.mExecutor.execute(command);
    }

    /**
     * 借还记录接口 同步加载 POST
     */
    public void borrowReturnRecordRequest() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        BorrowRecordReqBody borrowRecordReqBody = new BorrowRecordReqBody();
        borrowRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        borrowRecordReqBody.record.addAll(borrowRecordList);
        Gson gson = new Gson();
        String josn = gson.toJson(borrowRecordReqBody);
        Log.e("json", josn);
        Call<BorrowRecordResBody> postAction = service.borrowRecord(borrowRecordReqBody);
        try {
            Response<BorrowRecordResBody> response = postAction.execute();
            if (response != null && response.body() != null) {
                // 同步成功
                setBorrowReturnRecordOk();
            } else {
                Log.e("request", "borrow Record: Failure");
            }
        } catch (IOException e) {
            Log.e("request", "borrow Record: IOException");
        }
    }

    public void setBorrowReturnRecordOk() {
        for (BorrowRecord borrowRecord : borrowRecordList) {
            borrowRecord.setIsFlag(true);
        }
        borrowRecordDao.insertOrReplaceInTx(borrowRecordList);
        borrowRecordList.clear();
    }


    /**
     * （接口）借 失败 的通知接口
     */
    private void borrowFail(BorrowRecord borrowRecord, String borrowObjectId) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<BorrowSuccessResBody> updateAction = service.borrowFail(borrowObjectId);
        Log.e("json", "borrowFail" + updateAction.request().body().toString());
        try {
            Response<BorrowSuccessResBody> response = updateAction.execute();
            if (response != null && response.body() != null && response.body().data) {
                // 成功
                borrowRecord.setIsFlag(true);
                borrowRecordDao.insertOrReplaceInTx(borrowRecord);
            } else {
                // 失败
                setBorrrowReturnRecordCommand(borrowRecord, "");
            }
        } catch (IOException e) {
            // 失败
            setBorrrowReturnRecordCommand(borrowRecord, "");
        }
    }

    /**
     * （接口）还 失败 的通知接口
     */
    private void returnFail(BorrowRecord borrowRecord, String returnObjectId) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<ReturnSuccessResBody> updateAction = service.returnFail(returnObjectId);
        Log.e("json", "returnFail" + updateAction.request().url().toString());
        try {
            Response<ReturnSuccessResBody> response = updateAction.execute();
            if (response != null && response.body() != null && response.body().data) {
                // 成功
                borrowRecord.setIsFlag(true);
                borrowRecordDao.insertOrReplaceInTx(borrowRecord);
            } else {
                // 失败
                setBorrrowReturnRecordCommand(borrowRecord, "");
            }
        } catch (IOException e) {
            // 失败
            setBorrrowReturnRecordCommand(borrowRecord, "");
        }
    }

}
