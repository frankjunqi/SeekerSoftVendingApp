package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpCard;
import com.seekersoftvendingapp.database.table.EmpCardDao;
import com.seekersoftvendingapp.database.table.ErrorRecord;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.ResultObj;
import com.seekersoftvendingapp.network.entity.returnpro.ReturnProResBody;
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
 * 3. 还 读卡 页面
 * Created by kjh08490 on 2016/11/25.
 */

public class ReturnCardReadActivity extends BaseActivity {

    private EditText et_getcard;
    private RelativeLayout ll_keyboard;

    private String cardId = "";

    private EmpCardDao empCardDao;

    private Passage passage;

    private TextView tv_upup;

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
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_cardread);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("刷卡确认");

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        empCardDao = daoSession.getEmpCardDao();

        passage = (Passage) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        if (passage == null) {
            Toast.makeText(ReturnCardReadActivity.this, "输入货道信息有异常，请重试...", Toast.LENGTH_SHORT).show();
            this.finish();
        }

        ll_keyboard = (RelativeLayout) findViewById(R.id.ll_keyboard);
        ll_keyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnCardReadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        et_getcard = (EditText) findViewById(R.id.et_getcard);

        et_getcard.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (KeyEvent.KEYCODE_ENTER == i && KeyEvent.ACTION_DOWN == keyEvent.getAction()) {
                    cardId = et_getcard.getText().toString().replace("\n", "");
                    if (TextUtils.isEmpty(cardId)) {
                        // 读到的卡号为null or ""
                        ErrorRecord errorRecord = new ErrorRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, "还货", "读到的卡号为空.", DataFormat.getNowTime(), "", "", "");
                        Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
                        Toast.makeText(ReturnCardReadActivity.this, "请重新读卡...", Toast.LENGTH_SHORT).show();
                    } else {
                        // 处理业务
                        gotoResult();
                        //handleReadCardAfterBusniess(cardId);
                    }
                    return true;
                }
                return false;
            }
        });

