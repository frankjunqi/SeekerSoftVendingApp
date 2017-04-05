package com.seekersoftvendingapp.track;

import android.content.Context;

import com.google.gson.Gson;
import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.ErrorRecord;
import com.seekersoftvendingapp.database.table.ErrorRecordDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.error.ErrorReqBody;
import com.seekersoftvendingapp.network.entity.error.ErrorResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.LogCat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 错误日志上传
 * Created by kjh08490 on 2016/12/23.
 */

public class ErrorNTrack implements InterfaceTrack {
    private Context mContext;
    // 等待提交的错误日志记录list
    private List<ErrorRecord> errorRecordList = new ArrayList<>();
    private ErrorRecordDao errorRecordDao;

    public ErrorNTrack(Context ctx) {
        this.mContext = ctx;
        DaoSession daoSession = ((SeekersoftApp) mContext.getApplicationContext()).getDaoSession();
        errorRecordDao = daoSession.getErrorRecordDao();
    }

    public void setTakeOutRecord(final ErrorRecord errorRecord) {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                if (errorRecord == null) {
                    return;
                }
                if (errorRecord.getIsFlag()) {
                    // IsDel: true标识同步；
                    errorRecordDao.insertOrReplaceInTx(errorRecord);
                } else {
                    // no network or fail need to send to server:(往数据库中更新这条记录 + 等待提交的出货记录list插入这条数据)
                    errorRecordDao.insertOrReplaceInTx(errorRecord);
                    errorRecordList.add(errorRecord);
                    asyncErrorRequest();
                }
            }
        };
        this.mExecutor.execute(command);
    }

    /**
     * 提交异常记录 POST
     */
    private void asyncErrorRequest() {
        if (errorRecordList == null || errorRecordList.size() == 0) {
            return;
        }
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        ErrorReqBody errorRecordReqBody = new ErrorReqBody();
        errorRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        errorRecordReqBody.error.addAll(errorRecordList);
        Gson gson = new Gson();
        String josn = gson.toJson(errorRecordReqBody);
        LogCat.e("error = " + josn);
        Call<ErrorResBody> postAction = service.error(errorRecordReqBody);
        try {
            Response<ErrorResBody> response = postAction.execute();
            if (response != null && response.body() != null) {
                setErrorRecordOk();
            } else {
                LogCat.e("error : Failure");
            }
        } catch (IOException e) {

        }
    }


    /**
     * IsDel：
     * true标识同步完成；
     * false: 未同步；
     */
    private void setErrorRecordOk() {
        for (ErrorRecord errorRecord : errorRecordList) {
            errorRecord.setIsFlag(true);
        }
        errorRecordDao.insertOrReplaceInTx(errorRecordList);
        errorRecordList.clear();
    }

    @Override
    public void synchroAllDataToServer() {
        Runnable command = new Runnable() {
            @Override
            public void run() {
                if (errorRecordList == null || errorRecordList.size() == 0) {
                    errorRecordList = errorRecordDao.queryBuilder().where(ErrorRecordDao.Properties.IsFlag.eq(false)).list();
                }
                asyncErrorRequest();
            }
        };
        this.mExecutor.execute(command);
    }
}
