package com.mytips;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mytips.Utils.CommonMethods;

public class SetPasscode extends AppCompatActivity {

    Button button_set_fingerprint, button_set_passcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_passcode);


        findViewByIds();
    }

    public void findViewByIds() {
        button_set_fingerprint = (Button) findViewById(R.id.btn_fingerprint);
        button_set_passcode = (Button) findViewById(R.id.btn_set_passcode);
        button_set_passcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SetPasscode.this, SetPassword.class));
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Fingerprint API only available on from Android 6.0 (M)
            FingerprintManager fingerprintManager = (FingerprintManager) SetPasscode.this.getSystemService(Context.FINGERPRINT_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            if (!fingerprintManager.isHardwareDetected()) {
                // Device doesn't support fingerprint authentication
                Toast.makeText(this, "Sorry! Device doesn't support fingerprint authentication", Toast.LENGTH_SHORT).show();
                button_set_fingerprint.setVisibility(View.GONE);
                return;
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                // User hasn't enrolled any fingerprints to authenticate with
                Toast.makeText(this, "Please provide authentication for fingerprints", Toast.LENGTH_SHORT).show();
                button_set_fingerprint.setVisibility(View.GONE);
                return;
            } else {
                // Everything is ready for fingerprint authentication
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        CommonMethods.setTheme(getSupportActionBar(), SetPasscode.this);
    }
}
