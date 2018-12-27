package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * Created by ionolab-DP on 2018/12/21.
 */
public class DateTimeUtil {

    public static final String STANDARD_FORMAT="yyyy-MM-dd HH:mm:ss";

    public static Date strToDate(String dataTimeStr){
        DateTimeFormatter dateTimeFormatter= DateTimeFormat.forPattern(STANDARD_FORMAT);
        DateTime dateTime=dateTimeFormatter.parseDateTime(dataTimeStr);
        return dateTime.toDate();
    }

    public static String dateToStr(Date date){
        if (date==null)
            return StringUtils.EMPTY;
        DateTime dateTime= new DateTime(date);
        return dateTime.toString(STANDARD_FORMAT);
    }

    public static void main(String[] args) {

        System.out.println(strToDate("2018-12-20 11:11:11"));
        System.out.println(dateToStr(new Date()));
    }
}
