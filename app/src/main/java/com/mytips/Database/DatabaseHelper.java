package com.mytips.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Beesolver on 05-10-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String dbname = DatabaseUtils.db_Name;
    private static int version = DatabaseUtils.dbVersion;
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, dbname, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        db.execSQL(DatabaseUtils.CREATE_TABLE + DatabaseUtils.PROFILE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT,  "
                + DatabaseUtils.ProfileID + " INTEGER,  "
                + DatabaseUtils.ProfileName + " TEXT, "
                + DatabaseUtils.IsSupervisor + " INTEGER,  "
                + DatabaseUtils.GetTournamentTip +
                " INTEGER,  "
                + DatabaseUtils.GetTips + " INTEGER,  "
                + DatabaseUtils.PayPeriod + " TEXT, "
                + DatabaseUtils.StartDayWeek + " TEXT, "
                + DatabaseUtils.Wages + " INTEGER,  "
                + DatabaseUtils.HourlyPay + " INTEGER,  "
                + DatabaseUtils.HolidayPay + " TEXT, "
                + DatabaseUtils.ProfilePic + " TEXT, "
                + DatabaseUtils.Tipees + " TEXT );"

        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        dropIfTableExist(DatabaseUtils.PROFILE_TABLE, db);
    }

    public void dropIfTableExist(String table_name, SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS '" + table_name + "'");

    }
}
