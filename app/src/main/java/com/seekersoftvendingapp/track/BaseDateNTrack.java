package com.seekersoftvendingapp.track;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.seekersoftvendingapp.SeekersoftApp;
import com.seekersoftvendingapp.database.table.AdminCardDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpCardDao;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.database.table.ProductDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.SynchroBaseDataResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.util.LogCat;
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
    private EmpCardDao empCardDao;
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
        empCardDao = daoSession.getEmpCardDao();
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
        Call<SynchroBaseDataResBody> updateAction = service.getSynchroBaseData(SeekerSoftConstant.DEVICEID, timestamp);
        LogCat.e("5 min update: getSynchroBaseData = " + updateAction.request().url().toString());
        try {
            Response<SynchroBaseDataResBody> response = updateAction.execute();
            if (response != null && response.body() != null) {

                SeekerSoftConstant.machine = response.body().getMachine();
                SeekerSoftConstant.phoneDesc = response.body().getPhoneDesc();
                SeekerSoftConstant.versionDesc = response.body().getModle();

                if (response.body().getAdminCardList() != null && response.body().getAdminCardList().size() > 0) {
                    adminCardDao.insertOrReplaceInTx(response.body().getAdminCardList());
                }
                if (response.body().getEmpCardList() != null && response.body().getEmpCardList().size() > 0) {
                    empCardDao.insertOrReplaceInTx(response.body().getEmpCardList());
                }
                if (response.body().getEmpPowerList() != null && response.body().getEmpPowerList().size() > 0) {
                    empPowerDao.insertOrReplaceInTx(response.body().getEmpPowerList());
                }
                if (response.body().getProductList() != null && response.body().getProductList().size() > 0) {
                    productDao.insertOrReplaceInTx(response.body().getProductList());
                }

                if (TextUtils.isEmpty(timestamp)) {
                    // 第一次请求，直接全部更新
                    passageDao.insertOrReplaceInTx(response.body().getPassageList());
                } else {
                    // 更新操作，不更新库存，不更新借还状态
                    List<Passage> onlineList = response.body().getPassageList();
                    if (onlineList.size() > 0) {
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
                }
            }
        } catch (IOException e) {
            // do nothing
        }
        if (!StopMesage) {
            mHandle.sendEmptyMessageDelayed(SENDBASEDATEUPDATE, SeekerSoftConstant.TIMELOGN);
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
