package com.task.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by blanke on 2017/4/23.
 */
public class DateUtils {
    public static String formatData(Date date){
        SimpleDateFormat format=new SimpleDateFormat("yy/MM/dd HH:mm");
        return format.format(date);
    }
}
