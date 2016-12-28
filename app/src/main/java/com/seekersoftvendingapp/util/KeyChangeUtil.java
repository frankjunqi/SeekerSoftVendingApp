package com.seekersoftvendingapp.util;

/**
 * Created by kjh08490 on 2016/12/28.
 */

public class KeyChangeUtil {

    public static String getFlagInt(String charOne) {
        String outInt = "";
        if (charOne.equals("A")) {
            outInt = "1";
        } else if (charOne.equals("B")) {
            outInt = "2";
        } else if (charOne.equals("C")) {
            outInt = "3";
        }
        return outInt;
    }
}
