package com.seekersoftvendingapp.network.entity.basedata;

import com.seekersoftvendingapp.database.table.Product;
import com.seekersoftvendingapp.util.DataFormat;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class ProductEntity implements Serializable {
    public boolean isDel;// 软删除的标记
    public String productName;// 产品名称
    public String cusProductName;// 产品在客户的名称
    public String objectId;// 产品唯一id
    public String createdAt;
    public String updatedAt;

    public Product getProduct() {
        return new Product(isDel, productName, cusProductName, objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt), "", "", "");
    }

}
