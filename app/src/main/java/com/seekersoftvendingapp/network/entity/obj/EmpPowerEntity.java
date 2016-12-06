package com.seekersoftvendingapp.network.entity.obj;

import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.util.DataFormat;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class EmpPowerEntity implements Serializable {

    public String unit;

    public String product;

    public int count;
    public int period;
    public String objectId;
    public String createdAt;
    public String updatedAt;

    public EmpPower getEmpPower() {
        return new EmpPower(null, unit, product, count, period, objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt));
    }

    @Override
    public String toString() {
        return "{ \n    unit = " + unit + ",\n product" + product + ",\n    count" + count + ",\n      period = " + period + ",\n   objectId="
                + objectId + ",\n   createdAt = " + createdAt + ",\n    updatedAt = " + updatedAt + "}";
    }
}
