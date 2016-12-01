package com.seekersoftvendingapp.network.entity.obj;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class EmployeeEntity implements Serializable {

    public String empNo;
    public ArrayList<String> card = new ArrayList<String>();
    public ArrayList<String> power = new ArrayList<String>();
    public String objectId;
    public String createdAt;
    public String updatedAt;


    @Override
    public String toString() {
        return "{ \n    empNo = " + empNo + ",\n    objectId = " + objectId + ",\n  createdAt" + createdAt +
                ",\n    updatedAt = " + updatedAt + ",\n    card = " + card.toString() + ",\n   power = " + power.toString() + "}";
    }
}
