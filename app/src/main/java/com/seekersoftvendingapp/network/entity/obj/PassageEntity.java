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

    @Override
    public String toString() {
        return "{ \n    capacity = " + capacity + ",\n  seqNo = " + seqNo + ",\n    whorlSize = " + whorlSize + ",\n    isSend = " + isSend + ",\n      product"
                + product.toString() + ",\n     borrowState" + borrowState + ",\n    stock = " + stock + ",\n    objectId=" + objectId + ",\n    createdAt = " + createdAt + ",\n    updatedAt = " + updatedAt + "}";
    }

    public class PassageProduct implements Serializable {
        public String __type;
        public String className;
        public String objectId;

        @Override
        public String toString() {
            return "{ \n        __type = " + __type + ",\n      className = " + className + ",\n        objectId = " + objectId + "}";
        }
    }

}
