package com.seekersoftvendingapp.network.entity.error;

import com.seekersoftvendingapp.network.entity.supplyrecord.SupplyRecordObj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/12/7.
 */

public class ErrorReqBody implements Serializable {
    public String deviceId;

    public List<ErrorObj> error = new ArrayList<ErrorObj>();
}
