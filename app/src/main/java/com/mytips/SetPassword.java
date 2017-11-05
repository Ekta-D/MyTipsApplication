package com.mytips;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mytips.Utils.Constants;

public class SetPassword extends AppCompatActivity {

    EditText editText, editText_old;
    String password;
    SharedPreferences sharedPreferences;
    TextView label_passwrd;
    boolean is_done_clicked = false;
    String pass = "";
    Button btn_cancel, btn_continue;
    SharedPreferences.Editor editor;
    String stored_password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        editText = (EditText) findViewById(R.id.editText_passcode);
        editText_old = (EditText) findViewById(R.id.old_passcode);
        label_passwrd = (TextView) findViewById(R.id.textview_passcode);
        btn_cancel = (Button) findViewById(R.id.cancel_button);
        btn_continue = (Button) findViewById(R.id.continue_button);

        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        stored_password = sharedPreferences.getString(Constants.ConfirmKey, "");

        if (!stored_password.equalsIgnoreCase("")) {
            editText_old.setVisibility(View.VISIBLE);
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                password = s.toString();

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
                SetPassword.this.finish();
            }
        });

        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password_validation();
            }
        });
    }


    public void password_validation() {

        String password = editText.getText().toString().trim();
        String old = editText_old.getText().toString().trim();
        if (!stored_password.equalsIgnoreCase("")) {
            if (!old.equalsIgnoreCase(stored_password)) {
                Toast.makeText(this, "Old Password did not match!", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        editor.putString(Constants.PasswordKey, password);
        editor.apply();
        Intent i = new Intent(this, ConfirmPassword.class);
        startActivity(i);
        finish();
    }
}
