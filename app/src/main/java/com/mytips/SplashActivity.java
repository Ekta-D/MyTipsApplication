package com.mytips;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    public static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    public static final String ISFIRST_TIME = "Isfirst_time";
    SharedPreferences sharedPreferences;
    boolean isFirstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sharedPreferences = getSharedPreferences("Pref", MODE_PRIVATE);
        if(sharedPreferences!=null)
            isFirstTime = sharedPreferences.getBoolean(ISFIRST_TIME, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.argb(77, 20, 20, 20));
            window.setNavigationBarColor(Color.argb(77, 20, 20, 20));
        }

        findViewById(R.id.button_go).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText name = (EditText) findViewById(R.id.et_name);
                EditText email = (EditText) findViewById(R.id.et_email);

                if(!name.getText().toString().trim().equals("") && !email.getText().toString().trim().equals("")){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("user_name", name.getText().toString().trim());
                    editor.putString("user_email", email.getText().toString().trim());
                    editor.commit();
                    startActivity(new Intent(getBaseContext(), LandingActivity.class));
                    finish();
                } else if(name.getText().toString().trim().equals("")){
                    name.setError("Name is required");
                } else if(email.getText().toString().trim().equals("")){
                    email.setError("Email is required");
                }
            }
        });
        if(!isFirstTime){
            checkPermissions();
        }
        if (!sharedPreferences.getString("user_name","").equals("")) {
            hideViews();
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run() {
                    startActivity(new Intent(getBaseContext(), LandingActivity.class));
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

    public boolean checkPermissions() {
        int permissionWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        int camera_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWrite != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (camera_permission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }


        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_ACCOUNTS);
            return false;
        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCOUNTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Toast.makeText(SplashActivity.this, "permissions granted!", Toast.LENGTH_SHORT).show();
                } else {
                    //Toast.makeText(SplashActivity.this, "permissions not granted!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}