//        et_getcard.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.toString().endsWith("\n")) {
//                    cardId = s.toString().replace("\n", "");
//                    if (TextUtils.isEmpty(cardId)) {
//                        // 读到的卡号为null or ""
//                        ErrorRecord errorRecord = new ErrorRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, "还货", "读到的卡号为空.", DataFormat.getNowTime(), "", "", "");
//                        Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
//                        Toast.makeText(ReturnCardReadActivity.this, "请重新读卡...", Toast.LENGTH_SHORT).show();
//                    } else {
//                        // 处理业务
//                        handleReadCardAfterBusniess(cardId);
//                    }
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
        countDownTimer.start();

        tv_upup = (TextView) findViewById(R.id.tv_upup);
        tv_upup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });
    }

    public void gotoResult() {
        Intent intent = new Intent(ReturnCardReadActivity.this, HandleReturnResultActivity.class);
        intent.putExtra(SeekerSoftConstant.PASSAGE, passage);
        intent.putExtra(SeekerSoftConstant.CardNum, cardId);
        startActivity(intent);
        this.finish();
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
            isReturnPro(cardId);
        } else {
            // 本地判断是否可以出货
            TakeOutError takeOutError = localReturnPro(cardId);
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
            cmdBufferStoreSerial("");
        } else {
            // 不可以出货
            handleResult(takeOutError);
        }
    }

    /**
     * 打开柜子串口设备出货
     *
     * @return
     */
    private void cmdBufferStoreSerial(final String objectId) {
        ShipmentObject shipmentObject = new ShipmentObject();
        try {
            // 格子柜子
            NewVendingSerialPort.SingleInit().setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
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
            shipmentObject.containerNum = TextUtils.isEmpty(passage.getFlag()) ? 1 : Integer.parseInt(passage.getFlag()) + 1;
            shipmentObject.proNum = Integer.parseInt(passage.getSeqNo());
            // 需要生成唯一码
            shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
            NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject);
        } catch (Exception e) {
            handleStoreSerialPort(false, objectId);
        }

    }


    private void handleStoreSerialPort(boolean isSuccess, String objectId) {
        if (isSuccess) {
            // 打开成功之后逻辑 加入线程池队列 --- 交付线程池进行消费入本地库以及通知远程服务端 -- 本地数据库进行库存的消耗
            BorrowRecord borrowRecord = new BorrowRecord(null, true, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, false, true, new Date(), "", "", "");
            passage.setStock(passage.getStock() + passage.getCapacity());
            passage.setBorrowState(false);
            passage.setUsed("");
            // 更新此人已经还货物
            if (TextUtils.isEmpty(objectId)) {
                // 本地消费
                borrowRecord.setIsFlag(false);
            } else {
                // 网络消费
                borrowRecord.setIsFlag(true);
            }
            Track.getInstance(ReturnCardReadActivity.this).setBorrowReturnRecordCommand(passage, borrowRecord);

            // 串口打开柜子成功
            handleResult(new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG));
        } else {
            //  调用失败接口 如果接口错误，则加入到同步队列里面去
            BorrowRecord borrowRecord = new BorrowRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, false, false, new Date(), "", "", "");
            Track.getInstance(ReturnCardReadActivity.this).setBorrowReturnRecordCommand(passage, borrowRecord, objectId);
            // 串口打开柜子失败
            handleResult(new TakeOutError(TakeOutError.OPEN_GEZI_SERIAL_FAILED_FLAG));
        }
    }

    /**
     * 处理本地消费结果（到结果页面）
     */
    private void handleResult(TakeOutError takeOutError) {
        if (!takeOutError.isSuccess()) {
            et_getcard.setText("");
            ErrorRecord errorRecord = new ErrorRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, "消费问题: " + takeOutError.serverMsg, takeOutError.getTakeOutMsg(), DataFormat.getNowTime(), "", "", "");
            Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
        }
        Intent intent = new Intent(ReturnCardReadActivity.this, HandleResultActivity.class);
        intent.putExtra(SeekerSoftConstant.TAKEOUTERROR, takeOutError);
        startActivity(intent);
        this.finish();
    }


    /**
     * 当无网络时候进行本地判断，否则全部走网络判断为准；
     * 库存以本地为准；
     * 本地进行判断是否可以出货
     */
    private TakeOutError localReturnPro(String cardId) {
        // 具体查询card对应的用户
        List<EmpCard> empCardList = empCardDao.queryBuilder()
                .where(EmpCardDao.Properties.IsDel.eq(false))
                .where(EmpCardDao.Properties.Card.like(cardId))
                .list();
        if (empCardList != null && empCardList.size() > 0) {
            EmpCard empCard = empCardList.get(0);
            if (!TextUtils.isEmpty(empCard.getEmp()) && empCard.getEmp().equals(passage.getUsed())) {
                return new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG);
            } else {
                // 此人无权限
                return new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG);
            }
        } else {
            // 无此员工
            return new TakeOutError(TakeOutError.HAS_NOEMPLOYEE_FLAG);
        }
    }


    /**
     * （接口）判断是否能出货
     */
    private void isReturnPro(final String cardId) {
        showProgress();
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<ReturnProResBody> updateAction = service.returnPro(SeekerSoftConstant.DEVICEID, cardId, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo());
        LogCat.e("returnPro = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<ReturnProResBody>() {
            @Override
            public void onResponse(Call<ReturnProResBody> call, Response<ReturnProResBody> response) {
                if (response != null && response.body() != null && response.body().data.result) {
                    cmdBufferStoreSerial(response.body().data.objectId);
                } else {
                    TakeOutError takeOutError = new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG);
                    takeOutError.serverMsg = response != null && response.body() != null && !TextUtils.isEmpty(response.body().message) ? response.body().message : "";
                    handleResult(takeOutError);
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<ReturnProResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(ReturnCardReadActivity.this, "网络链接问题，本地进行还货操作", Toast.LENGTH_LONG).show();
                TakeOutError takeOutError = localReturnPro(cardId);
                handleResult(takeOutError);
            }
        });
    }

}
