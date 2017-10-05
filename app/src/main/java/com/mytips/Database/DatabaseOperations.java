package com.mytips.Database;

import android.content.ContentValues;
import android.content.Context;
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

    public void insertProfileInfoIntoDatabase(String profile_name, boolean isSupervisor, boolean isTournamentTips,
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
        contentValues.put(DatabaseUtils.ProfileName, profile_name);
        contentValues.put(DatabaseUtils.IsSupervisor, supervisor);
        contentValues.put(DatabaseUtils.GetTournamentTip, tournamentTips);
        contentValues.put(DatabaseUtils.GetTips, getTips);
        contentValues.put(DatabaseUtils.PayPeriod, payPeriod);
        contentValues.put(DatabaseUtils.StartDayWeek, startDay);
        contentValues.put(DatabaseUtils.HourlyPay, hourPay);
        contentValues.put(DatabaseUtils.HolidayPay, holidayPay);
        contentValues.put(DatabaseUtils.Tipees, tipees);

        try {
            db.insert(DatabaseUtils.PROFILE_TABLE, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
