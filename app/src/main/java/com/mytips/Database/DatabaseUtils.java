package com.mytips.Database;

/**
 * Created by Beesolver on 05-10-2017.
 */

public class DatabaseUtils {

    public static final String db_Name = "tipseeDB";
    public static final int dbVersion = 1;
    public static final String CREATE_TABLE = "CREATE TABLE ";

    public static final String PROFILE_TABLE = "profile";
    public static final String TIPEE_TABLE = "tipees";

    // profile fields
    public static final String Profile_ID = "id";
    public static final String ProfileID = "profile_id";
    public static final String ProfileName = "profile_name";
    public static final String IsSupervisor = "supervisor";
    public static final String GetTournamentTip = "get_tournamentTips";
    public static final String GetTips = "getTips";
    public static final String PayPeriod = "pay_period";
    public static final String StartDayWeek = "start_day_week";

    public static final String HourlyPay = "hourly_pay";
    public static final String HolidayPay = "holiday_pay";
    public static final String Tipees = "tipees";
    public static final String ProfilePic = "profile_pic";
    public static final String IsActive = "is_active";


    //tipees fields
    public static final String TipeeID = "tipee_id";
    public static final String TipeeName = "tipee_name";
    public static final String TipeeOut = "tip_out";

}
