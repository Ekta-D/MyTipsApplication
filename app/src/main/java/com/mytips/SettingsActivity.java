package com.mytips;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.fingerprint.FingerprintManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
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
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mytips.Adapter.AddTipeeAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Database.DatabaseUtils;
import com.mytips.Model.TipeeInfo;
import com.mytips.Preferences.Preferences;
import com.mytips.Utils.CommonMethods;
import com.google.android.gms.drive.DriveResourceClient;
import com.mytips.Utils.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static android.content.ContentValues.TAG;

//import com.google.android.gms.tasks.Task;
//import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
//import com.google.api.client.http.HttpRequest;
//import com.google.api.client.http.HttpRequestInitializer;
//import com.google.api.client.http.HttpTransport;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.api.services.drive.model.FileList;
//import com.itextpdf.text.pdf.codec.Base64;
//import com.google.api.services.drive.model.File;

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
public class SettingsActivity extends BaseDemoActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    public java.io.File f1 = null;
    public Context context;
    String action = "done";
    private GoogleApiClient mGoogleApiClient;
    double tipee_percent;
    String tipee_name_tipout;
    SharedPreferences sharedPreferences;
    boolean is_first = false;
    SharedPreferences.Editor editor;

    private static final int REQUEST_CODE_RESOLUTION = 3;

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
        progressDialog = new ProgressDialog(SettingsActivity.this);
        context = SettingsActivity.this;
        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        /*mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(action)) {
                    Thread thread = new Thread(runnable);
                    thread.start();
                }
            }
        };*/
        addPreferencesFromResource(R.xml.pref_general);
