package com.seekersoftvendingapp;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.database.table.DaoSession;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.PassageDao;
import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekerSoftService;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordObj;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordReqBody;
import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordResBody;
import com.seekersoftvendingapp.network.entity.updata.UpdateResBody;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.track.Track;
import com.seekersoftvendingapp.updateapk.TCTInsatllActionBroadcastReceiver;
import com.seekersoftvendingapp.util.DataFormat;
import com.seekersoftvendingapp.util.LogCat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 4. 管理员 管理页面
 * Created by kjh08490 on 2016/11/28.
 */

public class ManagerGoodsActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_onekeyinsert;
    private Button btn_onebyoneinsert;
    private Button btn_exit;
    private Button btn_update;

    private AdminCard adminCard;
    private PassageDao passageDao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managergoods);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);

        setTitle("管理页面");

        DaoSession daoSession = ((SeekersoftApp) getApplication()).getDaoSession();
        passageDao = daoSession.getPassageDao();

        adminCard = (AdminCard) getIntent().getSerializableExtra(SeekerSoftConstant.ADMINCARD);

        btn_onekeyinsert = (Button) findViewById(R.id.btn_onekeyinsert);
        btn_onebyoneinsert = (Button) findViewById(R.id.btn_onebyoneinsert);
        btn_exit = (Button) findViewById(R.id.btn_exit);
        btn_return_mainpage = (Button) findViewById(R.id.btn_backtomain);
        btn_update = (Button) findViewById(R.id.btn_update);

        btn_onekeyinsert.setOnClickListener(this);
        btn_onebyoneinsert.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
        btn_return_mainpage.setOnClickListener(this);
        btn_update.setOnClickListener(this);

        // 同步基础数据
        Track.getInstance(getApplicationContext()).synchroDataToServer();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_onekeyinsert:
                if (SeekerSoftConstant.NETWORKCONNECT) {
                    plainDialogDemo();
                } else {
                    needNetwork();
                }
                break;
            case R.id.btn_onebyoneinsert:
                if (true) {
                    startActivity(new Intent(ManagerGoodsActivity.this, ManagerPassageActivity.class));
                } else {
                    needNetwork();
                }
                break;
            case R.id.btn_update:
                // 软件升级
                new AlertDialog.Builder(ManagerGoodsActivity.this)
                        .setTitle("系统升级")
                        .setMessage("你确定要进行系统升级吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateAPP();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).setCancelable(false).show();
                break;
            case R.id.btn_exit:
                if (SeekerSoftConstant.NETWORKCONNECT) {
                    exitDialog();
                } else {
                    needNetwork();
                }
                break;
            case R.id.btn_backtomain:
                Intent intent = new Intent(ManagerGoodsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }

    /**
     * 退出系统弹框提醒
     */
    private void exitDialog() {
        new AlertDialog.Builder(ManagerGoodsActivity.this)
                .setTitle("退出系统")
                .setMessage("你确定要退出系统吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent exitIntent = new Intent(ManagerGoodsActivity.this, MainActivity.class);
                        exitIntent.putExtra(SeekerSoftConstant.EXITAPP, SeekerSoftConstant.EXITAPPFALG);
                        exitIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(exitIntent);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();
    }

    /**
     * 一键补货
     */
    private void plainDialogDemo() {
        new AlertDialog.Builder(ManagerGoodsActivity.this)
                .setTitle("一键补货")
                .setMessage("确定把所有货道上的货品设置成最大库存？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 一键补货,补货到最大库存
                        asyncSupplyRecordRequest();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCancelable(false).show();
    }

    /**
     * 提交补货记录 POST
     */
    private void asyncSupplyRecordRequest() {
        showProgress();
        final List<Passage> passageList = passageDao.queryBuilder().where(PassageDao.Properties.IsDel.eq(false)).list();
        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        SupplyRecordReqBody supplyRecordReqBody = new SupplyRecordReqBody();
        supplyRecordReqBody.deviceId = SeekerSoftConstant.DEVICEID;
        // 处理补货记录
        for (Passage passage : passageList) {
            SupplyRecordObj supplyRecordObj = new SupplyRecordObj();
            supplyRecordObj.passage = (TextUtils.isEmpty(passage.getFlag()) ? "" : passage.getFlag()) + passage.getSeqNo();
            supplyRecordObj.card = SeekerSoftConstant.ADMINCARD;
            supplyRecordObj.count = passage.getCapacity() - passage.getStock();
            supplyRecordObj.time = DataFormat.getNowTime();
            supplyRecordReqBody.record.add(supplyRecordObj);
        }
        Gson gson = new Gson();
        String josn = gson.toJson(supplyRecordReqBody);
        LogCat.e("supplyRecord = " + josn);

        Call<SupplyRecordResBody> postAction = service.supplyRecord(supplyRecordReqBody);
        postAction.enqueue(new Callback<SupplyRecordResBody>() {
            @Override
            public void onResponse(Call<SupplyRecordResBody> call, Response<SupplyRecordResBody> response) {
                if (response != null && response.body() != null) {
                    for (Passage passage : passageList) {
                        passage.setStock(passage.getCapacity());
                    }
                    passageDao.insertOrReplaceInTx(passageList);
                    Toast.makeText(ManagerGoodsActivity.this, "补货成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManagerGoodsActivity.this, "supply Record: Failure", Toast.LENGTH_SHORT).show();
                    LogCat.e("supply Record: Failure");
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<SupplyRecordResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(ManagerGoodsActivity.this, "supply Record:  Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void needNetwork() {
        if (!SeekerSoftConstant.NETWORKCONNECT) {
            new AlertDialog.Builder(ManagerGoodsActivity.this)
                    .setTitle("需要联网")
                    .setMessage("确定已经连上网络，此页面操作需要在联网状态下进行？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).setCancelable(false).show();
        }
    }


    /**
     * 更新app接口
     */
    private boolean isDownloading = false;
    private ContentObserver contentObserver;

    public void updateAPP() {
        if (isDownloading) {
            Toast.makeText(ManagerGoodsActivity.this, "最新版本正在下载....", Toast.LENGTH_SHORT).show();
            return;
        }
        showProgress();
        // 异步加载(post)
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekerSoftService service = retrofit.create(SeekerSoftService.class);
        Call<UpdateResBody> postAction = service.updateApp(SeekerSoftConstant.DEVICEID, String.valueOf(getVersionCode(ManagerGoodsActivity.this)));
        postAction.enqueue(new Callback<UpdateResBody>() {
            @Override
            public void onResponse(Call<UpdateResBody> call, Response<UpdateResBody> response) {
                hideProgress();
                if (response != null && response.body() != null && response.body().data.result) {
                    // 下载apk
                    Toast.makeText(ManagerGoodsActivity.this, "Downloding VendingAPP.apk ... ", Toast.LENGTH_SHORT).show();
                    if (isDownloading) {
                        return;
                    }
                    downloadApk(response.body().data.url);
                } else {
                    Toast.makeText(ManagerGoodsActivity.this, "当前版本已经是最新版本。", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UpdateResBody> call, Throwable throwable) {
                hideProgress();
                Toast.makeText(ManagerGoodsActivity.this, "网络链接异常。", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadApk(String apkUrl) {
        isDownloading = true;
        // 下载
        try {
            final DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            clearDownLoadApk(downloadManager);
            Uri uri = Uri.parse(apkUrl);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setTitle("VendingAPP");
            // 指定文件保存在应用的私有目录
            request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, TCTInsatllActionBroadcastReceiver.APK_FILE_NAME);
            long reference = 0;
            try {
                reference = downloadManager.enqueue(request);// 下载id
            } catch (IllegalArgumentException e) {
                return;
            }
            final long id = reference;
            // 注册数据库监听
            getContentResolver().registerContentObserver(
                    Uri.parse("content://downloads/my_downloads"),
                    true,
                    contentObserver = new ContentObserver(null) {
                        @Override
                        public void onChange(boolean selfChange) {
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(id);
                            Cursor my = downloadManager.query(query);
                            if (my != null) {
                                if (my.moveToFirst()) {
                                    int fileSize = my.getInt(my
                                            .getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                    int bytesDL = my.getInt(my
                                            .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    int percent = bytesDL * 100 / fileSize;

                                    if (percent == 100) {
                                        isDownloading = false;
                                    }
                                }
                                my.close();
                            } else {
                                isDownloading = false;
                            }
                        }
                    });
        } catch (Exception e) {
            isDownloading = false;
        }
    }

    /**
     * 删除之前下载的apk文件
     */
    private void clearDownLoadApk(final DownloadManager manager) {
        // 删除失败和成功状态的数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                int delete[] = {DownloadManager.STATUS_FAILED, DownloadManager.STATUS_SUCCESSFUL};
                DownloadManager.Query query = new DownloadManager.Query();
                for (int element : delete) {
                    query.setFilterByStatus(element);
                    Cursor cursor = manager.query(query);
                    if (cursor != null) {
                        if (cursor.moveToFirst()) {
                            do {
                                manager.remove(cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID)));
                            } while (cursor.moveToNext());
                        }
                        cursor.close();
                    }
                }
            }
        }).start();
    }


    public int getVersionCode(Context context)//获取版本号(内部识别号)
    {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

}
