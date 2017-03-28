package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.database.table.AdminCardDao;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.ErrorRecord;
import com.seekersoftvendingapp.track.Track;
import com.seekersoftvendingapp.util.DataFormat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.view.KeyBordView;

import java.util.List;

/**
 * 4. 管理员 读卡 页面
 * Created by kjh08490 on 2016/11/25.
 */

public class ManagerCardReadActivity extends BaseActivity {

    private EditText et_getcard;
    private String cardId = "";

    private AdminCardDao adminCardDao;

    /**
     * 查询是否是管理员
     *
     * @param adminCardNum 管理员卡号
     */
    private void handleReadCardAfterBusniess(String adminCardNum) {
        List<AdminCard> adminList = adminCardDao.queryBuilder()
                .where(AdminCardDao.Properties.IsDel.eq(false))
                .where(AdminCardDao.Properties.Card.like("%" + adminCardNum + "%"))
                .list();
        if (adminList != null && adminList.size() > 0) {
            // 此人是管理员
            AdminCard adminCard = adminList.get(0);
            Intent intent = new Intent(ManagerCardReadActivity.this, ManagerGoodsActivity.class);
            intent.putExtra(SeekerSoftConstant.ADMINCARD, adminCard);
            startActivity(intent);
            this.finish();

        } else {
            // 此人不是管理员则提示他不是管理员，并且重新打开串口
            Toast.makeText(ManagerCardReadActivity.this, cardId + "此卡:" + adminCardNum + "不是管理卡,请您换管理卡，重新刷卡...", Toast.LENGTH_SHORT).show();
            ErrorRecord errorRecord = new ErrorRecord(null, false, "", adminCardNum, "管理员读卡", "此卡:" + adminCardNum + "不是管理卡.", DataFormat.getNowTime(), "", "", "");
            Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_cardread);
        setTitle("输入卡号...");
        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        adminCardDao = daoSession.getAdminCardDao();

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ManagerCardReadActivity.this, MainActivity.class);
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
                        ErrorRecord errorRecord = new ErrorRecord(null, false, "", "", "管理员读卡", "读到的卡号为空.", DataFormat.getNowTime(), "", "", "");
                        Track.getInstance(getApplicationContext()).setErrorCommand(errorRecord);
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

}
