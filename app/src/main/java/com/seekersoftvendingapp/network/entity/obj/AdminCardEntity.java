package com.seekersoftvendingapp.network.entity.obj;

import com.seekersoftvendingapp.database.table.AdminCard;
import com.seekersoftvendingapp.util.DataFormat;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class AdminCardEntity implements Serializable {

    public String card; // card id
    public String objectId;
    public String createdAt;
    public String updatedAt;

    public AdminCard getAdminCard() {
        return new AdminCard(null, card, objectId, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt));
    }

    @Override
    public String toString() {
        return "{ \n    card =" + card + ",\n   objectId = " + objectId + ",\n  createdAt = " + createdAt + ",\n    updateAt = " + updatedAt + "}";
    }
}
