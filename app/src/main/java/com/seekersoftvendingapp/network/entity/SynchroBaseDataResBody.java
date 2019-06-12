package com.seekersoftvendingapp.network.entity;

import com.seekersoftvendingapp.network.entity.basedata.AdminCardEntity;
import com.seekersoftvendingapp.network.entity.basedata.BoxEntity;
import com.seekersoftvendingapp.network.entity.basedata.EmpCardEntity;
import com.seekersoftvendingapp.network.entity.basedata.EmpPowerEntity;
import com.seekersoftvendingapp.network.entity.basedata.PassageEntity;
import com.seekersoftvendingapp.network.entity.basedata.ProductEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class SynchroBaseDataResBody implements Serializable {

    public int status;// 服务器返回的状态码
    public String message;// 服务器返回具体状态码描述信息
    public SyncharBaseEntity data = new SyncharBaseEntity();
    public String server_time;// 返回服务器时间



    public class SyncharBaseEntity implements Serializable {
        public List<AdminCardEntity> AdminCard = new ArrayList<AdminCardEntity>();
        public List<EmpCardEntity> EmpCard = new ArrayList<EmpCardEntity>();
        public List<ProductEntity> Product = new ArrayList<ProductEntity>();
        public List<EmpPowerEntity> EmpPower = new ArrayList<EmpPowerEntity>();
        public List<PassageEntity> Passage = new ArrayList<PassageEntity>();

        public List<BoxEntity> Box = new ArrayList<>();

    }

    public String getMachine() {
        if (data.Box != null && data.Box.size() > 0) {
            return "柜号: " + data.Box.get(0).machine;
        } else {
            return "";
        }
    }

    public String getPhoneDesc() {
        if (data.Box != null && data.Box.size() > 0) {
            return "紧急情况联系人：" + data.Box.get(0).connecter + " \n" + "联系方式：" + data.Box.get(0).phone;
        } else {
            return "";
        }
    }

    public String getModle() {
        if (data.Box != null && data.Box.size() > 0) {
            return data.Box.get(0).model;
        } else {
            return "";
        }
    }

}
