package com.mytips;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mytips.Adapter.AddTipeeAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Model.TipeeInfo;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;
import com.mytips.Preferences.Preferences;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    double tipee_percent;
    String tipee_name_tipout;
    SharedPreferences sharedPreferences;
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString();

                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue(stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary(
                                index >= 0
                                        ? listPreference.getEntries()[index]
                                        : null);

                    } else if (preference instanceof RingtonePreference) {
                        // For ringtone preferences, look up the correct display value
                        // using RingtoneManager.
                        if (TextUtils.isEmpty(stringValue)) {
                            // Empty values correspond to 'silent' (no ringtone).
                            preference.setSummary(R.string.pref_ringtone_silent);

                        } else {
                            Ringtone ringtone = RingtoneManager.getRingtone(
                                    preference.getContext(), Uri.parse(stringValue));

                            if (ringtone == null) {
                                // Clear the summary if there was a lookup error.
                                preference.setSummary(null);
                            } else {
                                // Set the summary to reflect the new ringtone display
                                // name.
                                String name = ringtone.getTitle(preference.getContext());
                                preference.setSummary(name);
                            }
                        }

                    } else {
                        // For all other preferences, set the summary to the value's
                        // simple string representation.
                        preference.setSummary(stringValue);
                    }
                    return true;
                }
            };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        CommonMethods.setTheme(getSupportActionBar(), SettingsActivity.this);

        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);

        addPreferencesFromResource(R.xml.pref_general);
