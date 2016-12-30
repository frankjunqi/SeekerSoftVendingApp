package com.seekersoftvendingapp.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String getNumber(String str) {
        if (str == null || TextUtils.isEmpty(str)) {
            return "";
        }
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
