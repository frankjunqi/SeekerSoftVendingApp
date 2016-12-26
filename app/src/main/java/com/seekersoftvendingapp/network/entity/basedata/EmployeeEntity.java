package com.seekersoftvendingapp.network.entity.basedata;

import com.seekersoftvendingapp.database.table.Employee;
import com.seekersoftvendingapp.util.DataFormat;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class EmployeeEntity implements Serializable {
    public boolean isDel;// 软删除的标记
    public String empNo;// 员工编号
    public ArrayList<String> card = new ArrayList<String>();// 卡号（数组）
    public ArrayList<String> power = new ArrayList<String>();// 权限（数组）
    public String objectId;
    public String createdAt;
    public String updatedAt;


    public static void main(String[] args) throws Exception {
        ArrayList<String> card = new ArrayList<String>();
        card.add("123");
        card.add("456");
        card.add("789");
        System.out.print(card.toString());// [123, 456, 789]
    }

    public Employee getEmployee() {
        return new Employee(isDel, empNo, card.toString(), power.toString(), objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt),"","","");
    }

    @Override
    public String toString() {
        return "{ \n    empNo = " + empNo + ",\n    objectId = " + objectId + ",\n  createdAt" + createdAt +
                ",\n    updatedAt = " + updatedAt + ",\n    card = " + card.toString() + ",\n   power = " + power.toString() + "}";
    }
}
