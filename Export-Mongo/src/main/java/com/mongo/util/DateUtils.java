package com.mongo.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: bolao
 * Date: 2018/9/17 10:21
 * Version: V1.0
 * To change this template use File | Settings | File Templates.
 * Description:   时间的工具类
 */
public class DateUtils {

    /**
     * 将 long 的时间戳转换为 dateTime 的String
     *
     * @param longTime
     * @return
     */
    public static String longTimeToDateTimeString(long longTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date(longTime);
        return sdf.format(date);
    }

    /**
     * 将 long 的时间戳转换为 date的String
     *
     * @param longTime
     * @return
     */
    public static String longTimeToDateString(Long longTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date(longTime);
        return sdf.format(date);
    }

    public static void main(String[] args) {
        String time = DateUtils.longTimeToDateTimeString(1536915650000l);
        System.out.println(time);
    }


}
