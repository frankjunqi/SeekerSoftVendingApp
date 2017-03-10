package com.seekersoftvendingapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.seekersoftvendingapp.database.dao.TestAdminCardDaoActivity;
import com.seekersoftvendingapp.database.dao.TestNoteDaoActivity;
import com.seekersoftvendingapp.database.rxdao.TestNoteRXDaoActivity;
import com.seekersoftvendingapp.image.TestFrescoActivity;
import com.seekersoftvendingapp.network.TestNetworkActivity;
import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.seekersoftvendingapp.test.TestCardReadActivity;
import com.seekersoftvendingapp.test.TestStoreActivity;
import com.seekersoftvendingapp.test.TestVendingActivity;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * http://blog.csdn.net/liu1164316159/article/details/38039841
 * http://blog.sina.com.cn/s/blog_accab4a90101deyv.html
 * http://blog.csdn.net/qq_29716061/article/details/51602173
 * A login screen that offers login via email/password.
 */
public class SettingActivity extends BaseActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // UI references.
    private TextView tv_permission;
    private View mProgressView;
    private View mLoginFormView;

    private Button btn_startapp;
    private Button btn_db_dao;
    private Button btn_db_rxdao;
    private Button btn_fresco;
    private Button btn_network;
    private Button btn_read_card;
    private Button btn_vending;
    private Button btn_store;
    private Button btn_db_admincard_dao;
    private Button btn_show_progress;
    private Button btn_hide_progress;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starttools);
        tv_permission = (TextView) findViewById(R.id.tv_permission);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Set up the login form.
        populateAutoComplete();

        // 启动App
        btn_startapp = (Button) findViewById(R.id.btn_startapp);
        btn_startapp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, StartAppActivity.class));
            }
        });

        // 数据库操作
        btn_db_dao = (Button) findViewById(R.id.btn_db_dao);
        btn_db_dao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TestNoteDaoActivity.class));
            }
        });

        // 数据库操作 AdminCard 表
        btn_db_admincard_dao = (Button) findViewById(R.id.btn_db_admincard_dao);
        btn_db_admincard_dao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TestAdminCardDaoActivity.class));
            }
        });

        // 数据库操作 RX 系列
        btn_db_rxdao = (Button) findViewById(R.id.btn_db_rxdao);
        btn_db_rxdao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TestNoteRXDaoActivity.class));
            }
        });

        // 图片加载 FRESCO
        btn_fresco = (Button) findViewById(R.id.btn_fresco);
        btn_fresco.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TestFrescoActivity.class));
            }
        });

        // 网络加载 ( POST GET )
        btn_network = (Button) findViewById(R.id.btn_network);
        btn_network.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TestNetworkActivity.class));
            }
        });

        // 读卡( ID IC )
        btn_read_card = (Button) findViewById(R.id.btn_read_card);
        btn_read_card.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TestCardReadActivity.class));
            }
        });

        // 螺纹柜的串口通信
        btn_vending = (Button) findViewById(R.id.btn_vending);
        btn_vending.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TestVendingActivity.class));
            }
        });


        // 格子柜的串口通信
        btn_store = (Button) findViewById(R.id.btn_store);
        btn_store.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, TestStoreActivity.class));
            }
        });

        btn_show_progress = (Button) findViewById(R.id.btn_show_progress);
        btn_show_progress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, WebviewActivity.class));
            }
        });

        btn_hide_progress = (Button) findViewById(R.id.btn_hide_progress);
        btn_hide_progress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                NewVendingSerialPort.getInstance().setOnDataReceiveListener(new NewVendingSerialPort.OnDataReceiveListener() {
                    @Override
                    public void onDataReceiveString(String IDNUM) {
                        Log.e("buffer: ",IDNUM);
                    }

                    @Override
                    public void onDataReceiveBuffer(byte[] buffer, int size) {
                        //Log.e("buffer: ",new String(buffer, 0, size));
                    }
                });
            }
        });

    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(tv_permission, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}

