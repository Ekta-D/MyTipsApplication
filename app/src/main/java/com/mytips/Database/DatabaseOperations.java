package com.mytips.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mytips.ActiveProfiles;
import com.mytips.Model.AddDay;
import com.mytips.Model.CountList;
import com.mytips.Model.Profiles;
import com.mytips.Model.TipeeInfo;
import com.mytips.SettingsActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Beesolver on 05-10-2017.
 */

public class DatabaseOperations {

    private SQLiteDatabase db;

    public DatabaseOperations() {
        // TODO Auto-generated constructor stub

    }

    public DatabaseOperations(Context context) {
        // TODO Auto-generated constructor stub
        db = DatabaseController.getTipseeDb(context);
    }

    public void insertProfileInfoIntoDatabase(String profile_id, String profile_name, boolean isSupervisor, boolean isTournamentTips,
                                              boolean isGetTips, String payPeriod, String startDay, double hourPay, String holidayPay
            , String tipees, String profile_pic, int profile_color
    ) {

        int supervisor = 0, tournamentTips = 0, getTips = 0;
        if (isSupervisor) {
            supervisor = 1;
        }
        if (isTournamentTips) {
            tournamentTips = 1;
        }
        if (isGetTips) {
            getTips = 1;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseUtils.ProfileName, profile_name);
        contentValues.put(DatabaseUtils.IsSupervisor, supervisor);
        contentValues.put(DatabaseUtils.GetTournamentTip, tournamentTips);
        contentValues.put(DatabaseUtils.GetTips, getTips);
        contentValues.put(DatabaseUtils.PayPeriod, payPeriod);
        contentValues.put(DatabaseUtils.StartDayWeek, startDay);
        contentValues.put(DatabaseUtils.HourlyPay, hourPay);
        contentValues.put(DatabaseUtils.HolidayPay, holidayPay);
        contentValues.put(DatabaseUtils.Tipees, tipees);
        contentValues.put(DatabaseUtils.IsActive, 1);
        contentValues.put(DatabaseUtils.ProfileColor, profile_color);
        contentValues.put(DatabaseUtils.ProfilePic, profile_pic);
        try {
            db.insert(DatabaseUtils.PROFILE_TABLE, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Cursor dataFetch(String tableName, String[] projection,
                            String selection, String[] select, String order) {
        Cursor cursor = null;
        try {
            cursor = db.query(tableName, projection, selection, select, null,
                    null, null);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return cursor;
    }

    public void updateProfileValues(int id, String profile_id, String profile_name, boolean isSupervisor, boolean isTournamentTips,
                                    boolean isGetTips, String payPeriod, String startDay, double hourPay, String holidayPay
            , String tipees, String profileImage, int profile_color) {

        int supervisor = 0, tournamentTips = 0, getTips = 0;
        if (isSupervisor) {
            supervisor = 1;
        }
        if (isTournamentTips) {
            tournamentTips = 1;
        }
        if (isGetTips) {
            getTips = 1;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseUtils.ProfileName, profile_name);
        contentValues.put(DatabaseUtils.IsSupervisor, supervisor);
        contentValues.put(DatabaseUtils.GetTournamentTip, tournamentTips);
        contentValues.put(DatabaseUtils.GetTips, getTips);
        contentValues.put(DatabaseUtils.PayPeriod, payPeriod);
        contentValues.put(DatabaseUtils.StartDayWeek, startDay);
        contentValues.put(DatabaseUtils.HourlyPay, hourPay);
        contentValues.put(DatabaseUtils.Tipees, tipees);
        contentValues.put(DatabaseUtils.ProfileColor, profile_color);
        contentValues.put(DatabaseUtils.HolidayPay, holidayPay);
        contentValues.put(DatabaseUtils.ProfilePic, profileImage);
        try {
            int changedRecord = db.update(DatabaseUtils.PROFILE_TABLE, contentValues, DatabaseUtils.Profile_ID + " =? ", new String[]{String.valueOf(id)});
            System.out.println("updated" + changedRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete_profile(String profile_id) {

        try {
            db.delete(DatabaseUtils.PROFILE_TABLE, DatabaseUtils.Profile_ID + "=?", new String[]{profile_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete_add_day(String add_id) {
        try {
            db.delete(DatabaseUtils.ADD_DAY_TABLE, DatabaseUtils.Add_ID + "=?", new String[]{add_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDeactivate(String profile_id) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseUtils.IsActive, 0);
            long val = db.update(DatabaseUtils.PROFILE_TABLE, contentValues, DatabaseUtils.Profile_ID + " =? ", new String[]{profile_id});

            Log.i("updated", String.valueOf(val));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertTipeeInfo(String tipee_id, String tipee_name, double tipee_out) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseUtils.TipeeID, tipee_id);
        contentValues.put(DatabaseUtils.TipeeName, tipee_name);
        contentValues.put(DatabaseUtils.TipeeOut, tipee_out);
        contentValues.put(DatabaseUtils.IsDeleted, 0);
        contentValues.put(DatabaseUtils.IsCheckedInAddDay, 0);
        try {
            db.insert(DatabaseUtils.TIPEE_TABLE, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateTipeeInfo(String tipee_id, String name, String tip_out) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseUtils.TipeeName, name);
        contentValues.put(DatabaseUtils.TipeeOut, tip_out);
        try {
            db.update(DatabaseUtils.TIPEE_TABLE, contentValues, DatabaseUtils.TipeeID + " =? ", new String[]{tipee_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteTipee(String tipee_id) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseUtils.IsDeleted, 1);
            db.update(DatabaseUtils.TIPEE_TABLE, contentValues, DatabaseUtils.TipeeID + "=?", new String[]{tipee_id});
            // db.delete(DatabaseUtils.TIPEE_TABLE, DatabaseUtils.TipeeID + "=?", new String[]{tipee_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<TipeeInfo> fetchTipeeList(Context context) {
        ArrayList<TipeeInfo> tippess_infolist;
        tippess_infolist = new ArrayList<>();
        Cursor cursor = null;
        String[] projections = new String[5];
        projections[0] = DatabaseUtils.TipeeID;
        projections[1] = DatabaseUtils.TipeeName;
        projections[2] = DatabaseUtils.TipeeOut;
        projections[3] = DatabaseUtils.IsDeleted;
        projections[4] = DatabaseUtils.IsCheckedInAddDay;
        try {
            cursor = new DatabaseOperations(context).dataFetch(DatabaseUtils.TIPEE_TABLE, projections,
                    null, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (cursor != null) {
            int cursor_count = cursor.getCount();
            if (cursor_count != 0) {
                if (cursor.moveToFirst()) {
                    do {
                        TipeeInfo tipeeInfo = new TipeeInfo();
                        tipeeInfo.setId(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipeeID)));
                        tipeeInfo.setName(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipeeName)));
                        tipeeInfo.setPercentage(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipeeOut)));
                        int del = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsDeleted));
                        boolean deleted = false;
                        if (del == 1) {
                            deleted = true;
                        }
                        int check = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsCheckedInAddDay));
                        if (check == 1) {
                            tipeeInfo.setIs_checkedInAddDay(true);
                        } else {
                            tipeeInfo.setIs_checkedInAddDay(false);
                        }
                        tipeeInfo.setIs_deleted(deleted);
                        tippess_infolist.add(tipeeInfo);
                    } while (cursor.moveToNext());
                }
            }
        }

        return tippess_infolist;


    }

    public ArrayList<Profiles> fetchAllProfile(Context context) {
        ArrayList<Profiles> profilesList;
        Cursor cursor = null;
        String[] projections = new String[14];
        projections[0] = DatabaseUtils.ProfileID;
        projections[1] = DatabaseUtils.ProfileName;
        projections[2] = DatabaseUtils.IsSupervisor;
        projections[3] = DatabaseUtils.IsActive;
        projections[4] = DatabaseUtils.GetTournamentTip;
        projections[5] = DatabaseUtils.GetTips;
        projections[6] = DatabaseUtils.PayPeriod;
        projections[7] = DatabaseUtils.StartDayWeek;
        projections[8] = DatabaseUtils.HourlyPay;
        projections[9] = DatabaseUtils.HolidayPay;
        projections[10] = DatabaseUtils.Tipees;
        projections[11] = DatabaseUtils.Profile_ID;
        projections[12] = DatabaseUtils.ProfilePic;
        projections[13] = DatabaseUtils.ProfileColor;
//        String selection[] = new String[]{"1"};
        try {
            // only active profiles will show

            String query = "select * from  profile where is_active = 1 ";
            cursor = db.rawQuery(query, null);
    /*        cursor = new DatabaseOperations(context).dataFetch(DatabaseUtils.PROFILE_TABLE, projections,
                    null, null, null);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;
        try {
            cursor_count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        profilesList = new ArrayList<>();
        if (cursor_count != 0) {

            if (cursor.moveToFirst()) {
                do {
                    Profiles profiles = new Profiles();
                    String id = cursor.getString(cursor.getColumnIndex(DatabaseUtils.Profile_ID));
                    String profileName = cursor.getString(cursor.getColumnIndex(DatabaseUtils.ProfileName));
                    String payPeriod = cursor.getString(cursor.getColumnIndex(DatabaseUtils.PayPeriod));
                    String hourlypay = cursor.getString(cursor.getColumnIndex(DatabaseUtils.HourlyPay));
                    String profileId = cursor.getString(cursor.getColumnIndex(DatabaseUtils.ProfileID));
                    int isActive = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsActive));
                    int is_supervisor = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsSupervisor));
                    int get_tournament = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GetTournamentTip));
                    int get_tips = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GetTips));
                    String startday = cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartDayWeek));
                    String holodayPay = cursor.getString(cursor.getColumnIndex(DatabaseUtils.HolidayPay));
                    String tipees = cursor.getString(cursor.getColumnIndex(DatabaseUtils.Tipees));

                    profiles.setId(Integer.parseInt(id));
                    profiles.setIs_supervisor(is_supervisor);
                    profiles.setStartday(startday);
                    profiles.setGet_tips(get_tips);
                    profiles.setGet_tournamenttip(get_tournament);
                    profiles.setHoliday_pay(holodayPay);
                    profiles.setProfile_id(profileId);
                    profiles.setProfile_name(profileName);
                    profiles.setPay_period(payPeriod);
                    profiles.setHourly_pay(hourlypay);
                    profiles.setIs_active(isActive);
                    profiles.setTipees_name(tipees);
                    profiles.setProfile_pic(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ProfilePic)));
                    profiles.setProfile_color(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.ProfileColor)));
                    profilesList.add(profiles);
                }
                while (cursor.moveToNext());
            }

        }
        return profilesList;
    }

    public void insertAddDayInfo(String profile_name, int profile_id, String start_shift, long check_in,
                                 String end_shift, long check_out, String auto_calculatedhour, int holiday_pay,
                                 String total_tips, String tipees, String tip_out_percentage, String total_tip_out,
                                 String tournament_count, String tounament_perday, String tournament_total, int isDay_off,
                                 String wages_hourly, String earns, int getting_tips, int getting_tournaments, String start_day_week
            , long start_long, long end_long, int is_dollar_checked, String manual_tips_dollar
            , int profile_color, int end_day, int calculatedTotalHr, int calculatedMins,
                                 String profile_wage_hourly) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseUtils.Profile, profile_name);
        contentValues.put(DatabaseUtils.SelectedProfile_ID, profile_id);
        contentValues.put(DatabaseUtils.CalculatedHours, auto_calculatedhour);
        contentValues.put(DatabaseUtils.isHolidayPay, holiday_pay);
        contentValues.put(DatabaseUtils.IsDayOff, isDay_off);
        contentValues.put(DatabaseUtils.TotalTips, total_tips);
        contentValues.put(DatabaseUtils.TipOutTipees, tipees);
        contentValues.put(DatabaseUtils.TournamentCount, tournament_count);
        contentValues.put(DatabaseUtils.TournamentPerDay, tounament_perday);
        contentValues.put(DatabaseUtils.TipOutPercentage, tip_out_percentage);
        contentValues.put(DatabaseUtils.TotaTipOut, total_tip_out);
        contentValues.put(DatabaseUtils.TounamentDowns, tournament_total);
        contentValues.put(DatabaseUtils.StartShift, start_shift);
        contentValues.put(DatabaseUtils.EndShift, end_shift);
        contentValues.put(DatabaseUtils.ClockIn, check_in);
        contentValues.put(DatabaseUtils.ClockOut, check_out);
        contentValues.put(DatabaseUtils.WagesPerHour, wages_hourly);
        contentValues.put(DatabaseUtils.TotalEarnings, earns);
        contentValues.put(DatabaseUtils.GettingTips, getting_tips);
        contentValues.put(DatabaseUtils.GettingTournamentDown, getting_tournaments);
        contentValues.put(DatabaseUtils.StartDayWeek, start_day_week);
        contentValues.put(DatabaseUtils.StartShiftLong, start_long);
        contentValues.put(DatabaseUtils.EndShiftLong, end_long);
        contentValues.put(DatabaseUtils.TipeeDollarChecked, is_dollar_checked);
        contentValues.put(DatabaseUtils.ManualTips, manual_tips_dollar);
        contentValues.put(DatabaseUtils.SelectedProfileColor, profile_color);
        contentValues.put(DatabaseUtils.isEndDay, end_day);
        contentValues.put(DatabaseUtils.TotalHr, calculatedTotalHr);
        contentValues.put(DatabaseUtils.PerHourProfileWage, profile_wage_hourly);
        contentValues.put(DatabaseUtils.TotalMin, calculatedMins);

        try {
            db.insert(DatabaseUtils.ADD_DAY_TABLE, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<AddDay> fetchAddDayDetails(Context context) {
        AddDay addDay;
        Cursor cursor = null;
        ArrayList<AddDay> addDayArrayList = new ArrayList<>();
        String[] projections = new String[30];
        projections[0] = DatabaseUtils.Profile;
        projections[1] = DatabaseUtils.CalculatedHours;
        projections[2] = DatabaseUtils.isHolidayPay;
        projections[3] = DatabaseUtils.IsDayOff;
        projections[4] = DatabaseUtils.TotalTips;
        projections[5] = DatabaseUtils.TipOutTipees;
        projections[6] = DatabaseUtils.TournamentCount;
        projections[7] = DatabaseUtils.TournamentPerDay;
        projections[8] = DatabaseUtils.TipOutPercentage;
        projections[9] = DatabaseUtils.TotaTipOut;
        projections[10] = DatabaseUtils.TounamentDowns;
        projections[11] = DatabaseUtils.StartShift;
        projections[12] = DatabaseUtils.ClockIn;
        projections[13] = DatabaseUtils.EndShift;
        projections[14] = DatabaseUtils.ClockOut;
        projections[15] = DatabaseUtils.Add_ID;
        projections[16] = DatabaseUtils.WagesPerHour;
        projections[17] = DatabaseUtils.TotalEarnings;
        projections[18] = DatabaseUtils.GettingTips;
        projections[19] = DatabaseUtils.GettingTournamentDown;
        projections[20] = DatabaseUtils.StartDayWeek;
        projections[21] = DatabaseUtils.StartShiftLong;
        projections[22] = DatabaseUtils.EndShiftLong;
        projections[23] = DatabaseUtils.TipeeDollarChecked;
        projections[24] = DatabaseUtils.ManualTips;
        projections[25] = DatabaseUtils.SelectedProfileColor;
        projections[26] = DatabaseUtils.isEndDay;
        projections[27] = DatabaseUtils.TotalHr;
        projections[28] = DatabaseUtils.TotalMin;
        projections[29] = DatabaseUtils.PerHourProfileWage;
        try {
            cursor = new DatabaseOperations(context).dataFetch(DatabaseUtils.ADD_DAY_TABLE, projections,
                    null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        int cursor_count = 0;

        if (cursor != null)
            cursor_count = cursor.getCount();

        if (cursor_count != 0) {
            if (cursor.moveToFirst()) {
                do {
                    addDay = new AddDay();
                    addDay.setId(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Add_ID)));
                    addDay.setProfile(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Profile)));
                    addDay.setCalculated_hours(cursor.getString(cursor.getColumnIndex(DatabaseUtils.CalculatedHours)));
                    addDay.setIsHolidaypay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isHolidayPay)));
                    addDay.setDay_off(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsDayOff)));
                    addDay.setTotal_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalTips)));
                    addDay.setTip_out_tipees(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutTipees)));
                    addDay.setTounament_count(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount)));
                    addDay.setTournament_perday(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentPerDay)));
                    addDay.setTip_out_percentage(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutPercentage)));
                    addDay.setTip_out(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotaTipOut)));
                    addDay.setTotal_tournament_downs(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TounamentDowns)));
                    addDay.setStart_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartShift)));
                    addDay.setCheck_in(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockIn)));
                    addDay.setCheck_out(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockOut)));
                    addDay.setEnd_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.EndShift)));
                    addDay.setWages_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.WagesPerHour)));
                    addDay.setTotal_earnings(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalEarnings)));
                    addDay.setGetting_tips(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTips)));
                    addDay.setGettingg_tournamnts(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTournamentDown)));
                    addDay.setStart_day_week(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartDayWeek)));
                    addDay.setStart_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.StartShiftLong)));
                    addDay.setEnd_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.EndShiftLong)));
                    addDay.setDollar_checked(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TipeeDollarChecked)));
                    addDay.setManual_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ManualTips)));
                    addDay.setProfile_color(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.SelectedProfileColor)));
                    addDay.setIsEndPay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isEndDay)));
                    addDay.setTotal_hours(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalHr)));
                    addDay.setTotal_minutes(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalMin)));
                    addDay.setProfile_wage_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.PerHourProfileWage)));
                    addDayArrayList.add(addDay);
                }
                while (cursor.moveToNext());
            }
        }
        return addDayArrayList;
    }

    public void updateAddDayInfo(String id, String profile_name, String start_shift, long check_in,
                                 String end_shift, long check_out, String auto_calculatedhour, int holiday_pay,
                                 String total_tips, String tipees, String tip_out_percentage, String total_tip_out,
                                 String tournament_count, String tounament_perday, String tournament_total, int isDay_off,
                                 String wages_hourly, String earns, int getting_tips, int getting_tournaments, String start_day_week
            , long start_long, long end_long, int is_dollar_checked, String manual_tips_dollar,
                                 int profile_color, int end_day
            , int calculatedTotalHr, int calculatedMins,
                                 String profile_wage_hourly) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseUtils.Profile, profile_name);
        contentValues.put(DatabaseUtils.CalculatedHours, auto_calculatedhour);
        contentValues.put(DatabaseUtils.isHolidayPay, holiday_pay);
        contentValues.put(DatabaseUtils.IsDayOff, isDay_off);
        contentValues.put(DatabaseUtils.TotalTips, total_tips);
        contentValues.put(DatabaseUtils.TipOutTipees, tipees);
        contentValues.put(DatabaseUtils.TournamentCount, tournament_count);
        contentValues.put(DatabaseUtils.TournamentPerDay, tounament_perday);
        contentValues.put(DatabaseUtils.TipOutPercentage, tip_out_percentage);
        contentValues.put(DatabaseUtils.TotaTipOut, total_tip_out);
        contentValues.put(DatabaseUtils.TounamentDowns, tournament_total);
        contentValues.put(DatabaseUtils.StartShift, start_shift);
        contentValues.put(DatabaseUtils.EndShift, end_shift);
        contentValues.put(DatabaseUtils.ClockIn, check_in);
        contentValues.put(DatabaseUtils.ClockOut, check_out);
        contentValues.put(DatabaseUtils.WagesPerHour, wages_hourly);
        contentValues.put(DatabaseUtils.TotalEarnings, earns);
        contentValues.put(DatabaseUtils.GettingTips, getting_tips);
        contentValues.put(DatabaseUtils.GettingTournamentDown, getting_tournaments);
        contentValues.put(DatabaseUtils.StartDayWeek, start_day_week);
        contentValues.put(DatabaseUtils.StartShiftLong, start_long);
        contentValues.put(DatabaseUtils.EndShiftLong, end_long);
        contentValues.put(DatabaseUtils.TipeeDollarChecked, is_dollar_checked);
        contentValues.put(DatabaseUtils.ManualTips, manual_tips_dollar);
        contentValues.put(DatabaseUtils.SelectedProfileColor, profile_color);
        contentValues.put(DatabaseUtils.isEndDay, end_day);
        contentValues.put(DatabaseUtils.TotalHr, calculatedTotalHr);
        contentValues.put(DatabaseUtils.PerHourProfileWage, profile_wage_hourly);
        contentValues.put(DatabaseUtils.TotalMin, calculatedMins);

        try {
            int changedRecord = db.update(DatabaseUtils.ADD_DAY_TABLE, contentValues, DatabaseUtils.Add_ID + " =? ", new String[]{String.valueOf(id)});
            System.out.println("updated_add" + changedRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public ArrayList<AddDay> fetchDataBetweenDates(long resetfrom, long resetTo, String profile, int profileID) {
        ArrayList<AddDay> fetched_data = new ArrayList<>();

        AddDay addDay = null;
        Cursor cursor = null;
        String query = "";
        if (profile.equalsIgnoreCase("All")) {
            query = "select * from  add_table where start_shift_long >= '" + resetfrom + "' AND   start_shift_long<='" + resetTo + "'  Order By start_shift_long";
        } else {
            query = "select * from  add_table where start_shift_long >= '" + resetfrom + "' AND   start_shift_long<='" + resetTo + "' AND  id= '" + profileID + "'  Order By start_shift_long";
        }

        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;

        if (cursor != null) {
            cursor_count = cursor.getCount();

            if (cursor_count != 0) {
                if (cursor.moveToFirst()) {
                    do {
                        addDay = new AddDay();
                        addDay.setId(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Add_ID)));
                        addDay.setProfile(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Profile)));
                        addDay.setCalculated_hours(cursor.getString(cursor.getColumnIndex(DatabaseUtils.CalculatedHours)));
                        addDay.setIsHolidaypay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isHolidayPay)));
                        addDay.setDay_off(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsDayOff)));
                        addDay.setTotal_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalTips)));
                        addDay.setTip_out_tipees(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutTipees)));
                        addDay.setTounament_count(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount)));
                        addDay.setTournament_perday(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentPerDay)));
                        addDay.setTip_out_percentage(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutPercentage)));
                        addDay.setTip_out(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotaTipOut)));
                        addDay.setTotal_tournament_downs(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TounamentDowns)));
                        addDay.setStart_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartShift)));
                        addDay.setCheck_in(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockIn)));
                        addDay.setCheck_out(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockOut)));
                        addDay.setEnd_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.EndShift)));
                        addDay.setWages_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.WagesPerHour)));
                        addDay.setTotal_earnings(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalEarnings)));
                        addDay.setGetting_tips(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTips)));
                        addDay.setGettingg_tournamnts(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTournamentDown)));
                        addDay.setStart_day_week(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartDayWeek)));
                        addDay.setStart_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.StartShiftLong)));
                        addDay.setEnd_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.EndShiftLong)));
                        addDay.setDollar_checked(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TipeeDollarChecked)));
                        addDay.setManual_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ManualTips)));
                        addDay.setProfile_color(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.SelectedProfileColor)));
                        addDay.setIsEndPay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isEndDay)));
                        addDay.setTotal_hours(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalHr)));
                        addDay.setTotal_minutes(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalMin)));
                        addDay.setProfile_wage_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.PerHourProfileWage)));
                        fetched_data.add(addDay);
                    }
                    while (cursor.moveToNext());
                }
            }
        }


        return fetched_data;

    }

    public ArrayList<AddDay> fetchDailyData(String profile, int profileID) {
        ArrayList<AddDay> daily_data = new ArrayList<>();
        AddDay addDay = null;
        Cursor cursor = null;
        String query = "";
        if (profile.equalsIgnoreCase("All")) {
            query = "select * from  add_table  Order By start_shift_long";
        } else {
            query = "select * from  add_table where  id=  '" + profileID + "'  Order By start_shift_long";
        }

        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;

        if (cursor != null) {
            cursor_count = cursor.getCount();

            if (cursor_count != 0) {
                if (cursor.moveToFirst()) {
                    do {

                        addDay = new AddDay();
                        addDay.setId(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Add_ID)));
                        addDay.setProfile(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Profile)));
                        addDay.setCalculated_hours(cursor.getString(cursor.getColumnIndex(DatabaseUtils.CalculatedHours)));
                        addDay.setIsHolidaypay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isHolidayPay)));
                        addDay.setDay_off(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsDayOff)));
                        addDay.setTotal_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalTips)));
                        addDay.setTip_out_tipees(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutTipees)));
                        addDay.setTounament_count(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount)));
                        addDay.setTournament_perday(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentPerDay)));
                        addDay.setTip_out_percentage(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutPercentage)));
                        addDay.setTip_out(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotaTipOut)));
                        addDay.setTotal_tournament_downs(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TounamentDowns)));
                        addDay.setStart_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartShift)));
                        addDay.setCheck_in(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockIn)));
                        addDay.setCheck_out(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockOut)));
                        addDay.setEnd_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.EndShift)));
                        addDay.setWages_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.WagesPerHour)));
                        addDay.setTotal_earnings(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalEarnings)));
                        addDay.setGetting_tips(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTips)));
                        addDay.setGettingg_tournamnts(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTournamentDown)));
                        addDay.setStart_day_week(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartDayWeek)));
                        addDay.setStart_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.StartShiftLong)));
                        addDay.setEnd_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.EndShiftLong)));
                        addDay.setDollar_checked(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TipeeDollarChecked)));
                        addDay.setManual_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ManualTips)));
                        addDay.setProfile_color(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.SelectedProfileColor)));
                        addDay.setIsEndPay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isEndDay)));
                        addDay.setTotal_hours(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalHr)));
                        addDay.setTotal_minutes(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalMin)));
                        addDay.setProfile_wage_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.PerHourProfileWage)));
                        daily_data.add(addDay);
                    }
                    while (cursor.moveToNext());
                }
            }
        }
        return daily_data;
    }

    //    public ArrayList<AddDay> yearlyData(String year, String profile)
    public ArrayList<AddDay> yearlyData(String year, String profile, int profileId) {
        ArrayList<AddDay> fetched_data = new ArrayList<>();
        AddDay addDay = null;
        Cursor cursor = null;
        String query = "";
        if (profile.equalsIgnoreCase("All")) {
            query = "select * from add_table WHERE start_shift LIKE  '%" + year + "%' Order By start_shift_long";
        } else {
            query = "select * from add_table WHERE start_shift LIKE  '%" + year + "%'  AND  id= '" + profileId + "' Order By start_shift_long";
        }

        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;

        if (cursor != null) {
            cursor_count = cursor.getCount();

            if (cursor_count != 0) {
                if (cursor.moveToFirst()) {
                    do {

                        addDay = new AddDay();
                        addDay.setId(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Add_ID)));
                        addDay.setProfile(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Profile)));
                        addDay.setCalculated_hours(cursor.getString(cursor.getColumnIndex(DatabaseUtils.CalculatedHours)));
                        addDay.setIsHolidaypay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isHolidayPay)));
                        addDay.setDay_off(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsDayOff)));
                        addDay.setTotal_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalTips)));
                        addDay.setTip_out_tipees(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutTipees)));
                        addDay.setTounament_count(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount)));
                        addDay.setTournament_perday(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentPerDay)));
                        addDay.setTip_out_percentage(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutPercentage)));
                        addDay.setTip_out(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotaTipOut)));
                        addDay.setTotal_tournament_downs(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TounamentDowns)));
                        addDay.setStart_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartShift)));
                        addDay.setCheck_in(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockIn)));
                        addDay.setCheck_out(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockOut)));
                        addDay.setEnd_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.EndShift)));
                        addDay.setWages_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.WagesPerHour)));
                        addDay.setTotal_earnings(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalEarnings)));
                        addDay.setGetting_tips(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTips)));
                        addDay.setGettingg_tournamnts(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTournamentDown)));
                        addDay.setStart_day_week(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartDayWeek)));
                        addDay.setStart_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.StartShiftLong)));
                        addDay.setEnd_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.EndShiftLong)));
                        addDay.setDollar_checked(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TipeeDollarChecked)));
                        addDay.setManual_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ManualTips)));
                        addDay.setProfile_color(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.SelectedProfileColor)));
                        addDay.setIsEndPay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isEndDay)));
                        addDay.setTotal_hours(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalHr)));
                        addDay.setTotal_minutes(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalMin)));
                        addDay.setProfile_wage_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.PerHourProfileWage)));
                        fetched_data.add(addDay);
                    }
                    while (cursor.moveToNext());
                }
            }
        }
        return fetched_data;
    }

    public ArrayList<CountList> tournamentCountPerDay(long reset_from, long reset_to, String profile) {

        ArrayList<CountList> counts = new ArrayList<>();
        String string = "";
        Cursor cursor = null;
        CountList countList;
        String query = "select * from  add_table where start_shift_long >= '" + reset_from + "' AND   start_shift_long<='" + reset_to + "'   AND  profile= '" + profile + "' ";
        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;

        if (cursor != null) {
            cursor_count = cursor.getCount();
            if (cursor_count != 0) {

                if (cursor.moveToFirst()) {
                    do {
                     /*   string = cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount));
                        counts.add(string);*/
                        countList = new CountList();
                        string = cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount));
                        if (string.equalsIgnoreCase("")) {
                            string = "0";
                        }
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        countList.setCount(string);
                        countList.setId(id);
                        counts.add(countList);
                    } while (cursor.moveToNext());
                }

            }
        }

        return counts;

    }

    public ArrayList<CountList> dailyCounts(String date, String profile) {
        ArrayList<CountList> counts = new ArrayList<>();
        Cursor cursor = null;
        String string = "";
        CountList countList;
//        String query = "select * from  add_table where start_shift = '" + date + "'  AND  profile= '" + profile + "' ";
        String query = "select * from  add_table where end_shift = '" + date + "'  AND  profile= '" + profile + "' ";
        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;

        if (cursor != null) {
            cursor_count = cursor.getCount();
            if (cursor_count != 0) {

                if (cursor.moveToFirst()) {
                    do {
                        countList = new CountList();
                        string = cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount));
                        if (string.equalsIgnoreCase("")) {
                            string = "0";
                        }
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        countList.setCount(string);
                        countList.setId(id);
                        counts.add(countList);
                        //  counts.add(string);
                    } while (cursor.moveToNext());
                }

            }
        }

        return counts;
    }

