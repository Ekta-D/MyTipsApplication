package com.mytips.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mytips.ActiveProfiles;
import com.mytips.Model.AddDay;
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
                                              boolean isGetTips, String payPeriod, String startDay, int hourPay, String holidayPay
            , String tipees, String profile_pic
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
                                    boolean isGetTips, String payPeriod, String startDay, int hourPay, String holidayPay
            , String tipees) {

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
        contentValues.put(DatabaseUtils.HolidayPay, holidayPay);
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
            db.update(DatabaseUtils.PROFILE_TABLE, contentValues, DatabaseUtils.ProfileID + " =? ", new String[]{profile_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertTipeeInfo(String tipee_id, String tipee_name, int tipee_out) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseUtils.TipeeID, tipee_id);
        contentValues.put(DatabaseUtils.TipeeName, tipee_name);
        contentValues.put(DatabaseUtils.TipeeOut, tipee_out);

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
            db.delete(DatabaseUtils.TIPEE_TABLE, DatabaseUtils.TipeeID + "=?", new String[]{tipee_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<TipeeInfo> fetchTipeeList(Context context) {
        ArrayList<TipeeInfo> tippess_infolist;
        tippess_infolist = new ArrayList<>();
        Cursor cursor = null;
        String[] projections = new String[3];
        projections[0] = DatabaseUtils.TipeeID;
        projections[1] = DatabaseUtils.TipeeName;
        projections[2] = DatabaseUtils.TipeeOut;
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
        String[] projections = new String[13];
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

//        String selection[] = new String[]{"1"};
        try {
            cursor = new DatabaseOperations(context).dataFetch(DatabaseUtils.PROFILE_TABLE, projections,
                    null, null, null);
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
                    profilesList.add(profiles);
                }
                while (cursor.moveToNext());
            }

        }
        return profilesList;
    }

    public void insertAddDayInfo(String profile_name, String start_shift, String check_in,
                                 String end_shift, String check_out, String auto_calculatedhour, int holiday_pay,
                                 String total_tips, String tipees, String tip_out_percentage, String total_tip_out,
                                 String tournament_count, String tounament_perday, String tournament_total, int isDay_off,
                                 String wages_hourly, String earns, int getting_tips, int getting_tournaments) {

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
        String[] projections = new String[20];
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
                    addDay.setCheck_in(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ClockIn)));
                    addDay.setCheck_out(cursor.getString(cursor.getColumnIndex(DatabaseUtils.ClockOut)));
                    addDay.setEnd_shift(cursor.getString(cursor.getColumnIndex(DatabaseUtils.EndShift)));
                    addDay.setWages_hourly(cursor.getString(cursor.getColumnIndex(DatabaseUtils.WagesPerHour)));
                    addDay.setTotal_earnings(cursor.getString(cursor.getColumnIndex(DatabaseUtils.TotalEarnings)));
                    addDay.setGetting_tips(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTips)));
                    addDay.setGettingg_tournamnts(cursor.getInt(cursor.getColumnIndex(DatabaseUtils.GettingTournamentDown)));
                    addDayArrayList.add(addDay);
                }
                while (cursor.moveToNext());
            }
        }
        return addDayArrayList;
    }

    public void updateAddDayInfo(String id, String profile_name, String start_shift, String check_in,
                                 String end_shift, String check_out, String auto_calculatedhour, int holiday_pay,
                                 String total_tips, String tipees, String tip_out_percentage, String total_tip_out,
                                 String tournament_count, String tounament_perday, String tournament_total, int isDay_off,
                                 String wages_hourly, String earns, int getting_tips, int getting_tournaments) {
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

        try {
            int changedRecord = db.update(DatabaseUtils.ADD_DAY_TABLE, contentValues, DatabaseUtils.Add_ID + " =? ", new String[]{String.valueOf(id)});
            System.out.println("updated_add" + changedRecord);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
