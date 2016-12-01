package com.seekersoftvendingapp.network.entity.obj;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class EmpPowerEntity implements Serializable {

    public String unit;
    public BeginDate begin = new BeginDate();
    public EmPowerProduct product = new EmPowerProduct();

    public int count;
    public int period;
    public String objectId;
    public String createdAt;
    public String updatedAt;

    @Override
    public String toString() {
        return "{ \n    unit = " + unit + ",\n  begin = " + begin.toString() + ",\n     product" + product.toString() + ",\n    count" + count + ",\n      period = " + period + ",\n   objectId="
                + objectId + ",\n   createdAt = " + createdAt + ",\n    updatedAt = " + updatedAt + "}";
    }

    public class BeginDate implements Serializable {
        public String __type;
        public String iso;

        @Override
        public String toString() {
            return "{ \n        __type = " + __type + ",\n      iso = " + iso + "}";
        }
    }

    public class EmPowerProduct implements Serializable {
        public String __type;
        public String className;
        public String objectId;

        @Override
        public String toString() {
            return "{ \n        __type = " + __type + ",\n      className = " + className + ",\n        objectId = " + objectId + "}";
        }
    }

}
