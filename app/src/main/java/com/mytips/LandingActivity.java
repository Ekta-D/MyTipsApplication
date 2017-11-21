package com.mytips;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.api.client.json.gson.GsonFactory;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPTableEvent;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.mytips.Adapter.SpinnerProfile;
import com.mytips.Adapter.SummaryAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Database.DatabaseUtils;
import com.mytips.Model.AddDay;
import com.mytips.Model.Profiles;
import com.mytips.Utils.CommonMethods;
import com.mytips.Utils.Constants;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private LinearLayout mRevealView;
    private boolean hidden = true;
    private ImageButton add_day, profile, share, invite, preferences, backup, emailData;
    Spinner spinnerProfile, spinnerReportType;
    ListView mListView;
    String[] reportTypeArray = new String[]{"Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
    Toolbar toolbar;
    ArrayList<AddDay> addDayArrayList;
    DatabaseOperations databaseOperations;
    File backupDB = null;
    Context context;
    TextView textView_total_earnings, textView_summaryTips, textView_summery_tds, textView_summery_tip_out, textView_hour_wage;
    RelativeLayout summery_tds_layout, summery_tip_outLayout, dashboard_bottm;
    Calendar startcalendar, endcalendar;
    private static final int DIALOG_ERROR_CODE = 100;
    ArrayList<AddDay> updated_spinner;
    String selected_profileName;
    ArrayList<Profiles> profiles;
    TextView no_data;
    String start_week = "";
    String start_shift = "";
    boolean pastDateSelected;
    SharedPreferences sharedPreferences;
    String selected_summary_type = "Daily";
    int default_date_format = 0;
    //    FloatingActionButton floatingActionButton;
//FloatingActionButton floatingActionButton;

    long start_shift_long, end_shift_long;
    ArrayList<AddDay> data;
    SharedPreferences.Editor editor;
    private static final String TAG = "drive-quickstart";
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
    private static final int REQUEST_CODE_CREATOR = 2;
    private static final int REQUEST_CODE_RESOLUTION = 3;
    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    public static final int MY_PERMISSIONS_REQUEST_ACCOUNTS = 1;
    boolean is_first = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        boolean grantedPermission = checkPermissions();
        if (Build.VERSION.SDK_INT >= 23) {
            if (!grantedPermission) {
                checkPermissions();

            }
            //only api 23 above
        }
        if (mGoogleSignInClient == null)
            mGoogleSignInClient = buildGoogleSignInClient();

        sharedPreferences = getSharedPreferences("MyTipsPreferences", MODE_PRIVATE);

        editor = sharedPreferences.edit();

        default_date_format = sharedPreferences.getInt("selected_date", 2);

//        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
//        floatingActionButton.setVisibility(View.VISIBLE);
        startcalendar = Calendar.getInstance();
        endcalendar = Calendar.getInstance();

        context = LandingActivity.this;
        databaseOperations = new DatabaseOperations(LandingActivity.this);
        updated_spinner = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        no_data = (TextView) findViewById(R.id.no_data_landing);
        no_data.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideRevealView();
                return false;
            }
        });
        setSupportActionBar(toolbar);

        CommonMethods.setTheme(getSupportActionBar(), LandingActivity.this);

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);
        dashboard_bottm = (RelativeLayout) findViewById(R.id.dashboard_total_earnings);
        add_day = (ImageButton) findViewById(R.id.add_day);
        add_day.setVisibility(View.VISIBLE);
        emailData = (ImageButton) findViewById(R.id.email_data);
        emailData.setOnClickListener(this);
        profile = (ImageButton) findViewById(R.id.profile);
        share = (ImageButton) findViewById(R.id.share);
        invite = (ImageButton) findViewById(R.id.invite);
        preferences = (ImageButton) findViewById(R.id.preferences);
        backup = (ImageButton) findViewById(R.id.backup);

        textView_total_earnings = (TextView) findViewById(R.id.total_earnings);
        textView_hour_wage = (TextView) findViewById(R.id.hour_wage);
        textView_summaryTips = (TextView) findViewById(R.id.summery_tips);
        textView_summery_tds = (TextView) findViewById(R.id.summery_tds);
        textView_summery_tip_out = (TextView) findViewById(R.id.summery_tip_out);

        summery_tds_layout = (RelativeLayout) findViewById(R.id.row_two);
        summery_tip_outLayout = (RelativeLayout) findViewById(R.id.row_three);

        summery_tds_layout.setVisibility(View.VISIBLE);
        summery_tip_outLayout.setVisibility(View.VISIBLE);
        add_day.setOnClickListener(this);
        profile.setOnClickListener(this);
        share.setOnClickListener(this);
        invite.setOnClickListener(this);
        preferences.setOnClickListener(this);
        backup.setOnClickListener(this);

        spinnerProfile = (Spinner) findViewById(R.id.spinner_profile);
        spinnerReportType = (Spinner) findViewById(R.id.spinner_report);
        mListView = (ListView) findViewById(R.id.add_summarylist);

        addDayArrayList = new ArrayList<>();
        addDayArrayList = databaseOperations.fetchAddDayDetails(context);
        if (addDayArrayList != null && addDayArrayList.size() > 0)
            no_data.setVisibility(View.GONE);

        //  Log.i("add_daylist", String.valueOf(addDayArrayList.size()));
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, reportTypeArray);
        spinnerReportType.setAdapter(typeAdapter);

        spinnerProfile.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (hidden) {
                    spinnerProfile.setEnabled(true);
                    spinnerReportType.setEnabled(true);
                    spinnerProfile.setClickable(true);
                    spinnerReportType.setClickable(true);
                } else {
                    spinnerProfile.setEnabled(false);
                    spinnerReportType.setEnabled(false);
                    spinnerProfile.setClickable(false);
                    spinnerReportType.setClickable(false);
                }
                return false;
            }
        });
        spinnerProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (profiles.size() > 0) {

//                    selected_ProfileID = addDayArrayList.get(position).getId();
//                    selected_profileName = addDayArrayList.get(position).getProfile();
                    selected_profileName = profiles.get(position).getProfile_name();
                    start_week = profiles.get(position).getStartday();

                    //if (addDayArrayList.size() > 0) {
                    // start_shift = addDayArrayList.get(position).getStart_shift();
                    //  no_data.setVisibility(View.GONE);
                    //updateView(addDayArrayList, selected_profileName);
                       /* if (reportTypeArray.length > 0) {
                            selected_summary_type = spinnerReportType.getSelectedItem().;
                        }*/

                    if (!start_week.equalsIgnoreCase("") && !selected_profileName.equalsIgnoreCase("")) {
                        changeData(selected_summary_type, start_week, selected_profileName);
                    }

                    //}
                }
                spinnerProfile.setSelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerReportType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (hidden) {
                    spinnerProfile.setEnabled(true);
                    spinnerReportType.setEnabled(true);
                    spinnerProfile.setClickable(true);
                    spinnerReportType.setClickable(true);
                } else {
                    spinnerProfile.setEnabled(false);
                    spinnerReportType.setEnabled(false);
                    spinnerProfile.setClickable(false);
                    spinnerReportType.setClickable(false);
                }
                return false;
            }
        });

        spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerReportType.setSelection(position);
                selected_summary_type = parent.getSelectedItem().toString();
                if (!start_week.equalsIgnoreCase("") && !selected_profileName.equalsIgnoreCase("")) {
                    changeData(selected_summary_type, start_week, selected_profileName);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final AddDay addDay = addDayArrayList.get(position);

                AlertDialog.Builder alert = new AlertDialog.Builder(LandingActivity.this);
                alert.setTitle("Make your selection");
                final String names[] = {"Edit", "Delete"};
                alert.setItems(names, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent intent = new Intent(LandingActivity.this, AddDayActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(Constants.AddDayProfile, addDay);
                            intent.putExtras(bundle);
                            startActivity(intent);

                        } else if (which == 1) {
                            deleteProfile(addDay);
                            addDayArrayList.remove(position);

                            setAdapter(addDayArrayList);
                            adapter.notifyDataSetChanged();
                            updateBottom(addDayArrayList);
                        }
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });

        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                hideRevealView();
                return false;
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
      /*  if (mGoogleApiClient == null) {
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
        mGoogleApiClient.connect();*/


        profiles = new DatabaseOperations(LandingActivity.this).fetchAllProfile(LandingActivity.this);

        Profiles profiles0 = new Profiles();
        profiles0.setProfile_name("All");
        profiles0.setStartday("Sunday");
        profiles.add(0, profiles0);
        updateSpinner(profiles);
        if (!start_week.equalsIgnoreCase("") && !selected_profileName.equalsIgnoreCase("")) {
            changeData(selected_summary_type, start_week, selected_profileName);
        }

        CommonMethods.setTheme(getSupportActionBar(), LandingActivity.this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        profiles = new DatabaseOperations(LandingActivity.this).fetchAllProfile(LandingActivity.this);
        Profiles profiles0 = new Profiles();
        profiles0.setProfile_name("All");
        profiles0.setStartday("Sunday");
        profiles.add(0, profiles0);
        updateSpinner(profiles);
        if (!start_week.equalsIgnoreCase("") && !selected_profileName.equalsIgnoreCase("")) {
            changeData(selected_summary_type, start_week, selected_profileName);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void hideRevealView() {
        if (mRevealView.getVisibility() == View.VISIBLE) {
            mRevealView.setVisibility(View.GONE);
            hidden = true;

            spinnerProfile.setEnabled(true);
            spinnerReportType.setEnabled(true);
            spinnerProfile.setClickable(true);
            spinnerReportType.setClickable(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                int cx = (mRevealView.getLeft() + mRevealView.getRight());
                int cy = (mRevealView.getTop());

                // to find  radius when icon is tapped for showing layout
                int startradius = 0;
                int endradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                // performing circular reveal when icon will be tapped
                Animator animator = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    animator = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, startradius, endradius);
                }
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                animator.setDuration(400);

                //reverse animation
                // to find radius when icon is tapped again for hiding layout
                //  starting radius will be the radius or the extent to which circular reveal animation is to be shown

                int reverse_startradius = Math.max(mRevealView.getWidth(), mRevealView.getHeight());

                //endradius will be zero
                int reverse_endradius = 0;

                // performing circular reveal for reverse animation
                Animator animate = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    animate = ViewAnimationUtils.createCircularReveal(mRevealView, cx, cy, reverse_startradius, reverse_endradius);
                }
                if (hidden) {

                    // to show the layout when icon is tapped
                    mRevealView.setVisibility(View.VISIBLE);
                    animator.start();
                    hidden = false;
                } else {
                    mRevealView.setVisibility(View.VISIBLE);

                    // to hide layout on animation end
                    animate.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mRevealView.setVisibility(View.INVISIBLE);
                            hidden = true;
                        }
                    });
                    animate.start();
                }

                return true;

            case R.id.fab:

                final Dialog dialog1 = new Dialog(LandingActivity.this);
                dialog1.setContentView(R.layout.search_dialog);
                dialog1.setTitle("Filter");

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog1.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                dialog1.show();
                dialog1.getWindow().setAttributes(lp);

                Button btn, btn2;
                final EditText editText1, editText2;

                btn = (Button) dialog1.findViewById(R.id.btn_01);
                btn2 = (Button) dialog1.findViewById(R.id.btn_02);

                editText1 = (EditText) dialog1.findViewById(R.id.editText_01);
                editText2 = (EditText) dialog1.findViewById(R.id.editText_02);


                String string1 = sharedPreferences.getString(Constants.EditTextStart, "");
                String string2 = sharedPreferences.getString(Constants.EditTextEnd, "");
                editText1.setText(string1);
                editText2.setText(string2);
                editText1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDatePicker(default_date_format, R.id.editText_01, editText1);
                    }
                });


                editText2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDatePicker(default_date_format, R.id.editText_02, editText2);
                    }
                });
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data = new ArrayList<AddDay>();

                        data = new DatabaseOperations(LandingActivity.this).fetchDataBetweenDates(startcalendar.getTimeInMillis(), endcalendar.
                                getTimeInMillis(), selected_profileName);

                        setAdapter(data);
                        adapter.notifyDataSetChanged();

                        dialog1.dismiss();

                    }
                });

                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editor.remove(Constants.EditTextStart).commit();
                        editor.remove(Constants.EditTextEnd).commit();

                        editText1.setText("");
                        editText2.setText("");
                        if (data != null && data.size() > 0) {
                            data.clear();
                        }
                        if (!start_week.equalsIgnoreCase("") && !selected_profileName.equalsIgnoreCase("")) {
                            changeData(selected_summary_type, start_week, selected_profileName);
                        }
                        dialog1.dismiss();
                    }
                });
                dialog1.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        hideRevealView();
        switch (v.getId()) {
            case R.id.add_day:
                startActivity(new Intent(getBaseContext(), AddDayActivity.class));
                break;
            case R.id.profile:
                startActivity(new Intent(getBaseContext(), ActiveProfiles.class));
                break;
            case R.id.preferences:
                startActivity(new Intent(getBaseContext(), SettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.share:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                    String sAux = "\nLet me recommend you this application\n\n";
                    sAux = sAux + "https://play.google.com/store/apps/details?id=Orion.Soft \n\n";
                    i.putExtra(Intent.EXTRA_TEXT, sAux);
                    startActivity(Intent.createChooser(i, "choose one"));
                } catch (Exception e) {
                    //e.toString();
                }

                break;
            case R.id.invite:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_EMAIL, "enter email");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My application name");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "My application text");


                shareIntent.setType("text/html");
                Intent intent = Intent.createChooser(shareIntent, "Choose Email Client");
                startActivityForResult(intent, 100);

                break;
            case R.id.backup:

                String email = sharedPreferences.getString("user_email", "");
                Log.i("login_email", email);

                if (!email.contains("@gmail.com")) {
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(LandingActivity.this);
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
                break;

            case R.id.email_data:


                if (addDayArrayList.size() > 0) {
                    Thread run = new Thread(thread);
                    run.start();
                } else {
                    Toast.makeText(LandingActivity.this, "No Data found", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }

    GoogleSignInClient mGoogleSignInClient;
    SummaryAdapter adapter;
    ArrayList<AddDay> selectedProfile;

    public void updateView(ArrayList<AddDay> addDayArrayList, String selected_profileName) {
        selectedProfile = new ArrayList<>();
        for (int i = 0; i < addDayArrayList.size(); i++) {
            AddDay addDay = addDayArrayList.get(i);
            if (addDay.getProfile().equalsIgnoreCase(selected_profileName)) {
                selectedProfile.add(addDay);
            }

        }
        if (selectedProfile.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
            adapter = new SummaryAdapter(default_date_format, LandingActivity.this, selectedProfile);
            mListView.setAdapter(adapter);
            no_data.setVisibility(View.GONE);
            updateBottom(selectedProfile);
        } else {
            no_data.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            updateBottom(selectedProfile);
        }

    }

    public void updateBottom(ArrayList<AddDay> addDayArrayList) {

        String total = "0";
        double totalEarnings = 0;
        double total_livetips = 0;
        String liveTips = "0";
        double tournament_totalTD = 0;
        String tds = "0";
        String tipOut = "0";
        double totalOuts = 0;
        String hr = "0";
        double hrs = 0;
        for (int i = 0; i < addDayArrayList.size(); i++) {

            total = addDayArrayList.get(i).getTotal_earnings();
            if (!total.equalsIgnoreCase("") && !total.equalsIgnoreCase("--")) {
                double t = Double.parseDouble(total);
                totalEarnings = totalEarnings + t;
            }
            liveTips = addDayArrayList.get(i).getTotal_tips();
            if (!liveTips.equalsIgnoreCase("")) {
                double t = Double.parseDouble(liveTips);
                total_livetips = total_livetips + t;
            }
            tds = addDayArrayList.get(i).getTotal_tournament_downs();
            if (!tds.equalsIgnoreCase("")) {
                double t = Double.parseDouble(tds);
                tournament_totalTD = tournament_totalTD + t;
            }

            tipOut = addDayArrayList.get(i).getTip_out();
            if (!tipOut.equalsIgnoreCase("")) {
                double t = Double.parseDouble(tipOut);
                totalOuts = totalOuts + t;
            }

            hr = addDayArrayList.get(i).getWages_hourly();
            if (!hr.equalsIgnoreCase("")) {
                double t = Double.parseDouble(hr);
                hrs = hrs + t;
            }
        }
        textView_total_earnings.setText("$" + String.valueOf(String.format("%.2f", totalEarnings)));
        textView_summaryTips.setText("Live Tips:$" + String.valueOf(String.format("%.2f", total_livetips)) + ", ");
        textView_summery_tds.setText("Tournament Downs:$" + String.valueOf(String.format("%.2f", tournament_totalTD)) + ", ");
        textView_summery_tip_out.setText("Tip-out:$" + String.valueOf(String.format("%.2f", totalOuts)) + ", ");


        textView_hour_wage.setText("Hourly Wage:$" + String.valueOf(String.format("%.2f", hrs)));
    }


    public void deleteProfile(AddDay addDay) {
        try {

            new DatabaseOperations(LandingActivity.this).delete_add_day(String.valueOf(addDay.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void updateSpinner(ArrayList<Profiles> profilesArrayList) {
        if (profilesArrayList.size() > 0) {

            SpinnerProfile profileAdapter = new SpinnerProfile(context, profilesArrayList);
            spinnerProfile.setAdapter(profileAdapter);
            selected_profileName = profilesArrayList.get(0).getProfile_name();
        } else {
            String[] profiles_empty = new String[]{"[none]"};
            ArrayAdapter<String> profileAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, profiles_empty);
            spinnerProfile.setAdapter(profileAdapter);
            selected_profileName = profiles_empty[0];
        }
    }

    public void changeData(String spinner_selected, String start_week, String selected_profileName) {
        String date_format = "";
        if (default_date_format == 2) {
            date_format = "MM/dd/yyyy";
        } else if (default_date_format == 1) {
            date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
        } else {
            date_format = "MMM dd,yyyy";
        }
        if (spinner_selected.equalsIgnoreCase("Daily")) {

            Calendar cal = Calendar.getInstance();

            String currentDate = (cal.get(Calendar.MONTH) + 1) + "/"
                    + cal.get(Calendar.DAY_OF_MONTH) + "/"
                    + cal.get(Calendar.YEAR);

            //String current_date = String.valueOf(cal.getTimeInMillis());
            addDayArrayList = new DatabaseOperations(LandingActivity.this).fetchDailyData(selected_profileName);
            setAdapter(addDayArrayList);
            updateBottom(addDayArrayList);

        } else if (spinner_selected.equalsIgnoreCase("Weekly")) {
//            int start_day = CommonMethods.getDay(start_week);
            int start_day = 1;// for sunday
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_WEEK, start_day);
            calendar1.set(Calendar.HOUR_OF_DAY, 0);
            calendar1.set(Calendar.MINUTE, 0);
            calendar1.set(Calendar.SECOND, 0);
            calendar1.set(Calendar.MILLISECOND, 0);

            DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.ENGLISH);
            ArrayList<String> string_dates = new ArrayList<>();
            ArrayList<Long> dates = new ArrayList<>();
            for (int i = 0; i < 7; i++) {

                String str_date = dateFormat.format(calendar1.getTime().getTime());

                Date date = calendar1.getTime();
                long lon = date.getTime();

                calendar1.add(Calendar.DATE, 1);

                string_dates.add(str_date);


                dates.add(lon);
            }
            long reset_next_week = 0;
            long reset_current = 0;
            if (dates.size() > 0) {

                reset_current = dates.get(0);
                reset_next_week = dates.get(6);
                addDayArrayList = new DatabaseOperations(LandingActivity.this).fetchDataBetweenDates(reset_current, reset_next_week, selected_profileName);

                setAdapter(addDayArrayList);
                updateBottom(addDayArrayList);
            }

//            if (string_dates.size() > 0) {
//                for (int i = 0; i < string_dates.size(); i++) {
//                    String j = string_dates.get(i);
//                    ArrayList<AddDay> list = new ArrayList<>();
//                    list = new DatabaseOperations(LandingActivity.this).fetchDailyData(j, selected_profileName);
//                    addDayArrayList.addAll(list);
//                }
//                setAdapter(addDayArrayList);
//                updateBottom(addDayArrayList);
//            }

        } else if (spinner_selected.equalsIgnoreCase("Bi-Weekly")) {

            //   int start_day = CommonMethods.getDay(start_week);
            int start_day = 1;//for sunday
            Calendar calendar1 = Calendar.getInstance();
            calendar1.set(Calendar.DAY_OF_WEEK, start_day);

            DateFormat dateFormat = new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.ENGLISH);
            ArrayList<String> string_dates = new ArrayList<>();
            ArrayList<Long> dates = new ArrayList<>();
            for (int i = 0; i < 15; i++) {

                String str_date = dateFormat.format(calendar1.getTime().getTime());

                Date date = calendar1.getTime();
                long lon = date.getTime();

                calendar1.add(Calendar.DATE, 1);

                string_dates.add(str_date);


                dates.add(lon);
            }
            Log.i("string_dates", string_dates.toString());

            long reset_next_week = 0;
            long reset_current = 0;
            if (dates.size() > 0) {

                reset_current = dates.get(0);
                reset_next_week = dates.get(14);
                addDayArrayList = new DatabaseOperations(LandingActivity.this).fetchDataBetweenDates(reset_current, reset_next_week, selected_profileName);

                setAdapter(addDayArrayList);
                updateBottom(addDayArrayList);
            }

        } else if (spinner_selected.equalsIgnoreCase("Monthly")) {
            Calendar cal = Calendar.getInstance();
            int month = cal.get(Calendar.MONTH);

            Calendar cal1 = Calendar.getInstance();

            cal1.set(Calendar.MONTH, month);

            int start_month = cal1.get(Calendar.MONTH); // 0 for jan and 11 for Dec
            int year = cal1.get(Calendar.YEAR);
            int days = CommonMethods.numDays(start_month, year);


            Calendar start_calendar = Calendar.getInstance();
            start_calendar.set(Calendar.DAY_OF_MONTH, 1);


            Calendar end_calendar = Calendar.getInstance();
            if (days == 28) {
                end_calendar.set(Calendar.DAY_OF_MONTH, 28);
            } else if (days == 29) {
                end_calendar.set(Calendar.DAY_OF_MONTH, 29);
            } else if (days == 30) {
                end_calendar.set(Calendar.DAY_OF_MONTH, 30);
            } else {
                end_calendar.set(Calendar.DAY_OF_MONTH, 31);
            }


            Date start_date = start_calendar.getTime();
            long start_date_long = start_date.getTime();

            Date end_date = end_calendar.getTime();
            long end_date_long = end_date.getTime();


            addDayArrayList = new DatabaseOperations(LandingActivity.this).fetchDataBetweenDates(start_date_long, end_date_long, selected_profileName);
            setAdapter(addDayArrayList);
            updateBottom(addDayArrayList);

        } else {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            String years = String.valueOf(year);

            addDayArrayList = new DatabaseOperations(LandingActivity.this).yearlyData(years, selected_profileName);
            setAdapter(addDayArrayList);
            updateBottom(addDayArrayList);
        }
        if (addDayArrayList.size() > 0) {
            no_data.setVisibility(View.GONE);
        } else {
            no_data.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onBackPressed() {

        if (mRevealView.getVisibility() == View.VISIBLE) {
            hideRevealView();
        } else
            super.onBackPressed();
    }


    String date_format = "";
    int startDay = 0, startMonth = 0, startYear = 0;
    int endDay = 0, endMonth = 0, endYear = 0;
    String selectedDate = "", date = "", start_date = "", end_date = "";

    public String getDatePicker(final int format_index, final int viewId, final EditText editText) {

        // Infalating Dialog for Date Picker
        final Dialog datePicker = new Dialog(LandingActivity.this);
        datePicker.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewDatePicker = inflater.inflate(R.layout.date_picker, null);
        datePicker.setContentView(viewDatePicker);

        if (format_index == 2) {
            date_format = "MM/dd/yyyy";
        } else if (format_index == 1) {
            date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
        } else {
            date_format = "MMM dd,yyyy";
        }

        // Getting date from Calendar View
        final CalendarView calendar = (CalendarView) viewDatePicker
                .findViewById(R.id.datePicker);
        final Button selectDate = (Button) viewDatePicker
                .findViewById(R.id.dateSelect);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {

                if (viewId == R.id.editText_01) {
                    startDay = dayOfMonth;
                    startMonth = month;
                    startYear = year;

                    /*start_calendar.set(Calendar.MONTH, startMonth);
                    start_calendar.set(Calendar.DAY_OF_MONTH, startDay);*/
                    startcalendar.set(startYear, startMonth, startDay);
//                    start_dateCalendar.set(startYear, startMonth, startDay);
                } else if (viewId == R.id.editText_02) {
                    endDay = dayOfMonth;
                    endMonth = month;
                    endYear = year;
                    endcalendar.set(endYear, endMonth, endDay);
//                    end_dateCalendar.set(endYear, endMonth, endDay);
                }


                selectedDate = String.valueOf(month + 1) + "/"
                        + String.valueOf(dayOfMonth) + "/"
                        + String.valueOf(year);
                System.out.println("Selected date: " + selectedDate);


                date = new SimpleDateFormat(date_format).format(new Date(
                        selectedDate));


                SimpleDateFormat sdfDate = new SimpleDateFormat(date_format);
                String currentDateString = sdfDate.format(new Date());
                Date currentDate = null;
                Date parsedStartDate = null;

                start_date = selectedDate;
                String date1 = new SimpleDateFormat(date_format).format(new Date(selectedDate));
                end_date = date1;
                try {
                    currentDate = sdfDate.parse(currentDateString);
                    parsedStartDate = sdfDate.parse(date);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

//                if (parsedStartDate.before(currentDate)) {
//                    Toast.makeText(AddDayActivity.this,
//                            "Past date cannot be selected!", Toast.LENGTH_LONG)
//                            .show();
//                    pastDateSelected = true;
//                } else {
//                    pastDateSelected = false;
//                }

//                if (pastDateSelected) {
//                    if (viewId == R.id.editText_start_shift) {
//                        editText_startShift.setText("Set Start Date");
//                    } else if (viewId == R.id.editText_end_shift) {
//                        editText_endShift.setText("Set End Date");
//                    }
//                } else {
                date = new SimpleDateFormat(date_format)
                        .format(new Date(date));
                if (viewId == R.id.editText_01) {
                    start_date = selectedDate;
                    editText.setText(selectedDate);
                    editor.putString(Constants.EditTextStart, selectedDate);
                    editor.commit();
                } else if (viewId == R.id.editText_02) {
                    date1 = new SimpleDateFormat(date_format).format(new Date(date));
                    end_date = date1;
//                        end_date = date;
                    editText.setText(date);
                    editor.putString(Constants.EditTextEnd, date);
                    editor.commit();
                }

//                }

            }
        });

        selectDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (viewId == R.id.editText_01) {

                    //   todayPayDay(global_startday, global_payperiod);
                }
                if (viewId == R.id.editText_01 && startDay == 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String d = sdf.format(new Date());
                    startYear = Integer.parseInt(d.substring(0, 4));
                    startMonth = Integer.parseInt(d.substring(4, 6));
                    startDay = Integer.parseInt(d.substring(6));
                    System.out.println(sdf.format(new Date()) + startYear
                            + startMonth + startDay);
                    String currentDate = String.valueOf(startMonth) + "/"
                            + String.valueOf(startDay) + "/"
                            + String.valueOf(startYear);
                    //  startDateDb = currentDate;
                    Date dates = new Date(currentDate);
                    // start_shift_long = dates.getTime();
                    date = new SimpleDateFormat(date_format)
                            .format(dates);

                    editor.putString(Constants.EditTextStart, selectedDate);
                    editor.commit();
                    editText.setText(date);

                } else if (viewId == R.id.editText_02 && startDay == 0) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String d = sdf.format(new Date());
                    endYear = Integer.parseInt(d.substring(0, 4));
                    endMonth = Integer.parseInt(d.substring(4, 6));
                    endDay = Integer.parseInt(d.substring(6));
                    System.out.println(sdf.format(new Date()) + endYear
                            + endMonth + endDay);
                    String currentDate = String.valueOf(endMonth) + "/"
                            + String.valueOf(endDay) + "/"
                            + String.valueOf(endYear);
                    Date dates = new Date(currentDate);
                    // end_shift_long = dates.getTime();
                    date = new SimpleDateFormat(date_format)
                            .format(dates);

                    // editText_endShift.setText(date);

                } else {

                    if (pastDateSelected) {
                        if (viewId == R.id.editText_01) {
                            editText.setText("Set Start Date");
                        } else if (viewId == R.id.editText_02) {
                            editText.setText("Set End Date");
                        }
                    } else {
                        // Toast.makeText(AddEventActivity.this,
                        // "Select start date once again!",
                        // Toast.LENGTH_SHORT).show();

                        if (viewId == R.id.editText_01) {
                            try {
                                Date selectedDate1 = new Date(selectedDate);
                                start_shift_long = selectedDate1.getTime();
                                date = new SimpleDateFormat(date_format)
                                        .format(selectedDate1);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                                String d = sdf.format(selectedDate1);
                                startYear = Integer.parseInt(d.substring(0, 4));
                                startMonth = Integer.parseInt(d.substring(4, 6));
                                startDay = Integer.parseInt(d.substring(6));
                                System.out.println(sdf.format(new Date()) + startYear
                                        + startMonth + startDay);
                                String currentDate = String.valueOf(startMonth) + "/"
                                        + String.valueOf(startDay) + "/"
                                        + String.valueOf(startYear);
                                //  startDateDb = currentDate;


                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                            }
                            editor.putString(Constants.EditTextStart, date);
                            editor.commit();
                            editText.setText(date);
                        }
                        if (viewId == R.id.editText_02) {
                            try {
                                Date selectedDate1 = new Date(selectedDate);
                                end_shift_long = selectedDate1.getTime();
                                date = new SimpleDateFormat(date_format)
                                        .format(selectedDate1);
                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                            }
                            editor.putString(Constants.EditTextEnd, date);
                            editor.commit();

                            editText.setText(date);
                        }

                    }
                }

                System.out.println("DATE>>>> " + date);

                datePicker.dismiss();

            }
        });

        // Selecting date from Date Dialog

        datePicker.show();
        return date;
    }

    public void setAdapter(ArrayList<AddDay> array) {
        adapter = new SummaryAdapter(default_date_format, LandingActivity.this, array);
        adapter.notifyDataSetChanged();
        mListView.setAdapter(adapter);
    }

    @Override
    protected void onPause() {
//        if (mGoogleApiClient != null) {
//            mGoogleApiClient.disconnect();
//        }
        super.onPause();
    }

    PdfWriter docWriter;
    String file_name;
    Runnable thread = new Runnable() {
        @Override
        public void run() {

            HeaderTable event = new HeaderTable();


            Document document = null;


            document = new Document(PageSize.A4, 36, 36, 36, 36);

            String root = Environment.getExternalStorageDirectory()
                    .toString();

            File myDir = new File(root + "/MyTipsDocument/");
            if (!myDir.exists()) {

                myDir.mkdirs();

            }
            Random generator = new Random();
            int n = 10000;

            n = generator.nextInt(n);

            String iname = "Doc" + n + ".pdf";
            file_name = iname;
            File file = new File(myDir, iname);


            try {
                docWriter = PdfWriter.getInstance(document, new FileOutputStream(file));
                // docWriter.setPageEvent(event);
                document.open();

                Font blackfont = FontFactory.getFont(FontFactory.HELVETICA, 13, Font.BOLD, BaseColor.BLACK);
                Font font = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.NORMAL, BaseColor.GRAY);

                PdfPCell taskcell = new PdfPCell();
                float[] taskcolumnWidths = {2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f};
                PdfPTable tasktable = new PdfPTable(taskcolumnWidths);

                tasktable.setWidthPercentage(100);
                tasktable.setExtendLastRow(true);
                tasktable.setTableEvent(new BorderEvent());
                tasktable.getDefaultCell().disableBorderSide(2);
                tasktable.getDefaultCell().disableBorderSide(3);


                PdfPCell cell = new PdfPCell();


                taskcell = new PdfPCell(new Phrase("Date", blackfont));
                taskcell.setHorizontalAlignment(Element.ALIGN_CENTER);

                taskcell.setBackgroundColor(BaseColor.WHITE);
                taskcell.setFixedHeight(20f);
                tasktable.addCell(taskcell);

                taskcell = new PdfPCell(new Phrase("Profile", blackfont));
                taskcell.setHorizontalAlignment(Element.ALIGN_CENTER);

                taskcell.setBackgroundColor(BaseColor.WHITE);
                taskcell.setFixedHeight(20f);
                tasktable.addCell(taskcell);
                taskcell = new PdfPCell(new Phrase("Working hours", blackfont));
                taskcell.setHorizontalAlignment(Element.ALIGN_CENTER);

                taskcell.setBackgroundColor(BaseColor.WHITE);
                taskcell.setFixedHeight(20f);

                tasktable.addCell(taskcell);


                taskcell = new PdfPCell(new Phrase("Live Tips", blackfont));
                taskcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                taskcell.setBackgroundColor(BaseColor.WHITE);
                taskcell.setFixedHeight(20f);

                tasktable.addCell(taskcell);


                taskcell = new PdfPCell(new Phrase("TD Count", blackfont));
                taskcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                taskcell.setBackgroundColor(BaseColor.WHITE);
                taskcell.setFixedHeight(20f);

                tasktable.addCell(taskcell);

                taskcell = new PdfPCell(new Phrase("Tip Out", blackfont));
                taskcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                taskcell.setBackgroundColor(BaseColor.WHITE);
                taskcell.setFixedHeight(20f);

                tasktable.addCell(taskcell);

                taskcell = new PdfPCell(new Phrase("Hourly Wage", blackfont));
                taskcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                taskcell.setBackgroundColor(BaseColor.WHITE);
                taskcell.setFixedHeight(20f);

                tasktable.addCell(taskcell);

                taskcell = new PdfPCell(new Phrase("Total", blackfont));
                taskcell.setHorizontalAlignment(Element.ALIGN_CENTER);
                taskcell.setBackgroundColor(BaseColor.WHITE);
                taskcell.setFixedHeight(20f);

                tasktable.addCell(taskcell);

                if (addDayArrayList.size() > 0) {
                    try {

                        for (int i = 0; i < addDayArrayList.size(); i++) {

                            Date d = new Date(addDayArrayList.get(i).getStart_long());
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(d);
                            Date date1 = cal.getTime();
                            if (default_date_format == 2) {
                                date_format = "MM/dd/yyyy";
                            } else if (default_date_format == 1) {
                                date_format = "E, MMM dd yyyy"; // E is for short name for Mon-Sun and EEEE full name of Monday-Sunday
                            } else {
                                date_format = "MMM dd,yyyy";
                            }
                            date = new SimpleDateFormat(date_format).format(date1);


                            cell = new PdfPCell(new Phrase(date, font));


                            cell.setPaddingTop(3);
                            cell.setFixedHeight(20f);
                            cell.setNoWrap(false);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                            cell.setBackgroundColor(BaseColor.WHITE);
                            cell.setPaddingBottom(3);

                            tasktable.addCell(cell);


                            cell = new PdfPCell(new Phrase(addDayArrayList.get(i).getProfile(), font));
                            cell.setPaddingTop(3);
                            cell.setPaddingBottom(3);
                            cell.setNoWrap(false);
                            cell.setFixedHeight(20f);

                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                            cell.setBackgroundColor(BaseColor.WHITE);
                            tasktable.addCell(cell);


                            cell = new PdfPCell(new Phrase(addDayArrayList.get(i).getCalculated_hours(), font));
                            cell.setPaddingTop(3);
                            cell.setPaddingBottom(3);
                            cell.setFixedHeight(20f);
                            cell.setNoWrap(false);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                            cell.setBackgroundColor(BaseColor.WHITE);
                            tasktable.addCell(cell);


                            String tips = addDayArrayList.get(i).getTotal_tips();
                            if (tips.equalsIgnoreCase("")) {
                                tips = "0";
                            }
                            cell = new PdfPCell(new Phrase(tips, font));
                            cell.setPaddingTop(3);
                            cell.setPaddingBottom(3);
                            cell.disableBorderSide(2);
                            cell.setNoWrap(false);
                            cell.setFixedHeight(20f);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                            cell.setBackgroundColor(BaseColor.WHITE);

                            tasktable.addCell(cell);


                            String count = addDayArrayList.get(i).getTounament_count();
                            if (count.equalsIgnoreCase("")) {
                                count = "0";

                            }
                            cell = new PdfPCell(new Phrase(count, font));
                            cell.setPaddingTop(3);
                            cell.setPaddingBottom(3);
                            cell.setNoWrap(false);
                            cell.setFixedHeight(20f);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                            cell.setBackgroundColor(BaseColor.WHITE);

                            tasktable.addCell(cell);


                            String tip_out = addDayArrayList.get(i).getTip_out();
                            if (tip_out.equalsIgnoreCase("")) {
                                tip_out = "0";

                            }
                            cell = new PdfPCell(new Phrase(tip_out, font));
                            cell.setPaddingTop(3);
                            cell.setPaddingBottom(3);
                            cell.setFixedHeight(20f);
                            cell.setNoWrap(false);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBackgroundColor(BaseColor.WHITE);
                            tasktable.addCell(cell);


                            String wages = addDayArrayList.get(i).getWages_hourly();
                            if (wages.equalsIgnoreCase("")) {
                                wages = "0";

                            }

                            cell = new PdfPCell(new Phrase(wages, font));
                            cell.setPaddingTop(3);
                            cell.setPaddingBottom(3);
                            cell.setNoWrap(false);

                            cell.setFixedHeight(20f);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

                            cell.setBackgroundColor(BaseColor.WHITE);
                            tasktable.addCell(cell);


                            String total_earns = addDayArrayList.get(i).getTotal_earnings();
                            if (total_earns.equalsIgnoreCase("")) {
                                total_earns = "0";

                            }
                            cell = new PdfPCell(new Phrase(total_earns, font));
                            cell.setPaddingTop(3);
                            cell.setPaddingBottom(3);
                            cell.setNoWrap(false);
                            cell.setFixedHeight(20f);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBackgroundColor(BaseColor.WHITE);
                            tasktable.addCell(cell);


                        }
                    } catch (Exception e) {
                        Log.i("pdf", e.toString());
                        e.printStackTrace();
                    }
                }
                try {
                    document.add(tasktable);
                    document.close();

                } catch (Exception e) {
                    Log.i("pdf1", e.toString());

                }


            } catch (FileNotFoundException e) {
                Log.i("pdf2", e.toString());
                e.printStackTrace();
            } catch (DocumentException e) {
                Log.i("pdf3", e.toString());
                e.printStackTrace();
            }


            sendEmail();

        }

    };

    public void sendEmail() {
        final String fromEmail = "mytipsdatabase@gmail.com";

        final ArrayList<String> arrayList = new ArrayList<>();
        String stored_email = sharedPreferences.getString("user_email", "");
        //arrayList.add("ekta@beesolvertechnology.com");
        arrayList.add(stored_email);
//
        final File pdf = new File(Environment.getExternalStorageDirectory()
                .toString() + "/MyTipsDocument/" + file_name);
        LandingActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new SendMailTask(LandingActivity.this).execute(fromEmail,
                        Constants.fromPassword, arrayList, "Subject", "body", pdf.getAbsolutePath());
            }
        });

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.i("drive", "connected called");
        if (mGoogleApiClient.isConnected()) {
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
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

        Log.i("drive", "connected suspend");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


        Log.i("drive", "connected failed");
//        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
//
//        if (mResolvingError) { // If already in resolution state, just return.
//            return;
//        } else if (connectionResult.hasResolution()) { // Error can be resolved by starting an intent with user interaction
//            mResolvingError = true;
//            try {
//                connectionResult.startResolutionForResult(this, DIALOG_ERROR_CODE);
//            } catch (IntentSender.SendIntentException e) {
//                e.printStackTrace();
//            }
//        } else { // Error cannot be resolved. Display Error Dialog stating the reason if possible.
//            ErrorDialogFragment fragment = new ErrorDialogFragment();
//            Bundle args = new Bundle();
//            args.putInt("error", connectionResult.getErrorCode());
//            fragment.setArguments(args);
//            fragment.show(getFragmentManager(), "errordialog");
//        }

        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
                progressDialog.dismiss();
            } catch (IntentSender.SendIntentException e) {
                // Unable to resolve, message user appropriately
            }
        } else {
            GooglePlayServicesUtil.getErrorDialog(connectionResult.getErrorCode(), this, 0).show();
            progressDialog.dismiss();
        }

    }

    private class HeaderTable extends PdfPageEventHelper {
        private HeaderTable() {

        }

    }

    private class BorderEvent implements PdfPTableEvent {
        public void tableLayout(PdfPTable table, float[][] widths, float[] heights, int headerRows, int rowStart, PdfContentByte[] canvases) {
            float width[] = widths[0];
            float x1 = width[0];
            float x2 = width[width.length - 1];
            float y1 = heights[0];
            float y2 = heights[heights.length - 1];
            PdfContentByte cb = canvases[PdfPTable.LINECANVAS];
            cb.rectangle(x1, y1, x2 - x1, y2 - y1);
            cb.stroke();
            cb.resetRGBColorStroke();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
//            mGoogleApiClient.connect(); // Connect the client to Google Drive
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //  mGoogleApiClient.disconnect();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_RESOLUTION:
                if (resultCode == RESULT_OK) {
                    //   uploadToDrive();

                }
                break;
        }
    }

    public void uploadToDrive() {

        create_file_in_folder();
    }

    ProgressDialog progressDialog;

    public void create_file_in_folder() {

        progressDialog = new ProgressDialog(LandingActivity.this);
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

    public boolean checkPermissions() {
        int permissionWrite = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        int camera_permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int finger_print = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            finger_print = ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT);
        }

        int accounts = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

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

        if (finger_print != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.USE_FINGERPRINT);
        }

        if (accounts != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.GET_ACCOUNTS);
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
