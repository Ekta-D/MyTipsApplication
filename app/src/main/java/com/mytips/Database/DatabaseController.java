package com.mytips.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

class DatabaseController {
    static SQLiteDatabase tipseeDb = null;

      static synchronized SQLiteDatabase getTipseeDb(Context context) {
       if (tipseeDb == null) {
           tipseeDb = new DatabaseHelper(context).getWritableDatabase();
       }
       return tipseeDb;
   }

}
