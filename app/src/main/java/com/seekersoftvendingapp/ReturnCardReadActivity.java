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
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.BorrowRecord;
import com.seekersoftvendingapp.database.table.BorrowRecordDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.database.table.EmpPowerDao;
import com.seekersoftvendingapp.database.table.Employee;
import com.seekersoftvendingapp.database.table.EmployeeDao;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.returnpro.ReturnProResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.serialport.CardReadSerialPort;
import com.seekersoftvendingapp.serialport.StoreSerialPort;
import com.seekersoftvendingapp.track.Track;
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

    private Button btn_return_goods;
    private Button btn_return_mainpage;

    // 货道的产品
    private String productId = "";
    private String pasageId = "";
    private String passageFlag = "";

    private PassageDao passageDao;
    private EmpPowerDao empPowerDao;
    private EmployeeDao employeeDao;
    private BorrowRecordDao borrowRecordDao;

    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SeekerSoftConstant.CARDRECEIVECODE:
                    SeekerSoftConstant.CARDID = msg.obj.toString();
                    if (TextUtils.isEmpty(SeekerSoftConstant.CARDID)) {
                        // TODO 读到的卡号为null or ""
                        Toast.makeText(ReturnCardReadActivity.this, "请重新读卡...", Toast.LENGTH_SHORT).show();
                    } else {
                        CardReadSerialPort.getCradSerialInstance().closeReadSerial();
                        // TODO 处理业务
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
        passageDao = daoSession.getPassageDao();
        empPowerDao = daoSession.getEmpPowerDao();
        employeeDao = daoSession.getEmployeeDao();
        borrowRecordDao = daoSession.getBorrowRecordDao();

        productId = getIntent().getStringExtra(SeekerSoftConstant.PRODUCTID);
        pasageId = getIntent().getStringExtra(SeekerSoftConstant.PASSAGEID);
        passageFlag = getIntent().getStringExtra(SeekerSoftConstant.PASSAGEFLAG);

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
                Intent intent = new Intent(ReturnCardReadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        // TODO 打开串口读卡器  -- 串口读到数据后关闭串口 -- 判断能否进行借接口
        // Open Serial Port Codeing Here
        CardReadSerialPort.getCradSerialInstance().setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {
                Log.e("TAG", IDNUM);
                Message message = new Message();
                message.what = SeekerSoftConstant.CARDRECEIVECODE;
                message.obj = IDNUM;
                mHandle.sendMessage(message);
            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {
                Log.e("TAG", "length is:" + size + ",data is:" + new String(buffer, 0, size));
            }
        });
        StoreSerialPort.getInstance().setOnDataReceiveListener(new StoreSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {

            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {

            }
        });
    }

    /**
     * 本地判断是否可以进行借出去
     */
    /**
     * 处理读到卡之后的业务
     */
    private void handleReadCardAfterBusniess() {
        // TODO 网络判断是否可以出货(网络优先)
        if (SeekerSoftConstant.NETWORKCONNECT) {
            isReturnPro(SeekerSoftConstant.CARDID);
        } else {
            // 本地判断是否可以出货
            TakeOutError takeOutError = localReturnPro(productId, SeekerSoftConstant.CARDID);
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
            cmdBufferStoreSerial();
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
    private void cmdBufferStoreSerial() {
        String cmd = StoreSerialPort.cmdOpenVender(pasageId.charAt(0), pasageId.charAt(1));
        boolean open = StoreSerialPort.getInstance().sendBuffer(StoreSerialPort.HexToByteArr(cmd));
        StoreSerialPort.getInstance().closeSerialPort();
        if (true) {
            // 货道的借还标记进行重置
            List<Passage> passageList = passageDao.queryBuilder()
                    .where(PassageDao.Properties.SeqNo.eq(pasageId))
                    .where(PassageDao.Properties.Flag.eq(passageFlag))
                    .list();
            if (passageList != null && passageList.size() > 0) {
                Passage passage = passageList.get(0);
                passage.setBorrowState(false);
                passageDao.insertOrReplaceInTx(passage);
            }

            // 打开成功之后逻辑 加入线程池队列 --- 交付线程池进行消费入本地库以及通知远程服务端 -- 本地数据库进行库存的消耗
            BorrowRecord borrowRecord = new BorrowRecord(null, true, pasageId, SeekerSoftConstant.CARDID, false, new Date());
            Track.getInstance(ReturnCardReadActivity.this).setBorrowReturnRecordCommand(borrowRecord);

            // 串口打开柜子成功
            handleResult(new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG));
        } else {
            // 串口打开柜子失败
            handleResult(new TakeOutError(TakeOutError.OPEN_LUOWEN_SERIAL_FAILED_FLAG));
        }
    }

    /**
     * 处理本地消费结果（到结果页面）
     */
    private void handleResult(TakeOutError takeOutError) {
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
    private TakeOutError localReturnPro(String productId, String cardId) {
        // 同一个商品 权限详细信息list 消费频次 周期消费次数
        List<EmpPower> listEmpPowers = empPowerDao.queryBuilder()
                .where(PassageDao.Properties.IsDel.eq(false))
                .where(PassageDao.Properties.Product.eq(productId)).list();

        if (listEmpPowers == null || listEmpPowers.size() == 0) {
            // 此商品暂时没有赋予出货权限
            return new TakeOutError(TakeOutError.PRO_HAS_NOPOWER_FLAG);
        }

        // 具体查询card对应的用户
        List<Employee> employeeList = employeeDao.queryBuilder()
                .where(EmployeeDao.Properties.IsDel.eq(false))
                .where(EmployeeDao.Properties.Card.in(cardId))
                .list();

        if (employeeList != null && employeeList.size() > 0) {
            Employee employee = employeeList.get(0);
            for (EmpPower empPower : listEmpPowers) {
                if (employee.getPower().contains(empPower.getObjectId())) {
                    // 此人有权限进行归还物品
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
     * （接口）判断是否能出货
     */
    private void isReturnPro(String cardId) {
        // 加载前
        // do something

        // 异步加载(get)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<ReturnProResBody> updateAction = service.returnPro(SeekerSoftConstant.DEVICEID, cardId, passageFlag + pasageId);
        updateAction.enqueue(new Callback<ReturnProResBody>() {
            @Override
            public void onResponse(Call<ReturnProResBody> call, Response<ReturnProResBody> response) {
                if (response != null && response.body() != null && response.body().data.result) {
                    Toast.makeText(ReturnCardReadActivity.this, "可以还货,true", Toast.LENGTH_LONG).show();
                    cmdBufferStoreSerial();
                } else {
                    Toast.makeText(ReturnCardReadActivity.this, "不可以还货,false", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReturnProResBody> call, Throwable throwable) {
                Toast.makeText(ReturnCardReadActivity.this, "basedate :  Failure", Toast.LENGTH_LONG).show();
                localReturnPro(productId, SeekerSoftConstant.CARDID);
            }
        });
    }

}