package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.Employee;
import com.seekersoftvendingapp.database.table.EmployeeDao;
import com.seekersoftvendingapp.database.table.ErrorRecord;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.TakeoutRecord;
import com.seekersoftvendingapp.database.table.TakeoutRecordDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.takeout.TakeOutResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.serialport.CardReadSerialPort;
import com.seekersoftvendingapp.serialport.StoreSerialPort;
import com.seekersoftvendingapp.serialport.VendingSerialPort;
import com.seekersoftvendingapp.track.Track;
import com.seekersoftvendingapp.util.DataFormat;
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

    private String TAG = TakeOutCardReadActivity.class.getSimpleName();

    private Button btn_return_goods;

    private TextView tv_errordesc;

    // 货道的产品
    private String productId = "";
    private String pasageId = "";
    private String passageFlag = "";

    // 判断是否是格子柜子消费
    private boolean isStoreSend = false;

    private EmpPowerDao empPowerDao;
    private EmployeeDao employeeDao;
    private TakeoutRecordDao takeOutRecordDao;

    private Passage passage;

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SeekerSoftConstant.CARDRECEIVECODE:
                    SeekerSoftConstant.CARDID = msg.obj.toString();
                    if (TextUtils.isEmpty(SeekerSoftConstant.CARDID)) {
                        // 读到的卡号为null or ""
                        ErrorRecord errorRecord = new ErrorRecord(null, false, passageFlag + pasageId, SeekerSoftConstant.CARDID, "出货", "读到的卡号为空.", DataFormat.getNowTime(), "", "", "");
                        Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
                        Toast.makeText(TakeOutCardReadActivity.this, "请重新读卡...", Toast.LENGTH_SHORT).show();
                    } else {
                        CardReadSerialPort.getCradSerialInstance().closeReadSerial();
                        // 处理业务
                        handleReadCardAfterBusniess();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardread);

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        empPowerDao = daoSession.getEmpPowerDao();
        employeeDao = daoSession.getEmployeeDao();
        takeOutRecordDao = daoSession.getTakeoutRecordDao();

        passage = (Passage) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        if (passage != null) {
            passageFlag = TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag();
            productId = passage.getProduct();
            pasageId = passage.getSeqNo();
            // 判断是否是格子柜消费
            if (!TextUtils.isEmpty(passageFlag) && passage.getIsSend()) {
                isStoreSend = true;
            }
        }


        btn_return_goods = (Button) findViewById(R.id.btn_return_goods);
        btn_return_goods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReadCardAfterBusniess();
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
        tv_errordesc = (TextView) findViewById(R.id.tv_errordesc);

        openCardSerialPort();

        // 初始化是否是格子柜消费
        if (isStoreSend) {
            openStoreSerialPort();
        } else {
            openVendingSerialPort();
        }

        countDownTimer.start();
    }

    /**
     * 打开打卡串口
     */
    private void openCardSerialPort() {
        // 打开串口读卡器  -- 串口读到数据后关闭串口 -- 判断能否进行取货接口
        CardReadSerialPort.getCradSerialInstance().setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {
                Log.e("tag", IDNUM);
                Message message = new Message();
                message.what = SeekerSoftConstant.CARDRECEIVECODE;
                message.obj = IDNUM;
                mHandle.sendMessage(message);
            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {
                Log.e(TAG, "length is:" + size + ",data is:" + new String(buffer, 0, size));
            }
        });
    }

    /**
     * 打开串口螺纹  ---  发送指令出货
     */
    private void openVendingSerialPort() {
        VendingSerialPort.getInstance().setOnDataReceiveListener(new VendingSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {

            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {
                Toast.makeText(TakeOutCardReadActivity.this, "Vending Serial = " + new String(buffer, 0, size), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 打开格子柜子串口
     */
    private void openStoreSerialPort() {
        StoreSerialPort.getInstance().setOnDataReceiveListener(new StoreSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {

            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {
                Toast.makeText(TakeOutCardReadActivity.this, "Store Serial = " + new String(buffer, 0, size), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 处理读到卡之后的业务
     */
    private void handleReadCardAfterBusniess() {
        // 网络判断是否可以出货(网络优先)
        if (SeekerSoftConstant.NETWORKCONNECT) {
            isTakeOutPro(SeekerSoftConstant.CARDID);
        } else {
            // 本地判断是否可以出货
            TakeOutError takeOutError = localTakeOutPro(productId, SeekerSoftConstant.CARDID);
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
            // 重新打开打卡串口
            openCardSerialPort();
            // 不可以出货
            handleResult(new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG));
        }
    }

    /**
     * 打开螺纹柜子串口设备出货
     *
     * @param objectId 出货的服务端的记录的objectid
     */
    private void cmdBufferVendingStoreSerial(String objectId) {
        boolean open = false;

        if (isStoreSend) {
            // 格子柜子
            try {
                String cmd = StoreSerialPort.cmdOpenStoreDoor(2,
                        TextUtils.isEmpty(passage.getFlag()) ? 0 : Integer.parseInt(passage.getFlag()),
                        Integer.parseInt(passage.getSeqNo()));
                open = StoreSerialPort.getInstance().sendBuffer(StoreSerialPort.HexToByteArr(cmd));
            } catch (Exception e) {
                open = false;
            }
        } else {
            // 螺纹柜子
            try {
                int col = Integer.parseInt(pasageId.substring(0, 1));
                int row = Integer.parseInt(pasageId.substring(1, 2));
                String cmd = VendingSerialPort.cmdOpenVender(col, row);
                open = VendingSerialPort.getInstance().sendBuffer(VendingSerialPort.HexToByteArr(cmd));
            } catch (Exception e) {
                open = false;
            }
        }

        // 成功
        if (open) {
            if (isStoreSend) {
                // 关闭格子柜
                StoreSerialPort.getInstance().closeSerialPort();
            } else {
                // 关闭螺纹柜
                VendingSerialPort.getInstance().closeSerialPort();
            }
            // 打开成功之后逻辑 加入线程池队列 --- 交付线程池进行消费入本地库以及通知远程服务端  --- 本地数据库进行库存的消耗
            TakeoutRecord takeoutRecord = new TakeoutRecord(null, true, passageFlag + pasageId, SeekerSoftConstant.CARDID, productId, new Date(), "", "", "");
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
            TakeoutRecord takeoutRecord = new TakeoutRecord(null, false, passageFlag + pasageId, SeekerSoftConstant.CARDID, productId, new Date(), "", "", "");
            Track.getInstance(TakeOutCardReadActivity.this).setTakeOutRecordCommand(passage, takeoutRecord, objectId);

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
            if (tv_errordesc != null) {
                tv_errordesc.setText(SeekerSoftConstant.CARDID + takeOutError.serverMsg + "---" + takeOutError.getTakeOutMsg());
            }
            ErrorRecord errorRecord = new ErrorRecord(null, false, passageFlag + pasageId, SeekerSoftConstant.CARDID, "消费问题: " + SeekerSoftConstant.CARDID + takeOutError.serverMsg, takeOutError.getTakeOutMsg(), DataFormat.getNowTime(), "", "", "");
            Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
        }
    }


    /**
     * 当无网络时候进行本地判断，否则全部走网络判断为准；
     * 库存以本地为准；
     * 本地进行判断是否可以出货
     */
    private TakeOutError localTakeOutPro(String productId, String cardId) {
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
            Employee employee = employeeList.get(0);
            for (EmpPower empPower : listEmpPowers) {
                if (employee.getPower().contains(empPower.getObjectId())) {
                    // 此人有权限（判断此人权限的消费次数问题）
                    int period = empPower.getPeriod();
                    String unit = empPower.getUnit();
                    int count = empPower.getCount();

                    List<TakeoutRecord> takeOutRecordList = takeOutRecordDao.queryBuilder().where(TakeoutRecordDao.Properties.Card.eq(cardId))
                            .where(TakeoutRecordDao.Properties.Passage.eq(pasageId))
                            .where(TakeoutRecordDao.Properties.ProductId.eq(productId))
                            .where(TakeoutRecordDao.Properties.Time.between(DataFormat.periodUnitGetStartDate(period, unit), DataFormat.getTodayDate()))
                            .list();
                    if (takeOutRecordList != null && takeOutRecordList.size() < count) {
                        // 此人消费次数未满，可以进行消费
                        return new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG);
                    } else {
                        // 此人消费次数已满，不可以进行消费
                        return new TakeOutError(TakeOutError.FAILE_TAKEOUT_FLAG);
                    }
                }
            }
            // 此人无权限
            openCardSerialPort();
            return new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG);
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
        Call<TakeOutResBody> updateAction = service.takeOut(SeekerSoftConstant.DEVICEID, cardId, passageFlag + pasageId);
        Log.e("json", "takeOut = " + updateAction.request().url().toString());
        updateAction.enqueue(new Callback<TakeOutResBody>() {
            @Override
            public void onResponse(Call<TakeOutResBody> call, Response<TakeOutResBody> response) {
                if (response != null && response.body() != null && response.body().data.result) {
                    cmdBufferVendingStoreSerial(response.body().data.objectId);
                } else {
                    // 此人没有权限,不可以出货
                    openCardSerialPort();
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
                TakeOutError takeOutError = localTakeOutPro(productId, SeekerSoftConstant.CARDID);
                outProResult(takeOutError);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 去除handle
        if (mHandle != null) {
            mHandle.removeMessages(SeekerSoftConstant.CARDRECEIVECODE);
        }
        // 关闭串口(读卡器 & 螺纹柜子)
        CardReadSerialPort.getCradSerialInstance().closeReadSerial();
        VendingSerialPort.getInstance().closeSerialPort();
    }
}
