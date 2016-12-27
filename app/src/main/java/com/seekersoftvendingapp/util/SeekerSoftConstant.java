package com.seekersoftvendingapp.util;

/**
 * Created by kjh08490 on 2016/11/25.
 */

public class SeekerSoftConstant {

    // 设备号
    public static String DEVICEID = "123";
    public static String CARDID = "123456";// 585000b2128fe1006931525a   ||   98152

    public static final String ADMINCARDNUM = "12312";

    // 网络是否链接
    public static boolean NETWORKCONNECT = true;

    // 读取卡中的数据的消息
    public static final int CARDRECEIVECODE = 1101;

    // 管理员读取卡中的数据的消息
    public static final int ADMINCARDRECECIVECODE = 1102;
    // 管理员卡号key
    public static String ADMINCARD = "admincard";


    // 退出程序key
    public static final String EXITAPP = "exitapp";
    public static final int EXITAPPFALG = 1;

    public final static int BASEDATALOOPER = 3000;

    public final static String PASSAGE = "PASSAGE";// new Passga()
    public final static String TAKEOUTERROR = "TAKEOUTERROR";

    public final static int TIMELOGN = 1000 * 5;// 基础数据的同步时间长

    public final static int ENDTIEMLONG = 30;
    public final static int ENDTIEMSHORT = 10;
}
