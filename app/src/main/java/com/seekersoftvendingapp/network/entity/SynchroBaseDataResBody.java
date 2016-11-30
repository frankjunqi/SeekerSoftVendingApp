package com.seekersoftvendingapp.network.entity;

import com.seekersoftvendingapp.network.entity.obj.AdminCardEntity;
import com.seekersoftvendingapp.network.entity.obj.EmpPowerEntity;
import com.seekersoftvendingapp.network.entity.obj.EmployeeEntity;
import com.seekersoftvendingapp.network.entity.obj.PassageEntity;
import com.seekersoftvendingapp.network.entity.obj.ProductEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class SynchroBaseDataResBody implements Serializable {

    public int status;// 服务器返回的状态码
    public String msg;// 服务器返回具体状态码描述信息
    //public SyncharBaseEntity data = new SyncharBaseEntity();
    public String server_time;// 返回服务器时间

    public class SyncharBaseEntity implements Serializable {

        public List<AdminCardEntity> AdminCard = new ArrayList<AdminCardEntity>();
        public List<ProductEntity> Product = new ArrayList<ProductEntity>();
        public List<EmployeeEntity> Employee = new ArrayList<EmployeeEntity>();
        public List<EmpPowerEntity> EmpPower = new ArrayList<EmpPowerEntity>();
        public List<PassageEntity> Passage = new ArrayList<PassageEntity>();

    }
}
