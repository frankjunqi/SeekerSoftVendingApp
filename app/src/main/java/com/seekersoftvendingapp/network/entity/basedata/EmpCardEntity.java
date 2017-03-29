package com.seekersoftvendingapp.network.entity.basedata;

import com.seekersoftvendingapp.database.table.EmpCard;
import com.seekersoftvendingapp.util.DataFormat;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2017/3/29.
 */

public class EmpCardEntity implements Serializable {

    public String emp;//"58da790d0ce463005719d7a0",
    public String card;//card: "123",
    public boolean isDel;//true,
    public String createdAt;//"2017-03-28T14:54:06.039Z",
    public String updatedAt;//"2017-03-28T15:45:09.070Z",
    public String objectId;//"58da790e5c497d00570d61e7"

    public EmpCard getEmpCard() {
        return new EmpCard(card, emp, isDel, DataFormat.fromISODate(createdAt), DataFormat.fromISODate(updatedAt), objectId, "", "", "");
    }
}
