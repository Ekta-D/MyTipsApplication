package com.mytips.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mytips.ActiveProfiles;
import com.mytips.Model.Profiles;
import com.mytips.Model.TipeeInfo;
import com.mytips.SettingsActivity;

import java.util.ArrayList;

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
            , String tipees
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
        contentValues.put(DatabaseUtils.ProfileID, profile_id);
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

    public void updateProfileValues(String profile_id, String profile_name, boolean isSupervisor, boolean isTournamentTips,
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
        try {
            db.update(DatabaseUtils.PROFILE_TABLE, contentValues, DatabaseUtils.ProfileID + " =? ", new String[]{profile_id});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete_profile(String profile_id) {

        try {
            db.delete(DatabaseUtils.PROFILE_TABLE, DatabaseUtils.ProfileID + "=?", new String[]{profile_id});
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

        return tippess_infolist;


    }

    public  ArrayList<Profiles> fetchAllProfile(Context context) {
        ArrayList<Profiles> profilesList;
        Cursor cursor = null;
        String[] projections = new String[11];
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
                    profilesList.add(profiles);
                }
                while (cursor.moveToNext());
            }

        }
        return profilesList;
    }
}
