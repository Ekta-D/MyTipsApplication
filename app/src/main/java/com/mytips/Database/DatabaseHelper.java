package com.mytips.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mytips.Interface.TipeeChecked;

/**
 * Created by Beesolver on 05-10-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String dbname = DatabaseUtils.db_Name;
    private static int version = DatabaseUtils.dbVersion;

    public DatabaseHelper(Context context) {
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(DatabaseUtils.CREATE_TABLE + DatabaseUtils.PROFILE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT,  "
                + DatabaseUtils.ProfileID + " INTEGER,  "
                + DatabaseUtils.ProfileName + " TEXT, "
                + DatabaseUtils.IsSupervisor + " INTEGER,  "
                + DatabaseUtils.IsActive + " INTEGER,  "
                + DatabaseUtils.GetTournamentTip +
                " INTEGER,  "
                + DatabaseUtils.GetTips + " INTEGER,  "
                + DatabaseUtils.PayPeriod + " TEXT, "
                + DatabaseUtils.StartDayWeek + " TEXT, "
                + DatabaseUtils.HourlyPay + " INTEGER,  "
                + DatabaseUtils.HolidayPay + " TEXT, "
                + DatabaseUtils.ProfilePic + " TEXT, "
                + DatabaseUtils.Tipees + " TEXT );"

        );
        db.execSQL(DatabaseUtils.CREATE_TABLE + DatabaseUtils.TIPEE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT,  " +
                DatabaseUtils.TipeeID + " INTEGER,  "
                + DatabaseUtils.TipeeName + " TEXT, "
                + DatabaseUtils.TipeeOut + " TEXT );");


        db.execSQL(DatabaseUtils.CREATE_TABLE + DatabaseUtils.ADD_DAY_TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DatabaseUtils.Profile + " TEXT, "
                + DatabaseUtils.CalculatedHours + " TEXT, "
                + DatabaseUtils.isHolidayPay + " INTEGER, "
                + DatabaseUtils.IsDayOff + " INTEGER, "
                + DatabaseUtils.TotalTips + " TEXT, "
                + DatabaseUtils.TipOutTipees + " TEXT, "
                + DatabaseUtils.TournamentCount + " TEXT, "
                + DatabaseUtils.TournamentPerDay + " TEXT, "
                + DatabaseUtils.TipOutPercentage + " INTEGER, "
                + DatabaseUtils.TotaTipOut + " TEXT, "
                + DatabaseUtils.TounamentDowns + " INTEGER, "

                + DatabaseUtils.GettingTips + " INTEGER, "
                + DatabaseUtils.GettingTournamentDown + " INTEGER, "

                + DatabaseUtils.TipeeDollarChecked + " INTEGER, "
                + DatabaseUtils.ManualTips + " TEXT, "

                + DatabaseUtils.StartShift + " TEXT, "
                //  + DatabaseUtils.ClockIn + " TEXT, "
                + DatabaseUtils.EndShift + " TEXT, "
                + DatabaseUtils.ClockIn + " REAL, "
                + DatabaseUtils.StartShiftLong + " REAL, "
                + DatabaseUtils.EndShiftLong + " REAL, "

                + DatabaseUtils.StartDayWeek + " TEXT, "

                + DatabaseUtils.WagesPerHour + " TEXT, "
                + DatabaseUtils.TotalEarnings + " TEXT, "
                + DatabaseUtils.ClockOut + " REAL " +
                //  + DatabaseUtils.ClockOut + " TEXT " +
                " );");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropIfTableExist(DatabaseUtils.PROFILE_TABLE, db);
        dropIfTableExist(DatabaseUtils.TIPEE_TABLE, db);
        dropIfTableExist(DatabaseUtils.ADD_DAY_TABLE, db);
    }

    public void dropIfTableExist(String table_name, SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS '" + table_name + "'");
    }
}
