package com.mytips;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.mytips.Utils.Constants;

public class SetPassword extends AppCompatActivity {

    EditText editText;
    String password;
    SharedPreferences sharedPreferences;
    TextView label_passwrd;
    boolean is_done_clicked = false;
    String pass="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);

        editText = (EditText) findViewById(R.id.editText_passcode);
        label_passwrd = (TextView) findViewById(R.id.textview_passcode);
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(editText, InputMethodManager.SHOW_FORCED);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);

        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (is_done_clicked) {
                     pass = sharedPreferences.getString(Constants.PasswordKey, "");
                    password = s.toString();
                } else {
                    password = s.toString();
                    editor.putString(Constants.PasswordKey, password);
                    editor.apply();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    is_done_clicked = true;
                    editText.setText("");
                    label_passwrd.setText("Confirm Password");

                    return true;
                }
                return false;
            }
        });
    }
}
