package com.seekersoftvendingapp.network.entity.obj;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class PassageEntity implements Serializable {

    public int capacity;
    public PassageProduct product = new PassageProduct();

    public String seqNo;
    public boolean borrowState;
    public int stock;
    public int whorlSize;
    public boolean isSend;
    public String objectId;
    public String createdAt;
    public String updatedAt;

    public class PassageProduct implements Serializable {
        public String __type;
        public String className;
        public String objectId;
    }

}
