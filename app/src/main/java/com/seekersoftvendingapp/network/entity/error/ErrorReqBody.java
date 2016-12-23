package com.seekersoftvendingapp.network.entity.error;

import com.seekersoftvendingapp.database.table.ErrorRecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/12/7.
 */

public class ErrorReqBody implements Serializable {
    public String deviceId;
    public List<ErrorRecord> error = new ArrayList<ErrorRecord>();
}
