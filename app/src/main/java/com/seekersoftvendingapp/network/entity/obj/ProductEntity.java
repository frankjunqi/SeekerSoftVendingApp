package com.seekersoftvendingapp.network.entity.obj;

import com.seekersoftvendingapp.database.table.Product;
import com.seekersoftvendingapp.util.DataFormat;

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

    public Product getProduct() {
        return new Product(null, name, cusProduct.cusProductName, cusProduct.objectId, DataFormat.formatDate(cusProduct.createdAt), DataFormat.formatDate(cusProduct.updatedAt), objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt));
    }

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