//        setHasOptionsMenu(true);
        final EditTextPreference editTextPreference_name, editTextPreference_email;
        Preference editTextPreference_show_add_tipee, backupData, restore_data;
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
        bindPreferenceSummaryToValue(findPreference("backup"));
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
                if (index == 0)
                    tempIndex = 1;
                else if (index == -1)
                    tempIndex = 2;
                else if (index == -2)
                    tempIndex = 3;
                else if (index == -3)
                    tempIndex = 4;
                else if (index == -4)
                    tempIndex = 5;
                else if (index == -5)
                    tempIndex = 6;


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

                                if (name.equalsIgnoreCase(" ") || percent.equalsIgnoreCase("")) {
                                    Toast.makeText(SettingsActivity.this, "Please enter details of tippee!", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
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
                signIn();
                return true;
            }
        });

        backupData = (Preference) findPreference("backup");
        backupData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //signIn();
                backupDataToDrive();
                return true;
            }
        });

    }

    private void backupDataToDrive() {
        String email = sharedPreferences.getString("user_email", "");
        Log.i("login_email", email);

        if (!email.contains("@gmail.com")) {
            AlertDialog.Builder alert_builder = new AlertDialog.Builder(SettingsActivity.this);
            alert_builder.setTitle("Make sure your account is of google");
            alert_builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = alert_builder.create();
            alertDialog.show();

        } else {

            try {

                File sd = Environment.getExternalStorageDirectory();
                final File data = Environment.getDataDirectory();
                FileChannel source = null;
                FileChannel destination = null;
                String currentDBPath = "/data/" + "com.mytips" + "/databases/" + DatabaseUtils.db_Name;
                String backupDBPath = DatabaseUtils.db_Name;
                File currentDB = new File(data, currentDBPath);
                backupDB = new File(sd, backupDBPath);
                try {
                    source = new FileInputStream(currentDB).getChannel();
                    destination = new FileOutputStream(backupDB).getChannel();
                    destination.transferFrom(source, 0, source.size());
                    source.close();
                    destination.close();
                    Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();


                    if (mGoogleApiClient == null) {
                        try {
                            mGoogleApiClient = new GoogleApiClient.Builder(this)
                                    .addApi(Drive.API)
                                    .addScope(Drive.SCOPE_FILE)
                                    .addConnectionCallbacks(this)
                                    .addOnConnectionFailedListener(this)
                                    .build();
                            is_first = true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mGoogleApiClient.connect();
                    uploadToDrive();

                    // TODO: 06-11-2017 account varification  and include to add two new jar files

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void uploadToDrive() {

        create_file_in_folder();
    }

    public void create_file_in_folder() {

        progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setMessage("Uploading database!");
        progressDialog.show();
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
            @Override
            public void onResult(@NonNull DriveApi.DriveContentsResult driveContentsResult) {


                saveToDrive(Drive.DriveApi.getRootFolder(mGoogleApiClient), "tipeesDB.db", "text/plain", backupDB, driveContentsResult);

            }
        });
    }

    boolean is_completed = false;
    File backupDB = null;

    void saveToDrive(final DriveFolder pFldr, final String titl,
                     final String mime, final java.io.File file, DriveApi.DriveContentsResult driveContentsResult) {

        // makeFolder();
       /* MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(Constants.DatabaseFileName)
                .setMimeType(mime)
                .setStarred(false)
                .build();*/
        if (mGoogleApiClient != null && pFldr != null && titl != null && mime != null && file != null)
            try {
                // create content from file
                String drive_folder_created_id = getResources().getString(R.string.folder_id);
                DriveId drivers_folder_id = DriveId.decodeFromString(drive_folder_created_id);

                Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(new ResultCallback<DriveApi.DriveContentsResult>() {
                    @Override
                    public void onResult(DriveApi.DriveContentsResult driveContentsResult) {
                        DriveContents cont = driveContentsResult != null && driveContentsResult.getStatus().isSuccess() ?
                                driveContentsResult.getDriveContents() : null;

                        // write file to content, chunk by chunk
                        if (cont != null) try {
                            OutputStream oos = cont.getOutputStream();
                            if (oos != null) try {
                                InputStream is = new FileInputStream(file);
                                byte[] buf = new byte[4096];
                                int c;
                                while ((c = is.read(buf, 0, buf.length)) > 0) {
                                    oos.write(buf, 0, c);
                                    oos.flush();
                                }
                            } finally {
                                oos.close();
                                progressDialog.dismiss();
                            }

                            // content's COOL, create metadata
                            MetadataChangeSet meta = new MetadataChangeSet.Builder().setTitle(titl).setMimeType(mime).build();

                            // now create file on GooDrive
                            pFldr.createFile(mGoogleApiClient, meta, cont).setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                @Override
                                public void onResult(DriveFolder.DriveFileResult driveFileResult) {
                                    if (driveFileResult != null && driveFileResult.getStatus().isSuccess()) {
                                        DriveFile dFil = driveFileResult != null && driveFileResult.getStatus().isSuccess() ?
                                                driveFileResult.getDriveFile() : null;
                                        if (dFil != null) {
                                            // BINGO , file uploaded
                                            dFil.getMetadata(mGoogleApiClient).setResultCallback(new ResultCallback<DriveResource.MetadataResult>() {
                                                @Override
                                                public void onResult(DriveResource.MetadataResult metadataResult) {
                                                    if (metadataResult != null && metadataResult.getStatus().isSuccess()) {
                                                        is_completed = true;
                                                        DriveId mDriveId = metadataResult.getMetadata().getDriveId();

                                                        editor.putString(Constants.SharedDriveId, mDriveId.encodeToString());
                                                        editor.commit();
                                                    }
                                                    progressDialog.dismiss();
                                                    if (is_completed) {
                                                        progressDialog.dismiss();
                                                    }
                                                }
                                            });


                                        }
                                    } else { /* report error */ }
                                }

                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        progressDialog.dismiss();

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

/*    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (mGoogleApiClient.isConnected()) {
           // searchFileInDrive();
            *//*String driveID = sharedPreferences.getString(Constants.SharedDriveId, "");
            java.io.File fl = new java.io.File(Environment.getExternalStorageDirectory(), Constants.FetchedData);
            DownloadFile(DriveId.decodeFromString(driveID), fl);*//*
        }
    }*/

    private void searchFileInDrive() {

        Query query =
                new Query.Builder().addFilter(Filters.and(Filters.eq(SearchableField.TITLE, "tipeesDB.db")))
                        .build();

        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {

                if (metadataBufferResult.getStatus().isSuccess()) {
                    //metadataBufferResult.getMetadataBuffer().get
                    System.out.println("Success: " + metadataBufferResult.getMetadataBuffer().toString());

                } else {
                    System.out.println("Failure " + "not found");
                }

            }
        });
        //Task<DriveContents> openTask = getDriveResourceClient().openFile(file, DriveFile.MODE_READ_WRITE);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("drive", "connected called");
        /*if (mGoogleApiClient.isConnected()) {
            if (!is_first) {
                progressDialog.dismiss();
                uploadToDrive();
            }

        } else {
            if (mGoogleApiClient == null) {
                try {
                    mGoogleApiClient = new GoogleApiClient.Builder(this)
                            .addApi(Drive.API)
                            .addScope(Drive.SCOPE_FILE)
                            .addConnectionCallbacks(this)
                            .addOnConnectionFailedListener(this)
                            .build();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mGoogleApiClient.connect();
        }*/
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("drive", "connected failed");

        /*if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
                progressDialog.dismiss();
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            progressDialog.dismiss();
        }*/
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
//        DownloadFile(DriveId.decodeFromString(driveID), fl);
    }

    ProgressDialog progressDialog;
    boolean isDone = false;
    BroadcastReceiver mReceiver;


    private void DownloadFile(final DriveId driveId, final java.io.File filename) {
        progressDialog.setMessage("Restoring factory!");
        progressDialog.show();
        if (!filename.exists()) {
            try {
                filename.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (mGoogleApiClient.isConnected()) {
            //   mGoogleApiClient.connect();
            /*DriveFile file = Drive.DriveApi.getFile(
                    mGoogleApiClient, driveId);
            file.getMetadata(mGoogleApiClient)
                    .setResultCallback(metadataRetrievedCallback);*/

        } else {
            mGoogleApiClient.connect(GoogleApiClient.SIGN_IN_MODE_REQUIRED);
        }

    }

    boolean is_done = false;
    private ResultCallback<DriveResource.MetadataResult> metadataRetrievedCallback = new ResultCallback<DriveResource.MetadataResult>() {
        @Override
        public void onResult(DriveResource.MetadataResult result) {
            if (result.getStatus().isSuccess()) {
                is_done = true;
                Intent intent = new Intent();
                intent.setAction(action);
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(SettingsActivity.this);
                manager.sendBroadcast(intent);
                return;
            }
        }
    };


    public boolean fileCopy(java.io.File src, java.io.File dst) throws IOException {
        boolean is_completed = false;
        InputStream in = new FileInputStream(src.getAbsolutePath());
        OutputStream mOutput = new FileOutputStream(dst.getAbsolutePath());
        byte[] mBuffer = new byte[1024];
        int mLength;
        while ((mLength = in.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
            is_completed = true;
        }
        mOutput.flush();
        mOutput.close();
        in.close();

        restartApp();
        return is_completed;
    }

    public void restartApp() {
        Intent mStartActivity = new Intent(context, LandingActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
        System.exit(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(action);

        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(this);
        manager.registerReceiver(mReceiver, filter);


    }

    protected static final int REQUEST_CODE_SIGN_IN = 0;

    @Override
    protected void onDriveClientReady() {
        pickTextFile()
                .addOnSuccessListener(this,
                        new OnSuccessListener<DriveId>() {
                            @Override
                            public void onSuccess(DriveId driveId) {
                                retrieveContents(driveId.asDriveFile());
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "No file selected", e);
                        showMessage("nothing happened");
                        finish();
                    }
                });
    }

    private void retrieveContents(DriveFile file) {

        progressDialog.setMessage("Restoring Database!");
        progressDialog.show();
        Task<DriveContents> openFileTask =
                getDriveResourceClient().openFile(file, DriveFile.MODE_READ_ONLY);
        // [END open_file]
        // [START read_contents]
        openFileTask
                .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                        DriveContents contents = task.getResult();
                        InputStream inputstream = contents.getInputStream();
                        try {
                            f1 = new java.io.File(Environment.getExternalStorageDirectory(), Constants.FetchedData);
                            FileOutputStream fileOutput = new FileOutputStream(f1);

                            byte[] buffer = new byte[1024];
                            int bufferLength = 0;
                            while ((bufferLength = inputstream.read(buffer)) > 0) {
                                fileOutput.write(buffer, 0, bufferLength);
                                isDone = true;

                            }
                            fileOutput.close();
                            inputstream.close();
                            if (isDone) {
                                final java.io.File data = Environment.getDataDirectory();
                                String currentDBPath = "/data/" + "com.mytips" + "/databases/" + DatabaseUtils.db_Name;
                                java.io.File currentDB = new java.io.File(data, currentDBPath);
                                try {
                                    fileCopy(f1, currentDB);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // Process contents...
                        // [START_EXCLUDE]
                        // [START read_as_string]
                        /*try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(contents.getInputStream()))) {
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }
                            showMessage("File read success");
                           // mFileContents.setText(builder.toString());
                        }*/
                        // [END read_as_string]
                        // [END_EXCLUDE]
                        // [START discard_contents]
                        Task<Void> discardTask = getDriveResourceClient().discardContents(contents);
                        // [END discard_contents]
                        return discardTask;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure
                        // [START_EXCLUDE]
                        Log.e(TAG, "Unable to read contents", e);
                        showMessage("Unable to read contents");
                        finish();
                        // [END_EXCLUDE]
                    }
                });
        // [END read_contents]
    }

    String driveID = "";
    /*Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (is_done) {
                driveID = sharedPreferences.getString(Constants.SharedDriveId, "");

                loadFile(Constants.DatabaseFileName);


                DriveFile file = Drive.DriveApi.getFile(
                        mGoogleApiClient, DriveId.decodeFromString(driveID));

                DriveApi.DriveContentsResult driveContentsResult = file.open(
                        mGoogleApiClient,
                        DriveFile.MODE_READ_ONLY, null).await();
                DriveContents driveContents = driveContentsResult
                        .getDriveContents();
                InputStream inputstream = driveContents.getInputStream();

                try {
                    f1 = new java.io.File(Environment.getExternalStorageDirectory(), Constants.FetchedData);
                    FileOutputStream fileOutput = new FileOutputStream(f1);

                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    while ((bufferLength = inputstream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                        isDone = true;

                    }
                    fileOutput.close();
                    inputstream.close();
                    if (isDone) {
                        final java.io.File data = Environment.getDataDirectory();
                        String currentDBPath = "/data/" + "com.mytips" + "/databases/" + DatabaseUtils.db_Name;
                        java.io.File currentDB = new java.io.File(data, currentDBPath);
                        try {
                            fileCopy(f1, currentDB);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }
    };*/

    private void loadFile(String filename) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, filename))
                .build();

        DriveFolder driveFolder = Drive.DriveApi.getRootFolder(mGoogleApiClient);
        driveFolder.queryChildren(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(@NonNull DriveApi.MetadataBufferResult metadataBufferResult) {
                final MetadataBuffer metadataBuffer = metadataBufferResult.getMetadataBuffer();
                for (Metadata metadata : metadataBuffer) {
                    String li = metadata.getEmbedLink();
                    DriveId driveId = metadata.getDriveId();
                    Log.i("link", li);

                }
            }
        });
    }
   /* private static List<com.google.api.services.drive.model.File> retrieveAllFiles(com.google.api.services.drive.Drive services) {
        List<com.google.api.services.drive.model.File> result = new ArrayList<>();
        com.google.api.services.drive.Drive.Files.List request = null;
        do {
            try {
                request = services.files().list();
                FileList fileList = request.execute();
                result.addAll(fileList.getItems());
                request.setPageToken(fileList.getNextPageToken());

            } catch (IOException e) {
                System.out.println("An error occurred: " + e);

            }
        } while (request.getPageToken() != null &&
                request.getPageToken().length() > 0);
        Log.i("result", result.toString());

        return result;

    }*/

}





