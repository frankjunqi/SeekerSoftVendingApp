package com.seekersoftvendingapp.track;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.table.AdminCardDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.EmployeeDao;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.database.table.ProductDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.SynchroBaseDataResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 基础数据同步的接口更新
 * Created by kjh08490 on 2016/12/22.
 */

public class BaseDateNTrack implements InterfaceTrack {

    public final static int SENDBASEDATEUPDATE = 1;

    private Context mContext;
    /**
     * 基础数据 GET
     */
    private AdminCardDao adminCardDao;
    private EmployeeDao employeeDao;
    private EmpPowerDao empPowerDao;
    private PassageDao passageDao;
    private ProductDao productDao;

    Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SENDBASEDATEUPDATE:
                    asyncBaseData();
                    break;
            }
        }
    };

    private boolean StopMesage = false;

    public BaseDateNTrack(Context mContext) {
        this.mContext = mContext;
        DaoSession daoSession = ((SeekersoftApp) mContext).getDaoSession();
        adminCardDao = daoSession.getAdminCardDao();
        employeeDao = daoSession.getEmployeeDao();
        empPowerDao = daoSession.getEmpPowerDao();
        passageDao = daoSession.getPassageDao();
        productDao = daoSession.getProductDao();
    }

    /**
     * 基础数据的同步
     */
    public void asyncBaseData() {
        final String timestamp = String.valueOf(System.currentTimeMillis());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                asyncGetBaseDataRequest(timestamp);
            }
        };
        this.mExecutor.execute(runnable);
    }


    private void asyncGetBaseDataRequest(final String timestamp) {
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<SynchroBaseDataResBody> updateAction = service.getSynchroBaseData("api", "getData", SeekerSoftConstant.DEVICEID, timestamp);
        Log.e("json", "getSynchroBaseData = " + updateAction.request().url().toString());
        try {
            Response<SynchroBaseDataResBody> response = updateAction.execute();
            if (response != null && response.body() != null) {
                adminCardDao.insertOrReplaceInTx(response.body().getAdminCardList());
                employeeDao.insertOrReplaceInTx(response.body().getEmployeeList());
                empPowerDao.insertOrReplaceInTx(response.body().getEmpPowerList());

                if (TextUtils.isEmpty(timestamp)) {
                    // 第一次请求，直接全部更新
                    passageDao.insertOrReplaceInTx(response.body().getPassageList());
                } else {
                    // 更新操作，不更新库存，不更新借还状态
                    List<Passage> onlineList = response.body().getPassageList();
                    List<Passage> passageList = passageDao.queryBuilder()
                            .where(PassageDao.Properties.IsDel.eq(false))
                            .where(PassageDao.Properties.Stock.gt(0)) // 判断库存
                            .list();
                    for (Passage passage : onlineList) {
                        for (Passage dbPassage : passageList) {
                            if (dbPassage.getSeqNo().equals(passage.getSeqNo()) && dbPassage.getFlag().equals(passage.getFlag())) {
                                passage.setBorrowState(dbPassage.getBorrowState());
                                passage.setStock(dbPassage.getStock());
                            }
                        }
                    }
                    passageDao.insertOrReplaceInTx(onlineList);
                }

                productDao.insertOrReplaceInTx(response.body().getProductList());
                // 成功初始化基础数据
            } else {
                // TODO 失败
            }
        } catch (IOException e) {
            // TODO 异常
        }
        if (!StopMesage) {
            mHandle.sendEmptyMessageDelayed(SENDBASEDATEUPDATE, SeekerSoftConstant.TIMELOGN);
        } else {
            // do noting
        }
    }

    public void removeMessage() {
        mHandle.removeMessages(SENDBASEDATEUPDATE);
        StopMesage = true;
    }

    @Override
    public void synchroAllDataToServer() {
        // do nothing ,no data sync to server
    }
}
