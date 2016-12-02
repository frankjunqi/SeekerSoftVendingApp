package com.seekersoftvendingapp.network.entity.obj;

import com.seekersoftvendingapp.database.table.Passage;
import com.seekersoftvendingapp.util.DataFormat;

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


    public Passage getPassage() {
        return new Passage(null, capacity, product.objectId, seqNo, borrowState, stock, whorlSize, isSend, objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt));
    }

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
