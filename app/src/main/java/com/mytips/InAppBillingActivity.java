package com.mytips;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mytips.BillingUtils.IabHelper;
import com.mytips.BillingUtils.IabResult;

public class InAppBillingActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_billing);



    }
}
