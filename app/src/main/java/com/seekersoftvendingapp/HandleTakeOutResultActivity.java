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

import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpCard;
import com.seekersoftvendingapp.database.table.EmpCardDao;
import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.ErrorRecord;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.TakeoutRecord;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.ResultObj;
import com.seekersoftvendingapp.network.entity.seekwork.MRoad;
import com.seekersoftvendingapp.network.entity.takeout.TakeOutResBody;
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

public class HandleTakeOutResultActivity extends BaseActivity {


    private TextView tv_handle_result;

    private MRoad passage;
    private int number = 1;
    private String cardId = "";


    private int recordNum = 0;
    private int recordSuccess = 0;

    private static final int LUOWEN = 1;
    private static final int GEZI = 2;

    private ImageView vi_flag;


    // 判断是否是格子柜子消费
    private boolean isStoreSend = false;

    private EmpPowerDao empPowerDao;
    private EmpCardDao empCardDao;
    private EmpPower empPower;

    private Handler mHnadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LUOWEN:
                    ResultObj resultObj = (ResultObj) msg.obj;
                    luowen(resultObj.isSuccess, resultObj.objectId);
                    break;
                case GEZI:
                    ResultObj resultObjG = (ResultObj) msg.obj;
                    gezi(resultObjG.isSuccess, resultObjG.objectId);
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

        setTitle("取货结果");

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        empPowerDao = daoSession.getEmpPowerDao();
        empCardDao = daoSession.getEmpCardDao();

        passage = (MRoad) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        number = getIntent().getIntExtra(SeekerSoftConstant.TakeoutNum, 1);
        cardId = getIntent().getStringExtra(SeekerSoftConstant.CardNum);

        if (passage != null) {
            // 判断是否是格子柜消费
        }

        tv_handle_result = (TextView) findViewById(R.id.tv_handle_result);

