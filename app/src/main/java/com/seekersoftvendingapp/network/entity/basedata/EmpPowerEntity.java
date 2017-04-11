package com.seekersoftvendingapp.network.entity.basedata;

import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.util.DataFormat;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class EmpPowerEntity implements Serializable {
    public String emp;
    public boolean isDel;// 软删除的标记
    public String unit;// 周期单位
    public String product;// 产品id
    public int count;// 数量
    public int used;
    public int period;// 周期
    public String objectId;
    public String createdAt;
    public String updatedAt;

    public EmpPower getEmpPower() {
        return new EmpPower(emp, unit, isDel, product, count, period, used, objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt), "", "", "");
    }
}
