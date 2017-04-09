package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
 * 成功 失败 页面（包括成功，失败描述信息页面） 共用
 * Created by kjh08490 on 2016/11/25.
 */

public class HandleBorrowResultActivity extends BaseActivity {


    private TextView tv_handle_result;

    private String cardId = "";

    private EmpCardDao empCardDao;
    private Passage passage;
    private EmpCard empCard;

    private static final int GEZI = 1;

    private ImageView vi_flag;

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
        setContentView(R.layout.activity_handleresult);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        vi_flag = (ImageView) findViewById(R.id.vi_flag);

        setTitle("借还结果");

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        empCardDao = daoSession.getEmpCardDao();

        passage = (Passage) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        cardId = getIntent().getStringExtra(SeekerSoftConstant.CardNum);

        tv_handle_result = (TextView) findViewById(R.id.tv_handle_result);

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HandleBorrowResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        handleReadCardAfterBusniess(cardId);
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
        vi_flag.setVisibility(View.VISIBLE);

        ShipmentObject shipmentObject = new ShipmentObject();
        try {
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
            // 格子柜子
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
            BorrowRecord borrowRecord = new BorrowRecord(null, true, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, true, true, new Date(), "", "", "");
            passage.setStock(0);
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
            Track.getInstance(HandleBorrowResultActivity.this).setBorrowReturnRecordCommand(passage, borrowRecord);

            // 串口打开柜子成功
            handleResult(new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG));
        } else {
            // 串口操作失败
            BorrowRecord borrowRecord = new BorrowRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, true, false, new Date(), "", "", "");
            Track.getInstance(HandleBorrowResultActivity.this).setBorrowReturnRecordCommand(passage, borrowRecord, objectId);

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
        if (takeOutError != null) {
            if (TextUtils.isEmpty(takeOutError.serverMsg)) {
                tv_handle_result.setText("服务器检测：" + takeOutError.getTakeOutMsg());
            } else {
                tv_handle_result.setText("本地检测：" + takeOutError.serverMsg);
            }
        }
        btn_return_mainpage.setVisibility(View.VISIBLE);
        countDownTimer.start();
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
            return new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG);


//            List<EmpPower> listEmpPowers = empPowerDao.queryBuilder()
//                    .where(EmpPowerDao.Properties.IsDel.eq(false))
//                    .where(EmpPowerDao.Properties.Emp.like(empCard.getEmp()))
//                    .where(EmpPowerDao.Properties.Product.eq(productId)).list();
//            if (listEmpPowers == null || listEmpPowers.size() == 0) {
//                // 此商品暂时没有赋予出货权限
//                return new TakeOutError(TakeOutError.PRO_HAS_NOPOWER_FLAG);
//            } else {
//                return new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG);
//            }
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
                Toast.makeText(HandleBorrowResultActivity.this, "网络链接问题，本地进行借货操作", Toast.LENGTH_LONG).show();
                TakeOutError takeOutError = localBorrowPro(passage.getProduct(), cardId);
                outProResult(takeOutError);
            }
        });
    }


    @Override
    public int setEndTime() {
        return SeekerSoftConstant.ENDTIEMSHORT;
    }
}