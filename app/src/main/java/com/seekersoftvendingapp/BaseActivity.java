package com.seekersoftvendingapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kjh08490 on 2016/12/16.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        // do nothing
    }
}
