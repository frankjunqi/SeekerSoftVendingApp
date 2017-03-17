package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.Employee;
import com.seekersoftvendingapp.database.table.EmployeeDao;
import com.seekersoftvendingapp.database.table.ErrorRecord;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.borrow.BorrowResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.seekersoftvendingapp.newtakeoutserial.ShipmentObject;
import com.seekersoftvendingapp.track.Track;
import com.seekersoftvendingapp.util.DataFormat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.util.TakeOutError;
import com.seekersoftvendingapp.view.KeyBordView;

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

    private LinearLayout ll_keyboard;
    private KeyBordView keyBordView;

    private String cardId = "";

    // 货道的产品
    private String productId = "";
    private String pasageId = "";
    private String passageFlag = "";

    private EmpPowerDao empPowerDao;
    private EmployeeDao employeeDao;
    private Passage passage;
    private Employee employee;// 当前扫描的卡是属于哪个员工

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_cardread);
        setTitle("输入卡号...");
        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        empPowerDao = daoSession.getEmpPowerDao();
        employeeDao = daoSession.getEmployeeDao();

        passage = (Passage) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        if (passage != null) {
            productId = passage.getProduct();
            pasageId = passage.getSeqNo();
            passageFlag = TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag();
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

        ll_keyboard = (LinearLayout) findViewById(R.id.ll_keyboard);
        keyBordView = new KeyBordView(this);
        keyBordView.setKeyWordHint("请输入您的卡号...");
        keyBordView.setSureClickListen(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardId = keyBordView.getKeyBoradStr();
                if (TextUtils.isEmpty(cardId)) {
                    // 读到的卡号为null or ""
                    ErrorRecord errorRecord = new ErrorRecord(null, false, passageFlag + pasageId, cardId, "借货", "读到的卡号为空.", DataFormat.getNowTime(), "", "", "");
                    Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
                    Toast.makeText(BorrowCardReadActivity.this, "请重新读卡...", Toast.LENGTH_SHORT).show();
                } else {
                    // 处理业务
                    handleReadCardAfterBusniess(cardId);
                }
            }
        });
        ll_keyboard.addView(keyBordView);

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
            TakeOutError takeOutError = localBorrowPro(productId, cardId);
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
            handleResult(new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG));
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
            // 格子柜子
            shipmentObject.containerNum = TextUtils.isEmpty(passage.getFlag()) ? 0 : Integer.parseInt(passage.getFlag());
            shipmentObject.proNum = Integer.parseInt(passage.getSeqNo());
            // TODO 需要生成唯一码
            shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;

            handleStoreSerialPort(true, objectId);
            /*NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject).setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                @Override
                public void onCmdCallBack(boolean isSuccess) {
                    // TODO NEED DELETE
                    isSuccess = true;
                    handleStoreSerialPort(isSuccess, objectId);
                }
            });*/
        } catch (Exception e) {
            // TODO NEED Change true ---> false
            handleStoreSerialPort(true, objectId);
        }
    }

    private void handleStoreSerialPort(boolean isSuccess, String objectId) {
        if (isSuccess) {
            // 打开成功之后逻辑 加入线程池队列 --- 交付线程池进行消费入本地库以及通知远程服务端 -- 本地数据库进行库存的消耗
            BorrowRecord borrowRecord = new BorrowRecord(null, true, passageFlag + pasageId, cardId, true, true, new Date(), "", "", "");
            passage.setStock(passage.getStock() - 1);
            passage.setBorrowState(true);
            // 更新此人已经借走货物
            passage.setBorrowUser(employee != null ? employee.getObjectId() : "");
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
            BorrowRecord borrowRecord = new BorrowRecord(null, false, passageFlag + pasageId, cardId, true, false, new Date(), "", "", "");
            Track.getInstance(BorrowCardReadActivity.this).setBorrowReturnRecordCommand(passage, borrowRecord, objectId);

            // 串口打开柜子失败
            handleResult(new TakeOutError(TakeOutError.OPEN_LUOWEN_SERIAL_FAILED_FLAG));
        }
    }

    /**
     * 处理本地消费结果（到结果页面）
     */
    private void handleResult(TakeOutError takeOutError) {
        if (takeOutError.isSuccess()) {
            Intent intent = new Intent(BorrowCardReadActivity.this, HandleResultActivity.class);
            intent.putExtra(SeekerSoftConstant.TAKEOUTERROR, takeOutError);
            startActivity(intent);
            this.finish();
        } else {
            Toast.makeText(BorrowCardReadActivity.this, cardId + takeOutError.serverMsg + "----" + takeOutError.getTakeOutMsg(), Toast.LENGTH_SHORT).show();
            ErrorRecord errorRecord = new ErrorRecord(null, false, passageFlag + pasageId, cardId, "消费问题: " + takeOutError.serverMsg, takeOutError.getTakeOutMsg(), DataFormat.getNowTime(), "", "", "");
            Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
        }
    }


    /**
     * 当无网络时候进行本地判断，否则全部走网络判断为准；
     * 库存以本地为准；
     * 本地进行判断是否可以出货
     */
    private TakeOutError localBorrowPro(String productId, String cardId) {
        // 同一个商品 权限详细信息list 消费频次 周期消费次数
        List<EmpPower> listEmpPowers = empPowerDao.queryBuilder()
                .where(EmpPowerDao.Properties.IsDel.eq(false))
                .where(EmpPowerDao.Properties.Product.eq(productId)).list();

        if (listEmpPowers == null || listEmpPowers.size() == 0) {
            // 此商品暂时没有赋予出货权限
            return new TakeOutError(TakeOutError.PRO_HAS_NOPOWER_FLAG);
        }

        // 具体查询card对应的用户
        List<Employee> employeeList = employeeDao.queryBuilder()
                .where(EmployeeDao.Properties.IsDel.eq(false))
                .where(EmployeeDao.Properties.Card.like("%" + cardId + "%"))
                .list();

        if (employeeList != null && employeeList.size() > 0) {
            employee = employeeList.get(0);
            for (EmpPower empPower : listEmpPowers) {
                if (employee.getPower().contains(empPower.getObjectId())) {
                    // 此人有权限进行借此物品
                    return new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG);
                }
            }
            // 此人无权限
            return new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG);
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
        Call<BorrowResBody> updateAction = service.borrow(SeekerSoftConstant.DEVICEID, cardId, passageFlag + pasageId);
        Log.e("json", "borrow = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<BorrowResBody>() {
            @Override
            public void onResponse(Call<BorrowResBody> call, Response<BorrowResBody> response) {
                if (response != null && response.body() != null && response.body().data.result) {
                    cmdBufferVendingSerial(response.body().data.objectId);
                } else {
                    TakeOutError takeOutError = new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG);
                    takeOutError.serverMsg = response.body().message;
                    handleResult(takeOutError);
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<BorrowResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(BorrowCardReadActivity.this, "网络链接问题，本地进行借货操作", Toast.LENGTH_LONG).show();
                TakeOutError takeOutError = localBorrowPro(productId, cardId);
                outProResult(takeOutError);
            }
        });
    }
}
