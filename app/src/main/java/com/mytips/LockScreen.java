package com.mytips;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mytips.Utils.Constants;

public class LockScreen extends AppCompatActivity {

    EditText editText;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        editText = (EditText) findViewById(R.id.editText_lock_password);
        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);

        final String password = sharedPreferences.getString(Constants.ConfirmKey, "");
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String input = editText.getText().toString().trim();
                    if (input.equalsIgnoreCase(password)) {
                        startActivity(new Intent(LockScreen.this, LandingActivity.class));
                        finish();
                    } else {
                        Snackbar.make(editText, "Please enter correct password!", Snackbar.LENGTH_LONG).show();
                    }

                }

                return false;
            }
        });
    }
}