        vi_flag = (ImageView) findViewById(R.id.vi_flag);

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HandleTakeOutResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                HandleTakeOutResultActivity.this.finish();
            }
        });

        handleReadCardAfterBusniess(cardId);
    }

    @Override
    public int setEndTime() {
        return SeekerSoftConstant.ENDTIEMSHORT;
    }

    /**
     * 处理读到卡之后的业务
     */
    private void handleReadCardAfterBusniess(String cardId) {
        // 网络判断是否可以出货(网络优先)
        if (SeekerSoftConstant.NETWORKCONNECT) {
            isTakeOutPro(cardId);
        } else {
            // 本地判断是否可以出货
            TakeOutError takeOutError = localTakeOutPro(passage.getProduct(), cardId);
            outProResult(takeOutError);
        }
    }

    /**
     * （接口）判断是否能出货
     */
    private void isTakeOutPro(final String cardId) {
        showProgress();
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<TakeOutResBody> updateAction = service.takeOut(SeekerSoftConstant.DEVICEID, cardId, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), String.valueOf(number));
        LogCat.e("takeOut = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<TakeOutResBody>() {
            @Override
            public void onResponse(Call<TakeOutResBody> call, Response<TakeOutResBody> response) {
                if (response != null && response.body() != null && response.body().data.result) {
                    cmdBufferVendingStoreSerial(response.body().data.objectId);
                } else {
                    // 此人没有权限,不可以出货
                    TakeOutError takeOutError = new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG);
                    takeOutError.serverMsg = response != null && response.body() != null && !TextUtils.isEmpty(response.body().message) ? response.body().message : "";
                    handleResult(takeOutError);
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<TakeOutResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(HandleTakeOutResultActivity.this, "网络链接问题，本地进行出货操作", Toast.LENGTH_LONG).show();
                TakeOutError takeOutError = localTakeOutPro(passage.getProduct(), cardId);
                outProResult(takeOutError);
            }
        });
    }

    /**
     * 当无网络时候进行本地判断，否则全部走网络判断为准；
     * 库存以本地为准；
     * 本地进行判断是否可以出货
     */
    private TakeOutError localTakeOutPro(String productId, String cardId) {
        // 具体查询card对应的用户
        List<EmpCard> empCardList = empCardDao.queryBuilder()
                .where(EmpCardDao.Properties.IsDel.eq(false))
                .where(EmpCardDao.Properties.Card.like(cardId)).list();
        if (empCardList != null && empCardList.size() > 0) {
            EmpCard empCard = empCardList.get(0);
            // 同一个商品 权限详细信息list 消费频次 周期消费次数
            List<EmpPower> listEmpPowers = empPowerDao.queryBuilder()
                    .where(EmpPowerDao.Properties.IsDel.eq(false))
                    .where(EmpPowerDao.Properties.Emp.like(empCard.getEmp()))
                    .where(EmpPowerDao.Properties.Product.eq(productId)).list();
            if (listEmpPowers == null || listEmpPowers.size() == 0) {
                // 此商品暂时没有赋予出货权限
                return new TakeOutError(TakeOutError.PRO_HAS_NOPOWER_FLAG);
            }
            empPower = listEmpPowers.get(0);
            int used = empPower.getUsed();
            int count = used + number;
            if (count <= empPower.getCount()) {
                return new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG);
            } else {
                // 此人消费次数已满，不可以进行消费
                return new TakeOutError(TakeOutError.FAILE_TAKEOUT_FLAG);
            }
        } else {
            // 无此员工
            return new TakeOutError(TakeOutError.HAS_NOEMPLOYEE_FLAG);
        }
    }

    /**
     * 出货并且进行成功失败的跳转
     *
     * @param takeOutError
     */
    private void outProResult(TakeOutError takeOutError) {
        if (takeOutError.isSuccess()) {
            // 串口螺纹passageID操作 || 格子消费柜子的passageId操作
            cmdBufferVendingStoreSerial("");
        } else {
            // 不可以出货
            handleResult(takeOutError);
        }
    }

    /**
     * 打开螺纹柜子串口设备出货
     *
     * @param objectId 出货的服务端的记录的objectid
     */
    private void cmdBufferVendingStoreSerial(final String objectId) {
        vi_flag.setVisibility(View.VISIBLE);
        if (isStoreSend) {
            NewVendingSerialPort.SingleInit().setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                @Override
                public void onCmdCallBack(boolean isSuccess) {
                    Message msg = new Message();
                    msg.what = GEZI;
                    ResultObj resultObj = new ResultObj();
                    resultObj.isSuccess = isSuccess;
                    resultObj.objectId = objectId;
                    msg.obj = resultObj;
                    mHnadler.sendMessage(msg);
                }
            });
            ShipmentObject shipmentObject = new ShipmentObject();
            // 格子柜子
            shipmentObject.containerNum = TextUtils.isEmpty(passage.getFlag()) ? 1 : Integer.parseInt(passage.getFlag()) + 1;
            shipmentObject.proNum = Integer.parseInt(passage.getSeqNo());
            shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
            NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject);
        } else {
            NewVendingSerialPort.SingleInit().setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                @Override
                public void onCmdCallBack(boolean isSuccess) {
                    Message msg = new Message();
                    msg.what = LUOWEN;
                    ResultObj resultObj = new ResultObj();
                    resultObj.isSuccess = isSuccess;
                    resultObj.objectId = objectId;
                    msg.obj = resultObj;
                    mHnadler.sendMessage(msg);
                }
            });
            for (int i = 0; i < number; i++) {
                ShipmentObject shipmentObject = new ShipmentObject();
                // 螺纹柜子
                shipmentObject.containerNum = TextUtils.isEmpty(passage.getFlag()) ? 1 : Integer.parseInt(passage.getFlag()) + 1;
                shipmentObject.proNum = Integer.parseInt(passage.getSeqNo());
                shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
                NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject);
            }
        }
    }

    private void gezi(boolean isSuccess, String objectId) {
        if (isSuccess) {
            recordSuccess = passage.getStock();
            handleNewVendingSerialPort(true);
        } else {
            handleNewVendingSerialPort(false);
            //  调用失败接口 如果接口错误，则加入到同步队列里面去
            TakeoutRecord takeoutRecord = new TakeoutRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(),
                    cardId, passage.getProduct(), DataFormat.getNowTime(), -1, "", "", "");
            Track.getInstance(HandleTakeOutResultActivity.this).setTakeOutRecordCommand(passage, takeoutRecord, objectId);
        }
    }

    private void luowen(boolean isSuccess, String objectId) {
        recordNum++;
        if (!isSuccess) {
            if (number > 1) {
                tv_handle_result.setText("取货第" + recordNum + "个失败...");
            }
            //  调用失败接口 如果接口错误，则加入到同步队列里面去
            TakeoutRecord takeoutRecord = new TakeoutRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(),
                    cardId, passage.getProduct(), DataFormat.getNowTime(), -1, "", "", "");
            Track.getInstance(HandleTakeOutResultActivity.this).setTakeOutRecordCommand(passage, takeoutRecord, objectId);
        } else {
            recordSuccess++;
            if (number > 1) {
                tv_handle_result.setText("取货第" + recordNum + "个成功...");
            }
        }
        if (recordNum == number && recordSuccess == recordNum) {
            handleNewVendingSerialPort(true);
        } else if (recordNum == number) {
            handleNewVendingSerialPort(false);
        }
    }


    private void handleNewVendingSerialPort(boolean isSuccessOpen) {
        // 成功
        if (isSuccessOpen) {
            if (empPower != null) {
                empPower.setUsed(empPower.getUsed() + 1);
                empPowerDao.insertOrReplace(empPower);
            }
            // 打开成功之后逻辑 加入线程池队列 --- 交付线程池进行消费入本地库以及通知远程服务端  --- 本地数据库进行库存的消耗
            TakeoutRecord takeoutRecord = new TakeoutRecord(null, true, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(),
                    cardId, passage.getProduct(), DataFormat.getNowTime(), recordSuccess, "", "", "");
            passage.setStock(passage.getStock() - recordSuccess);
            Track.getInstance(HandleTakeOutResultActivity.this).setTakeOutRecordCommand(passage, takeoutRecord);

            // 串口打开螺纹柜子成功
            handleResult(new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG));
        }
        // 失败
        else {
            // 串口打开螺纹柜子失败
            if (isStoreSend) {
                handleResult(new TakeOutError(TakeOutError.OPEN_GEZI_SERIAL_FAILED_FLAG));
            } else {
                handleResult(new TakeOutError(TakeOutError.OPEN_LUOWEN_SERIAL_FAILED_FLAG));
            }
        }
    }

    /**
     * 处理本地消费结果（到结果页面）
     */
    private void handleResult(TakeOutError takeOutError) {
        if (!takeOutError.isSuccess()) {
            ErrorRecord errorRecord = new ErrorRecord(null, false, (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo(), cardId, "消费问题: " + cardId + takeOutError.serverMsg, takeOutError.getTakeOutMsg(), DataFormat.getNowTime(), "", "", "");
            Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
        }
        if (takeOutError != null) {
            if (TextUtils.isEmpty(takeOutError.serverMsg)) {
                tv_handle_result.setText(takeOutError.getTakeOutMsg());
            } else {
                tv_handle_result.setText(takeOutError.serverMsg);
            }
        }
        btn_return_mainpage.setVisibility(View.VISIBLE);
        countDownTimer.start();
    }

}
