package com.seekersoftvendingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.seekersoftvendingapp.network.api.Host;
import com.seekersoftvendingapp.network.api.SeekWorkService;
import com.seekersoftvendingapp.network.api.SrvResult;
import com.seekersoftvendingapp.network.entity.ResultObj;
import com.seekersoftvendingapp.network.entity.seekwork.MRoad;
import com.seekersoftvendingapp.network.gsonfactory.GsonConverterFactory;
import com.seekersoftvendingapp.newtakeoutserial.NewVendingSerialPort;
import com.seekersoftvendingapp.newtakeoutserial.ShipmentObject;
import com.seekersoftvendingapp.util.LogCat;
import com.seekersoftvendingapp.util.SeekerSoftConstant;
import com.seekersoftvendingapp.util.TakeOutError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * 成功 失败 页面（包括成功，失败描述信息页面） 共用
 * Created by kjh08490 on 2016/11/25.
 */

public class HandleBorrowResultActivity extends BaseActivity {


    private TextView tv_handle_result;

    private String cardId = "";

    private MRoad passage;

    private static final int GEZI = 1;

    private ImageView vi_flag;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GEZI:
                    ResultObj resultObj = (ResultObj) msg.obj;
                    handleStoreSerialPort(resultObj.isSuccess, resultObj.objectId);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handleresult);

        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_right = (TextView) findViewById(R.id.tv_right);
        vi_flag = (ImageView) findViewById(R.id.vi_flag);

        setTitle("借还结果");

        passage = (MRoad) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        cardId = getIntent().getStringExtra(SeekerSoftConstant.CardNum);

        tv_handle_result = (TextView) findViewById(R.id.tv_handle_result);

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HandleBorrowResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                HandleBorrowResultActivity.this.finish();
            }
        });

        if (SeekerSoftConstant.NETWORKCONNECT) {
            authBorrowAndBack(cardId, 0);
        }
    }

    /**
     * 打开柜子串口设备出货
     *
     * @return
     */
    private void cmdBufferVendingSerial(final String objectId) {
        vi_flag.setVisibility(View.VISIBLE);

        ShipmentObject shipmentObject = new ShipmentObject();
        try {
            NewVendingSerialPort.SingleInit().setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                @Override
                public void onCmdCallBack(boolean isSuccess) {

                    Message msg = new Message();
                    msg.what = GEZI;

                    ResultObj resultObj = new ResultObj();
                    resultObj.isSuccess = isSuccess;
                    resultObj.objectId = objectId;
                    msg.obj = resultObj;

                    mHandler.sendMessage(msg);
                }
            });
            // 格子柜子
            // 1:主柜 2:A 3:B 4:C
            if ("主柜".equals(passage.getCabNo())) {
                shipmentObject.containerNum = 1;
            } else if ("A".equals(passage.getCabNo())) {
                shipmentObject.containerNum = 2;
            } else if ("B".equals(passage.getCabNo())) {
                shipmentObject.containerNum = 3;
            } else if ("C".equals(passage.getCabNo())) {
                shipmentObject.containerNum = 4;
            }
            shipmentObject.proNum = passage.getRealCode();
            // 需要生成唯一码
            shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
            NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject);

        } catch (Exception e) {
            handleStoreSerialPort(false, objectId);
        }
    }

    private void handleStoreSerialPort(boolean isSuccess, String objectId) {
        if (isSuccess) {
            // 串口打开柜子成功
            handleResult(new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG));
            // 借成功
            borrowComplete(cardId);
        } else {
            // 串口打开柜子失败
            handleResult(new TakeOutError(TakeOutError.OPEN_GEZI_SERIAL_FAILED_FLAG));
        }
    }

    // 借货成功确认
    private void borrowComplete(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.borrowComplete(cardNo, SeekerSoftConstant.machine, passage.getCabNo(), passage.getRoadCode(), passage.getQty());
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
            }
        });
    }

    /**
     * 处理本地消费结果（到结果页面）
     */
    private void handleResult(TakeOutError takeOutError) {
        if (takeOutError != null) {
            if (TextUtils.isEmpty(takeOutError.serverMsg)) {
                tv_handle_result.setText(takeOutError.getTakeOutMsg());
            } else {
                tv_handle_result.setText(takeOutError.serverMsg);
            }
        }
        btn_return_mainpage.setVisibility(View.VISIBLE);
        countDownTimer.start();
    }

    private void authBorrowAndBack(final String cardNo, int borrowBackType) {
        showProgress();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        final Call<SrvResult<Boolean>> mRoadAction = service.authBorrowAndBack(cardNo, SeekerSoftConstant.machine, passage.getCabNo(), passage.getRoadCode(), borrowBackType);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, final Response<SrvResult<Boolean>> response) {
                if (response != null && response.body() != null && response.body().getData()) {
                    // 操作格子柜 ，硬件编号，用户出货
                    cmdBufferVendingSerial("");
                } else {
                    handleResult(new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG));
                    Toast.makeText(HandleBorrowResultActivity.this, "提示：此卡无权取货。", Toast.LENGTH_LONG).show();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<SrvResult<Boolean>> call, Throwable throwable) {
                hideProgress();
                handleResult(new TakeOutError(TakeOutError.NO_NETWORK_FLAG));
                Toast.makeText(HandleBorrowResultActivity.this, "提示：网络异常。", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int setEndTime() {
        return SeekerSoftConstant.ENDTIEMSHORT;
    }
}
