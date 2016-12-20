package com.seekersoftvendingapp.network.entity.basedata;

import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.util.DataFormat;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class EmpPowerEntity implements Serializable {
    public boolean isDel;// 软删除的标记
    public String unit;// 周期单位
    public String begin;// 周期开始时间
    public String product;// 产品id
    public int count;// 数量
    public int period;// 周期
    public String objectId;
    public String createdAt;
    public String updatedAt;

    public EmpPower getEmpPower() {
        return new EmpPower(isDel, unit, DataFormat.fromISODate(begin), product, count, period, objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt));
    }

    @Override
    public String toString() {
        return "{ \n    unit = " + unit + ",\n product" + product + ",\n    count" + count + ",\n      period = " + period + ",\n   objectId="
                + objectId + ",\n   createdAt = " + createdAt + ",\n    updatedAt = " + updatedAt + "}";
    }
}
