package com.mytips.Utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Beesolver on 23-10-2017.
 */

public class CommonMethods {

    public static Date convertLongtoDate(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        Date d = (Date) c.getTime();
        return d;
    }

}
