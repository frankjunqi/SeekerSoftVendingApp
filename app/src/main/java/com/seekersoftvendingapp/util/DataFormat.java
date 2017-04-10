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
        String outDate = getNowTime();
        System.out.print(outDate);
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

    /**
     * yyyy-MM-dd HH:mm:ss
     * Get Today Date
     *
     * @return
     */
    public static Date getTodayDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar c = Calendar.getInstance();
        String dateStr = format.format(c.getTime());
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * yyyy-MM-dd
     * Get Befor Today Date
     *
     * @return
     */
    public static Date getBeforTodayDate(int period) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar canlendar = Calendar.getInstance();
        canlendar.add(Calendar.DATE, -period);
        String dateStr = format.format(canlendar.getTime());
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * yyyy-MM-dd
     * Get Month Today Date
     *
     * @return
     */
    public static Date getBeforMonthDate(int period) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar canlendar = Calendar.getInstance();
        canlendar.add(Calendar.MONTH, -period);
        String dateStr = format.format(canlendar.getTime());
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * yyyy-MM-dd
     * Get Year Today Date
     *
     * @return
     */
    public static Date getBeforYearDate(int period) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar canlendar = Calendar.getInstance();
        canlendar.add(Calendar.YEAR, -period);
        String dateStr = format.format(canlendar.getTime());
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            return new Date();
        }
    }

    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
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

    /**
     * 消费本地判断
     *
     * @param period
     * @param unit
     * @return
     */
    public static Date periodUnitGetStartDate(int period, String unit) {
        switch (unit) {
            case "day":
                if (period <= 0) {
                    return getBeforTodayDate(0);
                } else {
                    return getBeforTodayDate(period - 1);
                }
            case "month":
                if (period <= 0) {
                    return getBeforMonthDate(0);
                } else {
                    return getBeforMonthDate(period - 1);
                }
            case "year":
                if (period <= 0) {
                    return getBeforYearDate(0);
                } else {
                    return getBeforYearDate(period - 1);
                }
        }
        return new Date();
    }
}
