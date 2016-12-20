package com.seekersoftvendingapp.track;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.TakeoutRecord;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.borrowrecord.BorrowRecordReqBody;
import com.seekersoftvendingapp.network.entity.borrowrecord.BorrowRecordResBody;
import com.seekersoftvendingapp.network.entity.takeoutrecord.TakeoutRecordReqBody;
import com.seekersoftvendingapp.network.entity.takeoutrecord.TakeoutRecordResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by kjh08490 on 2016/12/20.
 */

public class NTrack {
    private Context mContext;
    private ExecutorService mExecutor;
    private EventController eventController;

    public NTrack(Context ctx) {
        this.mContext = ctx;
        eventController = new EventController(mContext);
        this.mExecutor = Executors.newSingleThreadExecutor();
    }

    /**
     * ------------------------------借还记录接口----------------------------
     *
     * @param borrowRecord
     */
    public void setBorrrowReturnRecordCommand(final BorrowRecord borrowRecord) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                NTrack.this.eventController.addBorrowReturnRecord(borrowRecord);
                NTrack.this.deliverBorrowReturnRecordCommonEvent(1);
            }
        };
        this.mExecutor.execute(command);
    }

    public void deliverBorrowReturnRecordCommonEvent(int size) {
        if (this.eventController.getTakeOutRecordSize() >= size) {
            borrowReturnRecordRequest();
        }
    }

    /**
     * 借还记录接口 同步加载 POST
     */
    public void borrowReturnRecordRequest() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        BorrowRecordReqBody borrowRecordReqBody = new BorrowRecordReqBody();
        borrowRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        borrowRecordReqBody.record.addAll(eventController.getBorrowReturnRecordList());
        Gson gson = new Gson();
        String josn = gson.toJson(borrowRecordReqBody);
        Log.e("json", josn);
        Call<BorrowRecordResBody> postAction = service.borrowRecord(borrowRecordReqBody);
        try {
            Response<BorrowRecordResBody> response = postAction.execute();
            if (response != null && response.body() != null) {
                // 同步成功
                eventController.setBorrowReturnRecordOk();
            } else {
                Log.e("request", "borrow Record: Failure");
            }
        } catch (IOException e) {
            Log.e("request", "borrow Record: IOException");
        }
    }


    /**
     * -----------------------------------设备成功出货后回调此接口上传交易成功记录---------------------------------------------------
     * 写数据库+addToList --- 请求网络记录接口 --- 成功（更新数据库）（remove缓存list）;失败（无需操作）（继续保留待提交信息）
     *
     * @param takeOutRecord
     */
    public void setTakeOutRecordCommand(final TakeoutRecord takeOutRecord) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                NTrack.this.eventController.addTakeOutRecord(takeOutRecord);
                NTrack.this.deliverTakeOutRecordCommonEvent(1);
            }
        };
        this.mExecutor.execute(command);
    }

    private void deliverTakeOutRecordCommonEvent(int size) {
        if (this.eventController.getTakeOutRecordSize() >= size) {
            takeoutRecordRequest();
        }
    }

    /**
     * 提交取货记录 同步加载 POST
     */
    private void takeoutRecordRequest() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        TakeoutRecordReqBody takeoutRecordReqBody = new TakeoutRecordReqBody();
        takeoutRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        takeoutRecordReqBody.record.addAll(eventController.getTakeOutRecordList());
        Gson gson = new Gson();
        String josn = gson.toJson(takeoutRecordReqBody);
        Log.e("json", josn);
        Call<TakeoutRecordResBody> postAction = service.takeoutRecord(takeoutRecordReqBody);
        try {
            Response<TakeoutRecordResBody> response = postAction.execute();
            if (response != null && response.body() != null) {
                // 成功更新数据库标识
                eventController.setTaketOutRecordOk();
            } else {
                // 失败不需要更新数据库标识
                Log.e("request", "takeout Record: Failure");
            }
        } catch (IOException e) {
            // 失败不需要更新数据库标识
            Log.e("request", "takeout Record: IOException");
        }
    }
}
