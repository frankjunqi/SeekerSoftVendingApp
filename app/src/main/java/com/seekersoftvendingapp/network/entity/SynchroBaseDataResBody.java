package com.seekersoftvendingapp.network.entity;

import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.database.table.EmpCard;
import com.seekersoftvendingapp.database.table.EmpPower;
import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.database.table.Product;
import com.seekersoftvendingapp.network.entity.basedata.AdminCardEntity;
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

    public List<AdminCard> getAdminCardList() {
        ArrayList<AdminCard> adminCards = new ArrayList<AdminCard>();
        if (data.AdminCard != null) {
            for (AdminCardEntity adminCardEntity : data.AdminCard) {
                adminCards.add(adminCardEntity.getAdminCard());
            }
        }
        return adminCards;
    }

    public List<Product> getProductList() {
        ArrayList<Product> products = new ArrayList<Product>();
        if (data.Product != null) {
            for (ProductEntity productEntity : data.Product) {
                products.add(productEntity.getProduct());
            }
        }
        return products;
    }

    public List<EmpCard> getEmpCardList() {
        List<EmpCard> empCardes = new ArrayList<EmpCard>();
        if (data.EmpCard != null) {
            for (EmpCardEntity empCardEntity : data.EmpCard) {
                empCardes.add(empCardEntity.getEmpCard());
            }
        }
        return empCardes;
    }

    public List<EmpPower> getEmpPowerList() {
        List<EmpPower> emppowers = new ArrayList<EmpPower>();
        if (data.EmpPower != null) {
            for (EmpPowerEntity emppowerEntity : data.EmpPower) {
                emppowers.add(emppowerEntity.getEmpPower());
            }
        }
        return emppowers;
    }

    public List<Passage> getPassageList() {
        List<Passage> passages = new ArrayList<Passage>();
        if (data.Passage != null) {
            for (PassageEntity passageEntity : data.Passage) {
                passages.add(passageEntity.getPassage());
            }
        }
        return passages;
    }

    public class SyncharBaseEntity implements Serializable {
        public List<AdminCardEntity> AdminCard = new ArrayList<AdminCardEntity>();
        public List<EmpCardEntity> EmpCard = new ArrayList<EmpCardEntity>();
        public List<ProductEntity> Product = new ArrayList<ProductEntity>();
        public List<EmpPowerEntity> EmpPower = new ArrayList<EmpPowerEntity>();
        public List<PassageEntity> Passage = new ArrayList<PassageEntity>();

    }
}
