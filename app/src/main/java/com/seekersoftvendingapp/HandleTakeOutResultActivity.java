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
import com.seekersoftvendingapp.network.entity.seekwork.MPickQueryByRFID;
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

public class HandleTakeOutResultActivity extends BaseActivity {


    private TextView tv_handle_result;

    private MRoad passage;
    private int number = 1;
    private String cardId = "";


    private int recordNum = 0;
    private int recordSuccess = 0;

    private static final int LUOWEN = 1;
    private static final int GEZI = 2;

    private ImageView vi_flag;


    // 判断是否是格子柜子消费
    private boolean isStoreSend = false;


    private Handler mHnadler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LUOWEN:
                    ResultObj resultObj = (ResultObj) msg.obj;
                    luowen(resultObj.isSuccess, resultObj.objectId);
                    break;
                case GEZI:
                    ResultObj resultObjG = (ResultObj) msg.obj;
                    gezi(resultObjG.isSuccess, resultObjG.objectId);
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

        setTitle("取货结果");

        passage = (MRoad) getIntent().getSerializableExtra(SeekerSoftConstant.PASSAGE);
        number = getIntent().getIntExtra(SeekerSoftConstant.TakeoutNum, 1);
        cardId = getIntent().getStringExtra(SeekerSoftConstant.CardNum);

        if (passage != null) {
            // 判断是否是格子柜消费
            if ("1".equals(passage.getCabType())) {
                isStoreSend = true;
                number = passage.getQty();
            }
        }

        tv_handle_result = (TextView) findViewById(R.id.tv_handle_result);

        vi_flag = (ImageView) findViewById(R.id.vi_flag);