//        setHasOptionsMenu(true);
        final EditTextPreference editTextPreference_name, editTextPreference_email;
        Preference editTextPreference_show_add_tipee;
        final Preference preference_set_passcode;
        final ListPreference date_list, time_list, currency_list, theme_list;
        // Bind the summaries of EditText/List/Dialog/Ringtone preferences
        // to their values. When their values change, their summaries are
        // updated to reflect the new value, per the Android Design
        // guidelines.
        bindPreferenceSummaryToValue(findPreference("example_text"));
        bindPreferenceSummaryToValue(findPreference("example_list"));
        bindPreferenceSummaryToValue(findPreference("user_email"));
        bindPreferenceSummaryToValue(findPreference("date"));
        bindPreferenceSummaryToValue(findPreference("display_time"));
        bindPreferenceSummaryToValue(findPreference("current_list"));
        bindPreferenceSummaryToValue(findPreference("general_key"));
        bindPreferenceSummaryToValue(findPreference("backup_data"));
        bindPreferenceSummaryToValue(findPreference("backup_restore"));
        bindPreferenceSummaryToValue(findPreference("get_passcode"));
        bindPreferenceSummaryToValue(findPreference("edit_add_tipees"));

        editTextPreference_name = (EditTextPreference) findPreference("example_text");

        String stored_name, stored_email;
        /*if (sharedPreferences.getString("user_name", "").equals("") && sharedPreferences.getString("user_email", "").equals("")) {
            SharedPreferences sharedPrefs = getSharedPreferences("Pref", MODE_PRIVATE);
            stored_name = sharedPrefs.getString("user_name", "");
            stored_email = sharedPrefs.getString("user_email", "");
        } else {*/
        stored_name = sharedPreferences.getString("user_name", "");
        stored_email = sharedPreferences.getString("user_email", "");
        //}


        if (!stored_name.equalsIgnoreCase("")) {
            editTextPreference_name.setSummary(stored_name);
            editTextPreference_name.setText(stored_name);
        }

        editTextPreference_name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String edited_text = newValue.toString().trim();
                editTextPreference_name.setSummary(edited_text);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_name", edited_text);
                editor.commit();

                return true;
            }
        });

        editTextPreference_email = (EditTextPreference) findPreference("user_email");


        if (!stored_email.equalsIgnoreCase("")) {
            editTextPreference_email.setSummary(stored_email);
            editTextPreference_email.setText(stored_email);
        }
        editTextPreference_email.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String edited_text = newValue.toString().trim();
                editTextPreference_email.setSummary(edited_text);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("user_email", edited_text);
                editor.commit();
                return true;
            }
        });

        date_list = (ListPreference) findPreference("date");
        int selected_dateIndex = sharedPreferences.getInt("selected_date", 2);
        if (selected_dateIndex < 2) {
            date_list.setDefaultValue(selected_dateIndex);
        }
        date_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String selected_value = newValue.toString();

                int index = 0;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!selected_value.equalsIgnoreCase("")) {
                    index = Integer.parseInt(selected_value);
                }


                ListPreference listPreference = (ListPreference) preference;


                CharSequence[] entries = listPreference.getEntries();

                int tempIndex = 0;
                if (index == -1) {
                    tempIndex = 2;
                } else if (index == 0) {
                    tempIndex = 1;

                } else {
                    tempIndex = 0;
                }
                editor.putInt("selected_date", tempIndex);
                editor.commit();
                preference.setSummary(entries[tempIndex]);

                return true;
            }
        });

        time_list = (ListPreference) findPreference("display_time");
        int selected_time = sharedPreferences.getInt("selected_time", 1);
        if (selected_time < 1) {
            time_list.setDefaultValue(selected_time);
        }

        time_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String selected_value = newValue.toString();
                int index = 0;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!selected_value.equalsIgnoreCase("")) {
                    index = Integer.parseInt(selected_value);
                }

                ListPreference listPreference = (ListPreference) preference;


                CharSequence[] entries = listPreference.getEntries();
                int tempIndex = 0;
                if (index == -1) {
                    tempIndex = 1;
                } else {
                    tempIndex = 0;

                }
                editor.putInt("selected_time", tempIndex);
                editor.commit();
                preference.setSummary(entries[tempIndex]);
                return true;
            }
        });

        currency_list = (ListPreference) findPreference("current_list");
        int selected_currencyIndex = sharedPreferences.getInt("selected_currency", 2);
        if (selected_currencyIndex < 2) {
            currency_list.setDefaultValue(selected_currencyIndex);
        }

        currency_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String selected_value = newValue.toString();

                int index = 0;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!selected_value.equalsIgnoreCase("")) {
                    index = Integer.parseInt(selected_value);
                }

                editor.putInt("selected_currency", index);
                editor.commit();
                ListPreference listPreference = (ListPreference) preference;


                CharSequence[] entries = listPreference.getEntries();

                int tempIndex = 0;
                if (index == -1) {
                    tempIndex = 2;
                } else if (index == 0) {
                    tempIndex = 1;

                } else {
                    tempIndex = 0;
                }
                preference.setSummary(entries[tempIndex]);


                return true;
            }
        });
        theme_list = (ListPreference) findPreference("example_list");
        int selected_themeIndex = sharedPreferences.getInt("selected_theme", 2);
        if (selected_themeIndex < 2) {
            theme_list.setDefaultValue(selected_themeIndex);
        }
        theme_list.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {

                String selected_value = newValue.toString();

                int index = 0;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (!selected_value.equalsIgnoreCase("")) {
                    index = Integer.parseInt(selected_value);
                }

                editor.putInt("selected_theme", index);
                editor.commit();
                ListPreference listPreference = (ListPreference) preference;


                CharSequence[] entries = listPreference.getEntries();

                int tempIndex = 0;
                if (index == -1) {
                    tempIndex = 2;
                } else if (index == 0) {
                    tempIndex = 1;
                } else {
                    tempIndex = 0;
                }
                preference.setSummary(entries[tempIndex]);
                CommonMethods.setTheme(getSupportActionBar(), SettingsActivity.this);
                return true;
            }
        });
        editTextPreference_show_add_tipee = (Preference) findPreference("edit_add_tipees");

        editTextPreference_show_add_tipee.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                final Dialog tipees_dilog = new Dialog(SettingsActivity.this);
                tipees_dilog.setContentView(R.layout.show_edit_tipee_dialog);
                Button add_tipee_btn = (Button) tipees_dilog.findViewById(R.id.button);

                final ListView tipees_list = (ListView) tipees_dilog.findViewById(R.id.tipee_name_list);
                ArrayList<TipeeInfo> fetchInfo = Preferences.getInstance(SettingsActivity.this).getTipeeList(Constants.TipeeListKey);
                if (fetchInfo != null) {
                    AddTipeeAdapter adapter = new AddTipeeAdapter(SettingsActivity.this,
                            fetchInfo);
                    tipees_list.setAdapter(adapter);
                }


                tippess_infolist = new DatabaseOperations(SettingsActivity.this).fetchTipeeList(SettingsActivity.this);
                if (tippess_infolist.size() > 0) {
                    AddTipeeAdapter adapter = new AddTipeeAdapter(SettingsActivity.this,
                            tippess_infolist);
                    tipees_list.setAdapter(adapter);
                }
                add_tipee_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Dialog dialog = new Dialog(SettingsActivity.this);
                        dialog.setContentView(R.layout.add_tipee_dialog);

                        Button button_ok, button_cancel;
                        final EditText editText_tipeename, editText_tipee_out;

                        editText_tipeename = (EditText) dialog.findViewById(R.id.tipee_name);
                        editText_tipee_out = (EditText) dialog.findViewById(R.id.tipee_out);

                        button_ok = (Button) dialog.findViewById(R.id.ok_button);
                        button_cancel = (Button) dialog.findViewById(R.id.cancel_button);

                        button_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final String name = editText_tipeename.getText().toString().trim();
                                final String percent = editText_tipee_out.getText().toString().trim();

                                if (!percent.equalsIgnoreCase("")) {
                                    tipee_percent = Double.parseDouble(percent);
                                }
                                tipee_name_tipout = name + " " + percent + "%";

                                String tipee_id = UUID.randomUUID().toString();

                                TipeeInfo tipeeInfo = new TipeeInfo();
                                tipeeInfo.setName(name);
                                tipeeInfo.setId(tipee_id);
                                tipeeInfo.setPercentage(percent);
                                tippess_infolist.add(tipeeInfo);

                                new DatabaseOperations(SettingsActivity.this).insertTipeeInfo(tipee_id, name, tipee_percent);
                                if (tippess_infolist.size() > 0) {
                                    AddTipeeAdapter adapter = new AddTipeeAdapter(SettingsActivity.this,
                                            tippess_infolist);
//
//                                    Preferences.getInstance(SettingsActivity.this).save_list(Constants.TipeeListKey, tippess_infolist);
                                    tipees_list.setAdapter(adapter);
                                }
                                dialog.dismiss();
                            }
                        });
                        button_cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });


                        dialog.show();
                    }
                });

                tipees_dilog.show();
                return true;
            }
        });


        preference_set_passcode = (Preference) findPreference("get_passcode");

        preference_set_passcode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

