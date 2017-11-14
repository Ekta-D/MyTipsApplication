package com.mytips;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends GoogleAuthorizationActivity/* implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener */ {


    public static final String ISFIRST_TIME = "Isfirst_time";
    SharedPreferences sharedPreferences;
    boolean isFirstTime;
    GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    SharedPreferences.Editor editor;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);
        if (sharedPreferences != null)
            isFirstTime = sharedPreferences.getBoolean(ISFIRST_TIME, false);

        CommonMethods.setTheme(getSupportActionBar(), SplashActivity.this);

        editor = sharedPreferences.edit();
        final EditText name = (EditText) findViewById(R.id.et_name);
        email = (EditText) findViewById(R.id.et_email);

        findViewById(R.id.button_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!name.getText().toString().trim().equals("") && !email.getText().toString().trim().equals("")) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                   editor.putString("user_name", name.getText().toString().trim());
                   editor.putString("user_email", email.getText().toString().trim());
                    editor.commit();
                    startActivity(new Intent(getBaseContext(), LandingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else if (name.getText().toString().trim().equals("")) {
                    name.setError("Name is required");
                } else if (email.getText().toString().trim().equals("")) {
                    email.setError("Email is required");
                }
            }
        });
//        if (!isFirstTime) {
        final String password = sharedPreferences.getString(Constants.ConfirmKey, "");
//        if (sharedPreferences.getString("user_name", "").equals("")) {
//            checkPermissions();
//        }



  /*      String email_id = sharedPreferences.getString("user_email", "");
        if (!email_id.equalsIgnoreCase("") && email_id != null) {
            email.setText(email_id);
        }*/
        if (!sharedPreferences.getString("user_name", "").equals("")) {
            hideViews();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!password.equalsIgnoreCase("")) {


                        startActivity(new Intent(SplashActivity.this, LockScreen.class));
                    } else {

//                        String email_id = sharedPreferences.getString("user_email", "");
//                        if (email_id.equalsIgnoreCase("")) {
//                            editor = sharedPreferences.edit();
//                            signIn(editor);
//                        }
                        startActivity(new Intent(getBaseContext(), LandingActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    finish();
                }
            }, 2000);
        }
    }


    private void hideViews() {
        findViewById(R.id.et_name).setVisibility(View.GONE);
        findViewById(R.id.et_email).setVisibility(View.GONE);
        findViewById(R.id.button_go).setVisibility(View.GONE);
    }

    @Override
    protected void onDriveClientReady() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        String email_id = sharedPreferences.getString("user_email", "");
        if (!email_id.equalsIgnoreCase("") && email_id != null) {
            email.setText(email_id);
        }

       else {
            if (email_id.equalsIgnoreCase("")) {
                editor = sharedPreferences.edit();
                signIn(editor);
            }
        }
    }
}