        btn_return_mainpage = (Button) findViewById(R.id.btn_return_mainpage);
        btn_return_mainpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HandleTakeOutResultActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                HandleTakeOutResultActivity.this.finish();
            }
        });

        if (SeekerSoftConstant.NETWORKCONNECT) {
            pickQueryByRFID(cardId);
        }

    }

    @Override
    public int setEndTime() {
        return SeekerSoftConstant.ENDTIEMSHORT;
    }


    // 刷卡取货
    private void pickQueryByRFID(final String cardNo) {
        showProgress();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        final Call<SrvResult<MPickQueryByRFID>> mRoadAction = service.pickQueryByRFID(cardNo, SeekerSoftConstant.machine, passage.getCabNo(), passage.getRoadCode(), number
        );
        mRoadAction.enqueue(new Callback<SrvResult<MPickQueryByRFID>>() {
            @Override
            public void onResponse(Call<SrvResult<MPickQueryByRFID>> call, Response<SrvResult<MPickQueryByRFID>> response) {
                if (response != null && response.body() != null && response.body().getData() != null) {
                    // 成功逻辑
                    if (response.body().getData().isAuthorize()) {
                        // 硬件编号，用户出货
                        cmdBufferVendingStoreSerial("");
                    } else {
                        handleResult(new TakeOutError(TakeOutError.HAS_NOPOWER_FLAG));
                        Toast.makeText(HandleTakeOutResultActivity.this, "提示：此卡无权取货。", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // 无数据
                    handleResult(new TakeOutError(TakeOutError.ERROR_FLAG));
                    Toast.makeText(HandleTakeOutResultActivity.this, "提示：后台权限校验失败，请联系管理员。", Toast.LENGTH_LONG).show();
                }
                hideProgress();
            }

            @Override
            public void onFailure(Call<SrvResult<MPickQueryByRFID>> call, Throwable throwable) {
                // 异常
                hideProgress();
                handleResult(new TakeOutError(TakeOutError.NO_NETWORK_FLAG));
                Toast.makeText(HandleTakeOutResultActivity.this, "提示：网络异常。", Toast.LENGTH_LONG).show();
            }
        });

    }


    /**
     * 打开螺纹柜子串口设备出货
     *
     * @param objectId 出货的服务端的记录的objectid
     */
    private void cmdBufferVendingStoreSerial(final String objectId) {
        vi_flag.setVisibility(View.VISIBLE);
        if (isStoreSend) {
            NewVendingSerialPort.SingleInit().setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                @Override
                public void onCmdCallBack(boolean isSuccess) {
                    Message msg = new Message();
                    msg.what = GEZI;
                    ResultObj resultObj = new ResultObj();
                    resultObj.isSuccess = isSuccess;
                    resultObj.objectId = objectId;
                    msg.obj = resultObj;
                    mHnadler.sendMessage(msg);
                }
            });
            ShipmentObject shipmentObject = new ShipmentObject();
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
            shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
            NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject);
        } else {
            NewVendingSerialPort.SingleInit().setOnCmdCallBackListen(new NewVendingSerialPort.OnCmdCallBackListen() {
                @Override
                public void onCmdCallBack(boolean isSuccess) {
                    Message msg = new Message();
                    msg.what = LUOWEN;
                    ResultObj resultObj = new ResultObj();
                    resultObj.isSuccess = isSuccess;
                    resultObj.objectId = objectId;
                    msg.obj = resultObj;
                    mHnadler.sendMessage(msg);
                }
            });
            for (int i = 0; i < number; i++) {
                ShipmentObject shipmentObject = new ShipmentObject();
                // 螺纹柜子
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
                shipmentObject.objectId = shipmentObject.containerNum + shipmentObject.proNum;
                NewVendingSerialPort.SingleInit().pushCmdOutShipment(shipmentObject);
            }
        }
    }

    private void gezi(boolean isSuccess, String objectId) {
        if (isSuccess) {
            recordSuccess = passage.getQty();
            handleNewVendingSerialPort(true);
        } else {
            handleNewVendingSerialPort(false);
        }
    }

    private void luowen(boolean isSuccess, String objectId) {
        recordNum++;
        if (!isSuccess) {
            if (number > 1) {
                tv_handle_result.setText("取货第" + recordNum + "个失败...");
            }
        } else {
            recordSuccess++;
            if (number > 1) {
                tv_handle_result.setText("取货第" + recordNum + "个成功...");
            }
        }
        if (recordNum == number && recordSuccess == recordNum) {
            handleNewVendingSerialPort(true);
        } else if (recordNum == number) {
            handleNewVendingSerialPort(false);
        }
    }


    private void handleNewVendingSerialPort(boolean isSuccessOpen) {
        // 成功
        if (isSuccessOpen) {
            // 串口打开螺纹柜子成功
            handleResult(new TakeOutError(TakeOutError.CAN_TAKEOUT_FLAG));
            // 接口 成功
            pickSuccess(cardId);
        }
        // 失败
        else {
            // 串口打开螺纹柜子失败
            if (isStoreSend) {
                handleResult(new TakeOutError(TakeOutError.OPEN_GEZI_SERIAL_FAILED_FLAG));
            } else {
                handleResult(new TakeOutError(TakeOutError.OPEN_LUOWEN_SERIAL_FAILED_FLAG));
            }
        }
    }

    // 取货成功调用接口
    private void pickSuccess(String cardNo) {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Host.HOST).addConverterFactory(GsonConverterFactory.create()).build();
        SeekWorkService service = retrofit.create(SeekWorkService.class);
        Call<SrvResult<Boolean>> mRoadAction = service.pickSuccess(cardNo, SeekerSoftConstant.machine, passage.getCabNo(), passage.getRoadCode(), recordSuccess);
        LogCat.e("url = " + mRoadAction.request().url().toString());
        mRoadAction.enqueue(new Callback<SrvResult<Boolean>>() {
            @Override
            public void onResponse(Call<SrvResult<Boolean>> call, Response<SrvResult<Boolean>> response) {
                // 提示出货成功，关闭取货页面，返回道首页

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

}
