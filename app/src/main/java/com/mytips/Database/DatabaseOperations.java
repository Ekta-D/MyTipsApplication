package com.mytips.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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
}
