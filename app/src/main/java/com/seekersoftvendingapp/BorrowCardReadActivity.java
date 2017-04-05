package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpCard;
import com.seekersoftvendingapp.database.table.EmpCardDao;
import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.ErrorRecord;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.ResultObj;
import com.seekersoftvendingapp.network.entity.borrow.BorrowResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.seekersoftvendingapp.newtakeoutserial.ShipmentObject;
import com.seekersoftvendingapp.track.Track;
import com.seekersoftvendingapp.util.DataFormat;
import com.seekersoftvendingapp.util.LogCat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.util.TakeOutError;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 2. 借 读卡 页面
 * Created by kjh08490 on 2016/11/25.
 */

public class BorrowCardReadActivity extends BaseActivity {

    private EditText et_getcard;
    private RelativeLayout ll_keyboard;

    private String cardId = "";

    private EmpPowerDao empPowerDao;
    private EmpCardDao empCardDao;
    private Passage passage;
    private EmpCard empCard;

    private static final int GEZI = 1;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GEZI:
                    ResultObj resultObj = (ResultObj) msg.obj;
                    handleStoreSerialPort(resultObj.isSuccess, resultObj.objectId);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_cardread);
        setTitle("输入卡号...");
        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        empPowerDao = daoSession.getEmpPowerDao();
        empCardDao = daoSession.getEmpCardDao();

        ll_keyboard = (RelativeLayout) findViewById(R.id.ll_keyboard);
        ll_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        passage = (Passage) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        if (passage == null) {
            Toast.makeText(BorrowCardReadActivity.this, "输入货道信息有异常，请重试...", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BorrowCardReadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        et_getcard = (EditText) findViewById(R.id.et_getcard);
        et_getcard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().endsWith("\n")) {
                    cardId = s.toString().replace("\n", "");
                    if (TextUtils.isEmpty(cardId)) {
                        // 读到的卡号为null or ""
                        ErrorRecord errorRecord = new ErrorRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, "借货", "读到的卡号为空.", DataFormat.getNowTime(), "", "", "");
                        Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
                        Toast.makeText(BorrowCardReadActivity.this, "请重新读卡...", Toast.LENGTH_SHORT).show();
                    } else {
                        // 处理业务
                        handleReadCardAfterBusniess(cardId);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        countDownTimer.start();
    }

    /**
     * 本地判断是否可以进行借出去
     */
    /**
     * 处理读到卡之后的业务
     */
    private void handleReadCardAfterBusniess(String cardId) {
        // 网络判断是否可以出货(网络优先)
        if (SeekerSoftConstant.NETWORKCONNECT) {
            isBorrowPro(cardId);
        } else {
            // 本地判断是否可以出货
            TakeOutError takeOutError = localBorrowPro(passage.getProduct(), cardId);
            outProResult(takeOutError);
        }
    }

    /**
     * 出货并且进行成功失败的跳转
     *
     * @param takeOutError
     */
    private void outProResult(TakeOutError takeOutError) {
        if (takeOutError.isSuccess()) {
            // 串口柜子passageID操作
            cmdBufferVendingSerial("");
        } else {
            et_getcard.setText("");
            // 不可以出货
            handleResult(takeOutError);
        }
    }

    /**
     * 打开柜子串口设备出货
     *
     * @return
     */
    private void cmdBufferVendingSerial(final String objectId) {
        ShipmentObject shipmentObject = new ShipmentObject();
        try {
            NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject).setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                @Override
                public void onCmdCallBack(boolean isSuccess) {

                    Message msg = new Message();
                    msg.what = GEZI;

                    ResultObj resultObj = new ResultObj();
                    resultObj.isSuccess = isSuccess;
                    resultObj.objectId = objectId;
                    msg.obj = resultObj;

                    mHandler.sendMessage(msg);
                }
            });
            // 格子柜子
            shipmentObject.containerNum = TextUtils.isEmpty(passage.getFlag()) ? 1 : Integer.parseInt(passage.getFlag()) + 1;
            shipmentObject.proNum = Integer.parseInt(passage.getSeqNo());
            // 需要生成唯一码
            shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
        } catch (Exception e) {
            handleStoreSerialPort(false, objectId);
        }
    }

    private void handleStoreSerialPort(boolean isSuccess, String objectId) {
        if (isSuccess) {
            // 打开成功之后逻辑 加入线程池队列 --- 交付线程池进行消费入本地库以及通知远程服务端 -- 本地数据库进行库存的消耗
            BorrowRecord borrowRecord = new BorrowRecord(null, true, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, true, true, new Date(), "", "", "");
            passage.setStock(passage.getStock() - 1);
            passage.setBorrowState(true);
            passage.setUsed(empCard != null ? empCard.getEmp() : "");
            // 更新此人已经借走货物
            //passage.setBorrowUser(employee != null ? employee.getObjectId() : "");
            if (TextUtils.isEmpty(objectId)) {
                // 本地消费
                borrowRecord.setIsFlag(false);
            } else {
                // 网络消费
                borrowRecord.setIsFlag(true);
            }
            Track.getInstance(BorrowCardReadActivity.this).setBorrowReturnRecordCommand(passage, borrowRecord);

            // 串口打开柜子成功
            handleResult(new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG));
        } else {
            // 串口操作失败
            BorrowRecord borrowRecord = new BorrowRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, true, false, new Date(), "", "", "");
            Track.getInstance(BorrowCardReadActivity.this).setBorrowReturnRecordCommand(passage, borrowRecord, objectId);

            // 串口打开柜子失败
            handleResult(new TakeOutError(TakeOutError.OPEN_GEZI_SERIAL_FAILED_FLAG));
        }
    }

    /**
     * 处理本地消费结果（到结果页面）
     */
    private void handleResult(TakeOutError takeOutError) {
        if (!takeOutError.isSuccess()) {
            ErrorRecord errorRecord = new ErrorRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, "消费问题: " + takeOutError.serverMsg, takeOutError.getTakeOutMsg(), DataFormat.getNowTime(), "", "", "");
            Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
        }
        Intent intent = new Intent(BorrowCardReadActivity.this, HandleResultActivity.class);
        intent.putExtra(SeekerSoftConstant.TAKEOUTERROR, takeOutError);
        startActivity(intent);
        this.finish();
    }


    /**
     * 当无网络时候进行本地判断，否则全部走网络判断为准；
     * 库存以本地为准；
     * 本地进行判断是否可以出货
     */
    private TakeOutError localBorrowPro(String productId, String cardId) {
        // 具体查询card对应的用户
        List<EmpCard> empCardList = empCardDao.queryBuilder()
                .where(EmpCardDao.Properties.IsDel.eq(false))
                .where(EmpCardDao.Properties.Card.like(cardId)).list();
        if (empCardList != null && empCardList.size() > 0) {
            empCard = empCardList.get(0);
            List<EmpPower> listEmpPowers = empPowerDao.queryBuilder()
                    .where(EmpPowerDao.Properties.IsDel.eq(false))
                    .where(EmpPowerDao.Properties.Emp.like(empCard.getEmp()))
                    .where(EmpPowerDao.Properties.Product.eq(productId)).list();
            if (listEmpPowers == null || listEmpPowers.size() == 0) {
                // 此商品暂时没有赋予出货权限
                return new TakeOutError(TakeOutError.PRO_HAS_NOPOWER_FLAG);
            } else {
                return new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG);
            }
        } else {
            // 无此员工
            return new TakeOutError(TakeOutError.HAS_NOEMPLOYEE_FLAG);
        }
    }

    /**
     * （接口）判断是否能借
     */
    private void isBorrowPro(final String cardId) {
        showProgress();
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<BorrowResBody> updateAction = service.borrow(SeekerSoftConstant.DEVICEID, cardId, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo());
        LogCat.e("borrow = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<BorrowResBody>() {
            @Override
            public void onResponse(Call<BorrowResBody> call, Response<BorrowResBody> response) {
                if (response != null && response.body() != null && response.body().data.result) {
                    cmdBufferVendingSerial(response.body().data.objectId);
                } else {
                    TakeOutError takeOutError = new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG);
                    takeOutError.serverMsg = response != null && response.body() != null && !TextUtils.isEmpty(response.body().message) ? response.body().message : "";
                    handleResult(takeOutError);
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<BorrowResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(BorrowCardReadActivity.this, "网络链接问题，本地进行借货操作", Toast.LENGTH_LONG).show();
                TakeOutError takeOutError = localBorrowPro(passage.getProduct(), cardId);
                outProResult(takeOutError);
            }
        });
    }
}
