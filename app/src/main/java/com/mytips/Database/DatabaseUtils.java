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
    public static final String ADD_DAY_TABLE = "add_table";

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
    public static final String ProfileColor = "profile_color";


    //tipees fields
    public static final String TipeeID = "tipee_id";
    public static final String TipeeName = "tipee_name";
    public static final String TipeeOut = "tip_out";
    public static final String IsDeleted = "is_deleted";
    public static final String IsCheckedInAddDay = "is_checked";



    //add day fields
    public static final String SelectedProfile_ID = "profile_id";
    public static final String Add_ID = "id";
    public static final String Profile = "profile";
    public static final String TounamentDowns = "tournament_down";
    public static final String TournamentCount = "tournament_count";
    public static final String TournamentPerDay = "tournament_perday";
    public static final String TotalTips = "total_tips";
    public static final String TipOutPercentage = "tip_out_per";
    public static final String TotaTipOut = "total_tipout";
    public static final String TipOutTipees = "tip_out_tipees";
    public static final String CalculatedHours = "calculated_hours";
    public static final String isHolidayPay = "holiday_pay";
    public static final String isEndDay = "end_day";

    public static final String StartShift = "start_shift";
    public static final String EndShift = "end_shift";
    public static final String ClockIn = "clock_in";
    public static final String ClockOut = "clock_out";
    public static final String IsDayOff = "Is_day_off";
    public static final String WagesPerHour = "wages_hour";
    public static final String TotalEarnings = "total_earnings";
    public static final String GettingTips = "getting_tips";
    public static final String GettingTournamentDown = "getting_tournament_down";
    public static final String StartShiftLong = "start_shift_long";
    public static final String EndShiftLong = "end_shift_long";
    public static final String TipeeDollarChecked = "dollar_checked";
    public static final String ManualTips = "manual_tips";
    public static final String SelectedProfileColor = "selected_profile_color";
    public static final String TotalHr = "total_hours";
    public static final String TotalMin = "total_mins";
    public static final String PerHourProfileWage = "per_hour_wage";


}