    public String getProfileEarning(String profile_id) {
        String earns = "";

        Cursor cursor = null;

        String query = "select * from  add_table where profile_id = '" + profile_id + "'  ";
        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;

        if (cursor != null) {
            cursor_count = cursor.getCount();
            if (cursor_count != 0) {

                if (cursor.moveToFirst()) {
                    do {
                        earns = cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalEarnings));
                        if (earns.equalsIgnoreCase("")) {
                            earns = "0";
                        }

                    } while (cursor.moveToNext());
                }

            }
        }
        return earns;

    }

    public ArrayList<Profiles> fetchDeactiveProfiles() {
        ArrayList<Profiles> deactivated_profiles = new ArrayList<>();

        ArrayList<Profiles> profilesList;
        Cursor cursor = null;
        String[] projections = new String[14];
        projections[0] = DatabaseUtils.ProfileID;
        projections[1] = DatabaseUtils.ProfileName;
        projections[2] = DatabaseUtils.IsSupervisor;
        projections[3] = DatabaseUtils.IsActive;
        projections[4] = DatabaseUtils.GetTournamentTip;
        projections[5] = DatabaseUtils.GetTips;
        projections[6] = DatabaseUtils.PayPeriod;
        projections[7] = DatabaseUtils.StartDayWeek;
        projections[8] = DatabaseUtils.HourlyPay;
        projections[9] = DatabaseUtils.HolidayPay;
        projections[10] = DatabaseUtils.Tipees;
        projections[11] = DatabaseUtils.Profile_ID;
        projections[12] = DatabaseUtils.ProfilePic;
        projections[13] = DatabaseUtils.ProfileColor;
//        String selection[] = new String[]{"1"};
        try {
            // only active profiles will show

            String query = "select * from  profile where is_active = 0 ";
            cursor = db.rawQuery(query, null);
    /*        cursor = new DatabaseOperations(context).dataFetch(DatabaseUtils.PROFILE_TABLE, projections,
                    null, null, null);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;
        try {
            cursor_count = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        }
        profilesList = new ArrayList<>();
        if (cursor_count != 0) {

            if (cursor.moveToFirst()) {
                do {
                    Profiles profiles = new Profiles();
                    String id = cursor.getString(cursor.getColumnIndex(DatabaseUtils.Profile_ID));
                    String profileName = cursor.getString(cursor.getColumnIndex(DatabaseUtils.ProfileName));
                    String payPeriod = cursor.getString(cursor.getColumnIndex(DatabaseUtils.PayPeriod));
                    String hourlypay = cursor.getString(cursor.getColumnIndex(DatabaseUtils.HourlyPay));
                    String profileId = cursor.getString(cursor.getColumnIndex(DatabaseUtils.ProfileID));
                    int isActive = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsActive));
                    int is_supervisor = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsSupervisor));
                    int get_tournament = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GetTournamentTip));
                    int get_tips = cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GetTips));
                    String startday = cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartDayWeek));
                    String holodayPay = cursor.getString(cursor.getColumnIndex(DatabaseUtils.HolidayPay));
                    String tipees = cursor.getString(cursor.getColumnIndex(DatabaseUtils.Tipees));

                    profiles.setId(Integer.parseInt(id));
                    profiles.setIs_supervisor(is_supervisor);
                    profiles.setStartday(startday);
                    profiles.setGet_tips(get_tips);
                    profiles.setGet_tournamenttip(get_tournament);
                    profiles.setHoliday_pay(holodayPay);
                    profiles.setProfile_id(profileId);
                    profiles.setProfile_name(profileName);
                    profiles.setPay_period(payPeriod);
                    profiles.setHourly_pay(hourlypay);
                    profiles.setIs_active(isActive);
                    profiles.setTipees_name(tipees);
                    profiles.setProfile_pic(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ProfilePic)));
                    profiles.setProfile_color(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.ProfileColor)));
                    deactivated_profiles.add(profiles);
                }
                while (cursor.moveToNext());
            }

        }

        return deactivated_profiles;
    }

    public ArrayList<CountList> tournamentCountTillToday(long reset_to, String profile) {

        ArrayList<CountList> counts = new ArrayList<>();
        String string = "";
        Cursor cursor = null;
        CountList countList;
        String query = "select * from  add_table where  start_shift_long<='" + reset_to + "'   AND  profile= '" + profile + "' ";
        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;

        if (cursor != null) {
            cursor_count = cursor.getCount();
            if (cursor_count != 0) {

                if (cursor.moveToFirst()) {
                    do {
                     /*   string = cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount));
                        counts.add(string);*/
                        countList = new CountList();
                        string = cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount));
                        if (string.equalsIgnoreCase("")) {
                            string = "0";
                        }
                        int id = cursor.getInt(cursor.getColumnIndex("id"));
                        countList.setCount(string);
                        countList.setId(id);
                        counts.add(countList);
                    } while (cursor.moveToNext());
                }

            }
        }

        return counts;

    }

    public ArrayList<AddDay> fetchAddDayForProfile(int profileID) {
        ArrayList<AddDay> addDays = new ArrayList<>();
        AddDay addDay = null;
        Cursor cursor = null;
        String query = "";
        query = "select * from  add_table where  profile_id=  '" + profileID + "'  Order By start_shift_long";

        try {
            cursor = db.rawQuery(query, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int cursor_count = 0;

        if (cursor != null) {
            cursor_count = cursor.getCount();

            if (cursor_count != 0) {
                if (cursor.moveToFirst()) {
                    do {

                        addDay = new AddDay();
                        addDay.setId(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Add_ID)));
                        addDay.setProfile(cursor.getString(cursor.getColumnIndex(DatabaseUtils.Profile)));
                        addDay.setCalculated_hours(cursor.getString(cursor.getColumnIndex(DatabaseUtils.CalculatedHours)));
                        addDay.setIsHolidaypay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isHolidayPay)));
                        addDay.setDay_off(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.IsDayOff)));
                        addDay.setTotal_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalTips)));
                        addDay.setTip_out_tipees(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutTipees)));
                        addDay.setTounament_count(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentCount)));
                        addDay.setTournament_perday(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TournamentPerDay)));
                        addDay.setTip_out_percentage(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TipOutPercentage)));
                        addDay.setTip_out(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotaTipOut)));
                        addDay.setTotal_tournament_downs(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TounamentDowns)));
                        addDay.setStart_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartShift)));
                        addDay.setCheck_in(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockIn)));
                        addDay.setCheck_out(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.ClockOut)));
                        addDay.setEnd_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.EndShift)));
                        addDay.setWages_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.WagesPerHour)));
                        addDay.setTotal_earnings(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalEarnings)));
                        addDay.setGetting_tips(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTips)));
                        addDay.setGettingg_tournamnts(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTournamentDown)));
                        addDay.setStart_day_week(cursor.getString(cursor.getColumnIndex(DatabaseUtils.StartDayWeek)));
                        addDay.setStart_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.StartShiftLong)));
                        addDay.setEnd_long(cursor.getLong(cursor.getColumnIndex(DatabaseUtils.EndShiftLong)));
                        addDay.setDollar_checked(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TipeeDollarChecked)));
                        addDay.setManual_tips(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ManualTips)));
                        addDay.setProfile_color(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.SelectedProfileColor)));
                        addDay.setIsEndPay(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.isEndDay)));
                        addDay.setTotal_hours(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalHr)));
                        addDay.setTotal_minutes(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.TotalMin)));
                        addDay.setProfile_wage_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.PerHourProfileWage)));
                        addDays.add(addDay);
                    }
                    while (cursor.moveToNext());
                }
            }
        }
        return addDays;
    }


    public void updateProfileInfoInDays(ArrayList<AddDay> addDays, String profile_name, int profileID, boolean isGettingTournaments,
                                        boolean isGettingTips) {

        for (int i = 0; i < addDays.size(); i++) {
            AddDay addDay = addDays.get(i);

            addDay.setProfile(profile_name);

            if (isGettingTournaments) {
                addDay.setGettingg_tournamnts(1);
            } else {
                addDay.setGettingg_tournamnts(0);
            }
            if (isGettingTips) {
                addDay.setGetting_tips(1);
            } else {
                addDay.setGetting_tips(0);
            }


            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseUtils.SelectedProfile_ID, profileID);
            contentValues.put(DatabaseUtils.Profile, addDay.getProfile());
            contentValues.put(DatabaseUtils.GettingTournamentDown, addDay.getGettingg_tournamnts());
            contentValues.put(DatabaseUtils.GettingTips, addDay.getGetting_tips());

            try {
                int changedRecord = db.update(DatabaseUtils.ADD_DAY_TABLE, contentValues, DatabaseUtils.Add_ID + " =? ", new String[]{String.valueOf(addDay.getId())});
                System.out.println("updated_add" + changedRecord);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

}
