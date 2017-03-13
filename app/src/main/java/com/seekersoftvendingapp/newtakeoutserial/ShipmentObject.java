package com.seekersoftvendingapp.newtakeoutserial;

/**
 * 出货指令实体类
 * Created by kjh08490 on 2017/2/14.
 */

public class ShipmentObject {
    public long timestamp = 121212;// 时间戳
    public int col = 1;
    public int row = 1;

    public int proNum = 0;

    public int getNum() {
        return col * 10 + row;
    }
}
