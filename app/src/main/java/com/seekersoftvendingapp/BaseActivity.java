package com.seekersoftvendingapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.seekersoftvendingapp.util.SeekerSoftConstant;

/**
 * Created by kjh08490 on 2016/12/16.
 */

public class BaseActivity extends Activity {
    private Dialog progressDialog;
    protected Button btn_return_mainpage;

    protected TextView tv_title;
    protected TextView tv_right;

    public void setTitle(String title) {
        if (tv_title != null && !TextUtils.isEmpty(title)) {
            tv_title.setText(title);
        }
    }

    public void setRightTitle(String rightTitle, View.OnClickListener onClickListener) {
        if (tv_right != null && !TextUtils.isEmpty(rightTitle)) {
            tv_right.setVisibility(View.VISIBLE);
            tv_right.setText(rightTitle);
        }

        if (tv_right != null) {
            tv_right.setOnClickListener(onClickListener);
        }

    }

    protected CountDownTimer countDownTimer = new CountDownTimer(setEndTime() * 1000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            if (btn_return_mainpage != null) {
                btn_return_mainpage.setText("返回(" + millisUntilFinished / 1000 + "秒)");
            }
        }

        @Override
        public void onFinish() {
            BaseActivity.this.finish();
        }
    };

    /**
     * 设置倒计时初始时间
     *
     * @return
     */
    public int setEndTime() {
        return SeekerSoftConstant.ENDTIEMLONG;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 保持常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void showProgress() {
        if (progressDialog == null) {
            progressDialog = new Dialog(this, R.style.progress_dialog);
            progressDialog.setContentView(R.layout.dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
            msg.setText("数据加载请求中...");
        }
        progressDialog.show();
    }

    public void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
