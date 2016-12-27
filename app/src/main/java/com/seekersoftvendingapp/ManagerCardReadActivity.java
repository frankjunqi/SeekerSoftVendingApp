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

import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.database.table.AdminCardDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.ErrorRecord;
import com.seekersoftvendingapp.serialport.CardReadSerialPort;
import com.seekersoftvendingapp.track.Track;
import com.seekersoftvendingapp.util.DataFormat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import java.util.List;

/**
 * 4. 管理员 读卡 页面
 * Created by kjh08490 on 2016/11/25.
 */

public class ManagerCardReadActivity extends BaseActivity {

    private Button btn_login;
    private TextView tv_errordesc;

    private AdminCardDao adminCardDao;
    private Handler mHandle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SeekerSoftConstant.ADMINCARDRECECIVECODE:
                    // 管理员卡号
                    String adminCardNum = msg.obj.toString();
                    SeekerSoftConstant.ADMINCARD = adminCardNum;
                    if (TextUtils.isEmpty(adminCardNum)) {
                        // 读到的卡号为null or ""
                        ErrorRecord errorRecord = new ErrorRecord(null, false, "", "", "管理员读卡", "读到的卡号为空.", DataFormat.getNowTime(), "", "", "");
                        Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
                        if (tv_errordesc != null) {
                            tv_errordesc.setText("卡号为空，请重新读卡.");
                        }
                    } else {
                        CardReadSerialPort.getCradSerialInstance().closeReadSerial();
                        // 处理业务
                        handleReadCardAfterBusniess(SeekerSoftConstant.ADMINCARD);
                    }
                    break;
            }
        }
    };

    /**
     * 查询是否是管理员
     *
     * @param adminCardNum 管理员卡号
     */
    private void handleReadCardAfterBusniess(String adminCardNum) {
        Toast.makeText(ManagerCardReadActivity.this, adminCardNum.length() + "", Toast.LENGTH_SHORT).show();
        adminCardNum = adminCardNum.substring(1);
        List<AdminCard> adminList = adminCardDao.queryBuilder()
                .where(AdminCardDao.Properties.IsDel.eq(false))
                .where(AdminCardDao.Properties.Card.like("%" + adminCardNum + "%"))
                .list();
        // .where(AdminCardDao.Properties.Card.like("%" + adminCardNum + "%"))
        if (adminList != null && adminList.size() > 0) {
            // 此人是管理员
            AdminCard adminCard = adminList.get(0);
            Intent intent = new Intent(ManagerCardReadActivity.this, ManagerGoodsActivity.class);
            intent.putExtra(SeekerSoftConstant.ADMINCARD, adminCard);
            startActivity(intent);
            this.finish();

        } else {
            // 此人不是管理员则提示他不是管理员，并且重新打开串口
            if (tv_errordesc != null) {
                tv_errordesc.setText(adminCardNum + "此卡不是管理卡,请您换管理卡，重新刷卡...");
            }
            openCardSerialPort();

            ErrorRecord errorRecord = new ErrorRecord(null, false, "", adminCardNum, "管理员读卡", "此卡不是管理卡.", DataFormat.getNowTime(), "", "", "");
            Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_cardread);
        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        adminCardDao = daoSession.getAdminCardDao();

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleReadCardAfterBusniess(SeekerSoftConstant.ADMINCARDNUM);
            }
        });
        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerCardReadActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        tv_errordesc = (TextView) findViewById(R.id.tv_errordesc);

        openCardSerialPort();

        countDownTimer.start();
    }

    /**
     * 打开串口以及监听串口
     */
    private void openCardSerialPort() {
        // 打开串口读卡器  -- 串口读到数据后关闭串口 -- 判断能否进行登录管理页面
        CardReadSerialPort.getCradSerialInstance().setOnDataReceiveListener(new CardReadSerialPort.OnDataReceiveListener() {
            @Override
            public void onDataReceiveString(String IDNUM) {
                Log.e("tag", IDNUM);
                Message message = new Message();
                message.what = SeekerSoftConstant.ADMINCARDRECECIVECODE;
                message.obj = IDNUM;
                mHandle.sendMessage(message);
            }

            @Override
            public void onDataReceiveBuffer(byte[] buffer, int size) {
                Log.e("tag", "length is:" + size + ",data is:" + new String(buffer, 0, size));
            }
        });
    }
}
