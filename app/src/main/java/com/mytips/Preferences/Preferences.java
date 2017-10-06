package com.mytips.Preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mytips.Model.TipeeInfo;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Beesolver on 06-10-2017.
 */

public class Preferences {

    private static Preferences instance;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private Preferences(Context context) {

        prefs = context.getSharedPreferences("Pref",
                Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    public static synchronized Preferences getInstance(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }
        return instance;
    }

    public void save_list(String key, ArrayList<TipeeInfo> user_value) {
        Gson gson = new Gson();
        String all_users = gson.toJson(user_value);
        editor.putString(key, all_users);

        editor.commit();
    }

    public ArrayList<TipeeInfo> getTipeeList(String key) {
        Gson gson = new Gson();
        String users = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<TipeeInfo>>() {
        }.getType();
        ArrayList<TipeeInfo> all_users = gson.fromJson(users, type);
        return all_users;
    }
}
