package com.mytips.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.view.Window;
import android.view.WindowManager;

import com.mytips.R;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Beesolver on 23-10-2017.
 */

public class CommonMethods {

    public static Date convertLongtoDate(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        Date d = (Date) c.getTime();
        return d;
    }

    public static void setTheme(ActionBar actionBar, Activity context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);
        if(sharedPreferences.getInt("selected_theme",0)==1){
            //default Black and white
            if(actionBar!=null) actionBar.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.colorPrimary)));
            setStatusAndNavigationBarColor(context, context.getResources().getColor(R.color.colorPrimaryDark), context.getResources().getColor(R.color.colorPrimary));
        } else if (sharedPreferences.getInt("selected_theme",0)==0){
            //Navy Blue and white
            if(actionBar!=null) actionBar.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.blue_primary)));
            setStatusAndNavigationBarColor(context, context.getResources().getColor(R.color.blue_primary_dark), context.getResources().getColor(R.color.blue_primary));
        } else if (sharedPreferences.getInt("selected_theme",0)==-1){
            //Orange and white
            if(actionBar!=null) actionBar.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.orange_primary)));
            setStatusAndNavigationBarColor(context, context.getResources().getColor(R.color.orange_primary_dark), context.getResources().getColor(R.color.orange_primary));
        }
    }

    private static void setStatusAndNavigationBarColor(Activity context, int argbStatusBar, int argbNavigation) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = context.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(argbStatusBar);
            window.setNavigationBarColor(argbNavigation);
        }
    }

}
