package com.seekersoftvendingapp.network.entity.borrowrecord;

import com.seekersoftvendingapp.database.table.BorrowRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/12/7.
 */

public class BorrowRecordReqBody implements Serializable {
    public String deviceId;
    public List<BorrowRecord> record = new ArrayList<BorrowRecord>();
}
