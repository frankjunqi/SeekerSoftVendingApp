package com.seekersoftvendingapp.network.entity.obj;

import com.seekersoftvendingapp.database.table.Product;
import com.seekersoftvendingapp.util.DataFormat;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class ProductEntity implements Serializable {
    public boolean isDel;
    public String productName;
    public String cusProductName;
    public String objectId;
    public String createdAt;
    public String updatedAt;

    public Product getProduct() {
        return new Product(isDel, productName, cusProductName, objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt));
    }


    @Override
    public String toString() {
        return "{ \n    name = " + productName + ",\n  objectId = " + objectId + ",\n  createdAt = " + createdAt + ",\n    updateAt = " + updatedAt + ",\n     cusProduct = " + cusProductName + "}";
    }

}
