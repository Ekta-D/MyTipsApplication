package com.mytips;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mytips.Utils.Constants;

public class ConfirmPassword extends AppCompatActivity {

    Button btn_cancel, btn_continue;
    EditText editText;
    String pass = "";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_password);

        btn_cancel = (Button) findViewById(R.id.confirm_cancel_button);
        btn_continue = (Button) findViewById(R.id.confirm_continue_button);
        editText = (EditText) findViewById(R.id.confirm_editText_passcode);
        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);
        pass = sharedPreferences.getString(Constants.PasswordKey, "");


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();

                if (password.length() >= 4) {
                    btn_continue.setEnabled(true);
                } else {
                    btn_continue.setEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmPassword.this.finish();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editText.getText().toString().trim();

                if (!password.equalsIgnoreCase(pass)) {
                    Toast.makeText(ConfirmPassword.this, "Password must be same", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.ConfirmKey, password);
                    editor.apply();

                    Toast.makeText(ConfirmPassword.this, "Password saved successfully!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ConfirmPassword.this,SettingsActivity.class));
                    ConfirmPassword.this.finish();
                }
            }
        });
    }
}
