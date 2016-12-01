package com.seekersoftvendingapp.network.entity.obj;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class ProductEntity implements Serializable {

    public String name;
    public CustomProduct cusProduct = new CustomProduct();
    public String objectId;
    public String createdAt;
    public String updatedAt;


    public class CustomProduct implements Serializable {
        public String cusProductName;
        public String objectId;
        public String createdAt;
        public String updatedAt;

        @Override
        public String toString() {
            return "{ \n        cusProductName = " + cusProductName + ",\n      objectId" + objectId + ",\n     createdAt" + createdAt + ",\n       updatedAt " + updatedAt + "}";
        }
    }

    @Override
    public String toString() {
        return "{ \n    name = " + name + ",\n  objectId = " + objectId + ",\n  createdAt = " + createdAt + ",\n    updateAt = " + updatedAt + ",\n     cusProduct = " + cusProduct.toString() + "}";
    }

}
