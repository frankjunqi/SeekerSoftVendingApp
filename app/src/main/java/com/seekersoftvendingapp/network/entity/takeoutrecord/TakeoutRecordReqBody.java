package com.seekersoftvendingapp.network.entity.takeoutrecord;

import com.seekersoftvendingapp.database.table.TakeoutRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/12/7.
 */

public class TakeoutRecordReqBody implements Serializable {

    public String deviceId;

    public List<TakeoutRecord> record = new ArrayList<TakeoutRecord>();

}
