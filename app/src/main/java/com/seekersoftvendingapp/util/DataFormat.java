package com.seekersoftvendingapp.util;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by kjh08490 on 2016/12/2.
 */

public class DataFormat {


    public static void main(String[] args) throws Exception {
        Date outDate = fromISODate("2016-11-19T09:02:43.972Z");
        System.out.print(outDate.toString());


//        Date outDate1 = formatDate("2016-11-19T09:02:43.972Z");
//        System.out.print(outDate1.toString());
    }

    /**
     * 获取日期 时分秒为0(不区分时区)
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Calendar getCalendar() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String dateString = format.format(c.getTime());
        try {
            c.setTime(format.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        return format.format(c.getTime());
    }

    /**
     * @param date
     * @return
     */
    public static String formatDatetime(Date date) {
        SimpleDateFormat FORMAT_STAND_DT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return FORMAT_STAND_DT.format(date);
    }

    public static Date formatDate(String date) {
        SimpleDateFormat FORMAT_STAND_DT = new SimpleDateFormat("yyyy-MM-dd");
        Date outDate = null;
        try {
            outDate = FORMAT_STAND_DT.parse(date);
        } catch (ParseException e) {
        }
        return outDate;
    }

    /**
     * @param calendar
     * @return
     */
    public static String formatDate(Calendar calendar) {
        SimpleDateFormat FORMAT_STAND_DT = new SimpleDateFormat("yyyy-MM-dd");
        return FORMAT_STAND_DT.format(calendar.getTime());
    }

    public static Date formatDatetime(String date) {
        SimpleDateFormat FORMAT_STAND_DT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date outDate = null;
        try {
            outDate = FORMAT_STAND_DT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outDate;
    }

    /**
     * mongo数据库的时间类型比java的时间更精确，无法直接转换
     *
     * @param time
     * @return
     */
    public static Date fromISODate(String time) {
        if (!time.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.\\d{3}Z")) {
            return null;
        }
        time = time.replaceFirst("T", " ").replaceFirst(".\\d{3}Z", "");
        Date date = formatDatetime(time);
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.HOUR_OF_DAY, 8);
        return ca.getTime();
    }
}
