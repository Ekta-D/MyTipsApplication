package com.mytips.Interface;

import java.util.Calendar;

/**
 * Created by Beesolver on 10-10-2017.
 */

public interface TimeChangeListener {

    void OnTimeChange(String start_time, String end_time, Calendar start_date, Calendar end_date);
}