//               Intent intent = new Intent(SettingsActivity.this, SetPasscode.class);
//                startActivity(intent);
                final Dialog dialog1 = new Dialog(SettingsActivity.this);
                dialog1.setContentView(R.layout.activity_set_passcode);

                Button btn_setpasscode, btn_setfingerprint, btn_resetpasscode, btn_removepasscode;

                String stored_password = sharedPreferences.getString(Constants.ConfirmKey, "");

                btn_setpasscode = (Button) dialog1.findViewById(R.id.btn_set_passcode);
                btn_setfingerprint = (Button) dialog1.findViewById(R.id.btn_fingerprint);
                btn_resetpasscode = (Button) dialog1.findViewById(R.id.btn_reset_passcode);
                btn_removepasscode = (Button) dialog1.findViewById(R.id.btn_remove_passcode);

                if (stored_password.equalsIgnoreCase("")) {
                    btn_resetpasscode.setVisibility(View.GONE);
                    btn_removepasscode.setVisibility(View.GONE);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Fingerprint API only available on from Android 6.0 (M)
                    FingerprintManager fingerprintManager = (FingerprintManager) SettingsActivity.this.getSystemService(Context.FINGERPRINT_SERVICE);
                    if (ActivityCompat.checkSelfPermission(SettingsActivity.this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                    }
                    if (!fingerprintManager.isHardwareDetected()) {
                        // Device doesn't support fingerprint authentication
                        Toast.makeText(SettingsActivity.this, "Sorry! Device doesn't support fingerprint authentication", Toast.LENGTH_SHORT).show();
                        btn_setfingerprint.setVisibility(View.GONE);

                    } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                        // User hasn't enrolled any fingerprints to authenticate with
                        Toast.makeText(SettingsActivity.this, "Please provide authentication for fingerprints", Toast.LENGTH_SHORT).show();
                        btn_setfingerprint.setVisibility(View.GONE);

                    } else {
                        // Everything is ready for fingerprint authentication
                    }
                }
                btn_setpasscode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SettingsActivity.this, SetPassword.class));
                        dialog1.dismiss();
                    }
                });


                btn_resetpasscode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SettingsActivity.this, SetPassword.class));
                        dialog1.dismiss();
                    }
                });
                btn_removepasscode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final SharedPreferences.Editor editor = sharedPreferences.edit();
                        AlertDialog.Builder al = new AlertDialog.Builder(SettingsActivity.this);
                        al.setTitle("Please confirm!");
                        al.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                editor.remove(Constants.ConfirmKey);
                                editor.remove(Constants.PasswordKey);
                                editor.commit();
                                dialog1.dismiss();

                            }
                        });
                        al.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                dialog1.dismiss();
                            }
                        });

                        AlertDialog alertDialog = al.create();
                        alertDialog.show();
                    }
                });
                dialog1.show();
                return true;
            }
        });
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
//    @Override
//    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    public void onBuildHeaders(List<Header> target) {
//        loadHeadersFromResource(R.xml.pref_headers, target);
//    }
//


    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName);
//                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
//                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    ArrayList<TipeeInfo> tippess_infolist = new ArrayList<>();


}
