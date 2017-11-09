package com.mytips.tempCopySetting;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.mytips.Adapter.AddTipeeAdapter;
import com.mytips.AppCompatPreferenceActivity;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Model.TipeeInfo;
import com.mytips.Preferences.Preferences;
import com.mytips.R;
import com.mytips.SetPassword;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import static android.content.ContentValues.TAG;

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
public class SettingsActivity extends AppCompatPreferenceActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    public Context context;

    ProgressDialog progress;
    private GoogleApiClient mGoogleApiClient;
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

        context = SettingsActivity.this;
        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);

        addPreferencesFromResource(R.xml.pref_general);
//        setHasOptionsMenu(true);
        final EditTextPreference editTextPreference_name, editTextPreference_email;
        Preference editTextPreference_show_add_tipee, restore_data;
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
        if (theme_list.getValue() == null)
            theme_list.setValueIndex(0);

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

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(tipees_dilog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                tipees_dilog.show();
                tipees_dilog.getWindow().setAttributes(lp);

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

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog.show();
                        dialog.getWindow().setAttributes(lp);

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
                        startActivity(new Intent(SettingsActivity.this, SetPassword.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        dialog1.dismiss();
                    }
                });


                btn_resetpasscode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(SettingsActivity.this, SetPassword.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
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


        restore_data = (Preference) findPreference("backup_restore");
        restore_data.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                readFromGoogleDrive();

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
                startActivity(new Intent(getActivity(), SettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    ArrayList<TipeeInfo> tippess_infolist = new ArrayList<>();


    void readFromGoogleDrive() {
        byte[] buf = null;
        if (mGoogleApiClient == null) {
            // Create the API client and bind it to an instance variable.
            // We use this instance as the callback for connection and connection
            // failures.
            // Since no account name is passed, the user is prompted to choose.
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        //Connect the client. Once connected, the camera is launched.
        mGoogleApiClient.connect();
        Query query = new Query
                .Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, Constants.FolderName))
                .build();


        new SearchFileTask().execute(query);

    }

    DriveId drive_id = null;


    class SearchFileTask extends AsyncTask<Query, Void, DriveApi.MetadataBufferResult> {


        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
//            progress = new ProgressDialog(SettingsActivity.this);
//            progress.show();
        }

        @Override
        protected DriveApi.MetadataBufferResult doInBackground(Query... params) {
            return Drive.DriveApi.query(mGoogleApiClient, params[0]).await();
        }

        @Override
        protected void onPostExecute(DriveApi.MetadataBufferResult metadataBufferResult) {


            if (metadataBufferResult.getMetadataBuffer().getCount() == 0) {
                Toast.makeText(SettingsActivity.this, "No file", Toast.LENGTH_SHORT).show();
            } else {
                String driveID = sharedPreferences.getString(Constants.SharedDriveId, "");
                drive_id = DriveId.decodeFromString(driveID);

                final DriveFile driveFile = Drive.DriveApi.getFile(mGoogleApiClient, drive_id);
                Query query = new Query
                        .Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, Constants.FolderName))
                        .build();
                Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                    @Override
                    public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                        MetadataBuffer metaData = metadataBufferResult.getMetadataBuffer();
                        for (int i = 0; i < metaData.getCount(); i++) {
                            drive_id = metaData.get(i).getDriveId();
                            String embed = metaData.get(i).getEmbedLink();
//                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//                            StrictMode.setThreadPolicy(policy); // for handshake failed
                            downloadData d = new downloadData();
                            d.execute(embed);

                            Log.i("drive", driveFile.toString());

                        }
                    }
                });
            }

            //   super.onPostExecute(metadataBufferResult);
        }
    }


}

class downloadData extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... params) {
//        int count;
//        InputStream input = null;
//        OutputStream output = null;
//        URLConnection connection1 = null;
//        //   connection1.setDoInput(true);
//        URL url1 = null;
//        try {
//            url1 = new URL(params[0]);
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        }
//        //  URLConnection conection = null;
////        try {
////            connection1 = url1.openConnection();
////
////            connection1.connect();
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        // getting file length
//        //  int lenghtOfFile = connection1.getContentLength();
//
//        // input stream to read file - with 8k buffer
//        ByteArrayOutputStream input1 = null;
//        FileOutputStream output1 = null;
//        input1 = new ByteArrayOutputStream();
//
//        try {
//            input = url1.openStream();
//            byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
//            int n;
//            while ( (n = input.read(byteChunk)) > 0 ) {
//                input1.write(byteChunk, 0, n);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if (input!=null )
//            {
//                try {
//                    input.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }


        //////////////////////////////////***********************************?????????////////////////////
        // Output stream to write file
//        try {
//            output1 = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + "/" + Constants.FetchedData);
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }


//        byte data[] = new byte[4 * 1024];
//
//        long total = 0;
//
//        try {
//            while ((count = input1.read(data)) != -1) {
//                total += count;
//                output1.write(data, 0, count);
//
//            }
//            output1.flush();
//
//            // closing streams
//            output1.close();
//            input1.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        File image = new File(Environment.getExternalStorageDirectory()
                .toString() + "/" + "tipseeDB");
        File image2 = new File(Environment.getExternalStorageDirectory()
                .toString() + "/" + "fetched");
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(image.getAbsolutePath())));
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }

            Log.d(TAG, buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }


      /*  FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/", "fetched"));
            InputStream inputStream = new FileInputStream(image2.getAbsolutePath());
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] buf = new byte[1024];
            int c;
            while ((c = inputStream.read(buf, 0, buf.length)) > 0) {
                byteArrayOutputStream.write(buf, 0, c);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                outputStream.write(bytes);
                outputStream.flush();
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        InputStream in1 = null;
        try {
            in1 = new FileInputStream(image.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OutputStream out = null;
        try {
            out = new FileOutputStream(image2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Copy the bits from instream to outstream
        byte[] buf = new byte[1024];
        int len;
        try {
            while ((len = in1.read(buf)) > 0) {
                try {
                    out.write(buf, 0, len);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            in1.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        try {
        //           InputStream input = new FileInputStream(image.getAbsolutePath());
//            OutputStream output = new FileOutputStream(image1.getAbsolutePath());
//            byte[] bu = new byte[1024];
//            int len;
//            while ((len = input.read(bu)) > 0) {
//                output.write(bu, 0, len);
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

   /*     try {
            FileOutputStream out = new FileOutputStream(image2.getAbsolutePath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
//        try {
//
//            FileOutputStream fos;
//
//            fos = new FileOutputStream(new File(Environment.getExternalStorageDirectory() + "/SampleFettched", "fetched.Db"));
//            byte[] b = new byte[8];
//            int i;
//            while ((i = fis.read(b)) != -1) {
//                fos.write(b, 0, i);
//            }
//            fos.flush();
//            fos.close();
//            fis.close();
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
