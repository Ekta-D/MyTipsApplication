package com.mytips.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jethin on 29-07-2016.
 */
public class DateUtils {

    public static Date resetFromDate(Date currentDate) {
        try {

            Calendar returnVal = Calendar.getInstance();
            returnVal.setTime(currentDate);
            returnVal.set(Calendar.HOUR_OF_DAY, 0);
            returnVal.set(Calendar.MINUTE, 0);
            returnVal.set(Calendar.SECOND, 0);
            returnVal.set(Calendar.MILLISECOND, 0);
            // String fromatedDate = this.getFormatedDateString(d,
            // "MMM dd, yyyy");
            Date fromDate = returnVal.getTime();
            return fromDate;

        } catch (Exception e) {
            return new Date();
        }

    }


    //    String select_foodlog = "select * from  FoodLogEntry where Date >= '" + from_date + "'AND  Date<='" + to_date + "'
    public static Date resetToDate(Date currentDate) {
        try {

            Calendar returnVal = Calendar.getInstance();
            returnVal.setTime(currentDate);
            returnVal.set(Calendar.HOUR_OF_DAY, 23);
            returnVal.set(Calendar.MINUTE, 59);
            returnVal.set(Calendar.SECOND, 59);
            returnVal.set(Calendar.MILLISECOND, 59);
            // String fromatedDate = this.getFormatedDateString(d,
            // "MMM dd, yyyy");
            Date toDate = returnVal.getTime();
            return toDate;

        } catch (Exception e) {
            return new Date();
        }

    }


    public static Date resetlastMonth(Date currentDate) {

        try {
            Calendar returnVal = Calendar.getInstance();
            returnVal.setTime(currentDate);
            returnVal.add(Calendar.DATE, -30);
            Date result = returnVal.getTime();
            return result;
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date resetLastweek(Date currentDate) {
        try {
            Calendar returnVal = Calendar.getInstance();
            returnVal.setTime(currentDate);
            returnVal.add(Calendar.DAY_OF_YEAR, -7);
            Date result = returnVal.getTime();
            return result;
        } catch (Exception e) {
            return new Date();
        }
    }

    public static Date resetBiWeekly(Date currentDate) {
        try {
            Calendar returnVal = Calendar.getInstance();
            returnVal.setTime(currentDate);
            returnVal.add(Calendar.DAY_OF_YEAR, -15);
            Date result = returnVal.getTime();
            return result;
        } catch (Exception e) {
            return new Date();
        }
    }


}

