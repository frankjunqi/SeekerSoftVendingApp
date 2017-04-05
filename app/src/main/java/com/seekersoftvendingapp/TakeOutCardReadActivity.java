package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
 * 1. 取货 读卡 页面
 * Created by kjh08490 on 2016/11/25.
 */

public class TakeOutCardReadActivity extends BaseActivity {

    private RelativeLayout ll_keyboard;
    private EditText et_getcard;

    private String cardId = "";

    // 货道的产品
    private String productId = "";
    private String pasageId = "";
    private String passageFlag = "";

    // 判断是否是格子柜子消费
    private boolean isStoreSend = false;

    private EmpPowerDao empPowerDao;
    private EmpCardDao empCardDao;
    private EmpPower empPower;

    private Passage passage;
    private int number = 1;

    private int recordNum = 0;
    private int recordSuccess = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardread);
        setTitle("输入卡号...");
        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        empPowerDao = daoSession.getEmpPowerDao();
        empCardDao = daoSession.getEmpCardDao();

        passage = (Passage) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        number = getIntent().getIntExtra(SeekerSoftConstant.TakeoutNum, 1);
        if (passage != null) {
            passageFlag = TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag();
            productId = passage.getProduct();
            pasageId = passage.getSeqNo();
            // 判断是否是格子柜消费
            if (!TextUtils.isEmpty(passageFlag) && passage.getIsSend()) {
                isStoreSend = true;
            }
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
                Intent intent = new Intent(TakeOutCardReadActivity.this, MainActivity.class);
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
                        ErrorRecord errorRecord = new ErrorRecord(null, false, passageFlag + pasageId, cardId, "出货", "读到的卡号为空.", DataFormat.getNowTime(), "", "", "");
                        Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
                        Toast.makeText(TakeOutCardReadActivity.this, "请重新读卡...", Toast.LENGTH_SHORT).show();
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
     * 处理读到卡之后的业务
     */
    private void handleReadCardAfterBusniess(String cardId) {
        // 网络判断是否可以出货(网络优先)
        if (SeekerSoftConstant.NETWORKCONNECT) {
            isTakeOutPro(cardId);
        } else {
            // 本地判断是否可以出货
            TakeOutError takeOutError = localTakeOutPro(productId, cardId);
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
            // 串口螺纹passageID操作 || 格子消费柜子的passageId操作
            cmdBufferVendingStoreSerial("");
        } else {
            // 不可以出货
            handleResult(new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG));
        }
    }

    /**
     * 打开螺纹柜子串口设备出货
     *
     * @param objectId 出货的服务端的记录的objectid
     */
    private void cmdBufferVendingStoreSerial(final String objectId) {
        try {
            NewVendingSerialPort.SingleInit().setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                @Override
                public void onCmdCallBack(boolean isSuccess) {
                    recordNum++;
                    if (!isSuccess) {
                        //  调用失败接口 如果接口错误，则加入到同步队列里面去
                        TakeoutRecord takeoutRecord = new TakeoutRecord(null, false, passageFlag + pasageId, cardId, productId, new Date(), -1, "", "", "");
                        Track.getInstance(TakeOutCardReadActivity.this).setTakeOutRecordCommand(passage, takeoutRecord, objectId);
                    } else {
                        recordSuccess++;
                    }
                    if (recordNum == number && recordSuccess > 0) {
                        handleNewVendingSerialPort(true, objectId);
                    } else if (recordNum == number && recordSuccess == 0) {
                        handleNewVendingSerialPort(false, objectId);
                    }
                }
            });
            if (isStoreSend) {
                ShipmentObject shipmentObject = new ShipmentObject();
                // 格子柜子
                shipmentObject.containerNum = 2;
                shipmentObject.proNum = Integer.parseInt(passage.getSeqNo());
                // TODO 需要生成唯一码
                shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
                NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject);
            } else {
                for (int i = 0; i < number; i++) {
                    ShipmentObject shipmentObject = new ShipmentObject();
                    // 螺纹柜子
                    int col = Integer.parseInt(pasageId.substring(0, 1));
                    int row = Integer.parseInt(pasageId.substring(1, 2));
                    shipmentObject.containerNum = 1;
                    shipmentObject.proNum = col * 10 + row;
                    // TODO 需要生成唯一码
                    shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
                    NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject);
                }
            }
        } catch (Exception e) {
            handleNewVendingSerialPort(false, objectId);
        }
    }

    private void handleNewVendingSerialPort(boolean isSuccessOpen, String objectId) {
        // 成功
        if (isSuccessOpen) {
            if (empPower != null) {
                empPower.setUsed(empPower.getUsed() + 1);
                empPowerDao.insertOrReplace(empPower);
            }
            // 打开成功之后逻辑 加入线程池队列 --- 交付线程池进行消费入本地库以及通知远程服务端  --- 本地数据库进行库存的消耗
            TakeoutRecord takeoutRecord = new TakeoutRecord(null, true, passageFlag + pasageId, cardId, productId, new Date(), recordSuccess, "", "", "");
            passage.setStock(passage.getStock() - 1);
            if (TextUtils.isEmpty(objectId)) {
                // 本地消费
                takeoutRecord.setIsDel(false);
            } else {
                // 网络消费
                takeoutRecord.setIsDel(true);
            }
            Track.getInstance(TakeOutCardReadActivity.this).setTakeOutRecordCommand(passage, takeoutRecord);

            // 串口打开螺纹柜子成功
            handleResult(new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG));
        }
        // 失败
        else {
            //  调用失败接口 如果接口错误，则加入到同步队列里面去
            //TakeoutRecord takeoutRecord = new TakeoutRecord(null, false, passageFlag + pasageId, cardId, productId, new Date(), recordSuccess, "", "", "");
            //Track.getInstance(TakeOutCardReadActivity.this).setTakeOutRecordCommand(passage, takeoutRecord, objectId);

            // 串口打开螺纹柜子失败
            handleResult(new TakeOutError(TakeOutError.OPEN_LUOWEN_SERIAL_FAILED_FLAG));
        }
    }

    /**
     * 处理本地消费结果（到结果页面）
     */
    private void handleResult(TakeOutError takeOutError) {
        if (takeOutError.isSuccess()) {
            Intent intent = new Intent(TakeOutCardReadActivity.this, HandleResultActivity.class);
            intent.putExtra(SeekerSoftConstant.TAKEOUTERROR, takeOutError);
            startActivity(intent);
            this.finish();
        } else {
            et_getcard.setText("");
            Toast.makeText(TakeOutCardReadActivity.this, et_getcard.getText().toString() + takeOutError.serverMsg + "---" + takeOutError.getTakeOutMsg(), Toast.LENGTH_LONG).show();
            ErrorRecord errorRecord = new ErrorRecord(null, false, passageFlag + pasageId, cardId, "消费问题: " + cardId + takeOutError.serverMsg, takeOutError.getTakeOutMsg(), DataFormat.getNowTime(), "", "", "");
            Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
        }
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
                .where(EmpCardDao.Properties.Card.like("%" + cardId + "%")).list();
        if (empCardList != null && empCardList.size() > 0) {
            EmpCard empCard = empCardList.get(0);
            // 同一个商品 权限详细信息list 消费频次 周期消费次数
            List<EmpPower> listEmpPowers = empPowerDao.queryBuilder()
                    .where(EmpPowerDao.Properties.IsDel.eq(false))
                    .where(EmpPowerDao.Properties.Emp.like("%" + empCard.getEmp() + "%"))
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
     * （接口）判断是否能出货
     */
    private void isTakeOutPro(final String cardId) {
        showProgress();
        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<TakeOutResBody> updateAction = service.takeOut(SeekerSoftConstant.DEVICEID, cardId, passageFlag + pasageId, String.valueOf(number));
        LogCat.e("takeOut = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<TakeOutResBody>() {
            @Override
            public void onResponse(Call<TakeOutResBody> call, Response<TakeOutResBody> response) {
                if (response != null && response.body() != null && response.body().data.result) {
                    cmdBufferVendingStoreSerial(response.body().data.objectId);
                } else {
                    // 此人没有权限,不可以出货
                    TakeOutError takeOutError = new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG);
                    takeOutError.serverMsg = response.body().message;
                    handleResult(takeOutError);
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<TakeOutResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(TakeOutCardReadActivity.this, "网络链接问题，本地进行出货操作", Toast.LENGTH_LONG).show();
                TakeOutError takeOutError = localTakeOutPro(productId, cardId);
                outProResult(takeOutError);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
