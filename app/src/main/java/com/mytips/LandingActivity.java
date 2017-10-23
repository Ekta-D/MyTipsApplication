package com.mytips;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.mytips.Adapter.SpinnerProfile;
import com.mytips.Adapter.SummaryAdapter;
import com.mytips.Database.DatabaseOperations;
import com.mytips.Model.AddDay;
import com.mytips.Model.Profiles;
import com.mytips.Utils.Constants;
import com.mytips.Utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mRevealView;
    private boolean hidden = true;
    private ImageButton add_day, profile, share, invite, preferences, backup;
    Spinner spinnerProfile, spinnerReportType;
    ListView mListView;
    String[] reportTypeArray = new String[]{"Daily", "Weekly", "Bi-Weekly", "Monthly", "Yearly"};
    Toolbar toolbar;
    ArrayList<AddDay> addDayArrayList;
    DatabaseOperations databaseOperations;

    Context context;
    TextView textView_total_earnings, textView_summaryTips, textView_summery_tds, textView_summery_tip_out, textView_hour_wage;
    RelativeLayout summery_tds_layout, summery_tip_outLayout, dashboard_bottm;

    String selected_ProfileID;
    ArrayList<AddDay> updated_spinner;
    String selected_profileName;
    ArrayList<Profiles> profiles;
    TextView no_data;
    String start_week;
    String start_shift = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);


        context = LandingActivity.this;
        databaseOperations = new DatabaseOperations(LandingActivity.this);
        updated_spinner = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        no_data = (TextView) findViewById(R.id.no_data_landing);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            window.setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
        mRevealView.setVisibility(View.INVISIBLE);
        dashboard_bottm = (RelativeLayout) findViewById(R.id.dashboard_total_earnings);
        add_day = (ImageButton) findViewById(R.id.add_day);
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




        //  Log.i("add_daylist", String.valueOf(addDayArrayList.size()));
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, reportTypeArray);
        spinnerReportType.setAdapter(typeAdapter);

        spinnerProfile.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (profiles.size() > 0) {

//                    selected_ProfileID = addDayArrayList.get(position).getId();
//                    selected_profileName = addDayArrayList.get(position).getProfile();
                    selected_profileName = profiles.get(position).getProfile_name();
                    start_week = profiles.get(position).getStartday();
                    if (addDayArrayList.size() > 0) {
                        start_shift = addDayArrayList.get(position).getStart_shift();
                    }
                    updateView(addDayArrayList, selected_ProfileID, selected_profileName);
                }
                spinnerProfile.setSelection(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerReportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerReportType.setSelection(position);
                changeData(parent.getSelectedItem().toString(), start_week, start_shift);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                final AddDay addDay = selectedProfile.get(position);

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
                            selectedProfile.remove(position);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });

                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            }
        });

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
                startActivity(new Intent(getBaseContext(), SettingsActivity.class));
                break;
            case R.id.share:

                break;
            case R.id.invite:

                break;
            case R.id.backup:

                break;
        }
    }

    SummaryAdapter adapter;
    ArrayList<AddDay> selectedProfile;

    public void updateView(ArrayList<AddDay> addDayArrayList, String selected_ProfileID, String selected_profileName) {
//        for (int i = 0; i < addDayArrayList.size(); i++) {
//            profileArray.add(addDayArrayList.get(i).getProfile());
//        }
        selectedProfile = new ArrayList<>();
        for (int i = 0; i < addDayArrayList.size(); i++) {
            AddDay addDay = addDayArrayList.get(i);
            if (addDay.getProfile().equalsIgnoreCase(selected_profileName)) {
                selectedProfile.add(addDay);
            }

        }
        if (selectedProfile.size() > 0) {
            mListView.setVisibility(View.VISIBLE);
            //     dashboard_bottm.setVisibility(View.VISIBLE);
            adapter = new SummaryAdapter(LandingActivity.this, selectedProfile);
            mListView.setAdapter(adapter);
            no_data.setVisibility(View.GONE);
            updateBottom(selectedProfile);
        } else {
            no_data.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.GONE);
            updateBottom(selectedProfile);
            //   dashboard_bottm.setVisibility(View.GONE);
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
        textView_total_earnings.setText(String.valueOf(totalEarnings));


        textView_summaryTips.setText("Live Tips:$" + String.valueOf(total_livetips) + ", ");
        textView_summery_tds.setText("Tournament Downs:$" + String.valueOf(tournament_totalTD) + ", ");
        textView_summery_tip_out.setText("Tip-out:$" + String.valueOf(totalOuts) + ", ");
        textView_hour_wage.setText("Hourly Wage:$" + String.valueOf(hrs));
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
        } else {
            String[] profiles_empty = new String[]{"[none]"};
            ArrayAdapter<String> profileAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.spinner_text_view, R.id.text_spinner, profiles_empty);
            spinnerProfile.setAdapter(profileAdapter);
        }
    }

    public void changeData(String spinner_selected, String start_week, String start_shift) {

        ArrayList<AddDay> fetched_list;
        String weekday_name = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(System.currentTimeMillis());

        Log.i("weekday", weekday_name + " " + start_week);
        if (spinner_selected.equalsIgnoreCase("Daily")) {

        } else if (spinner_selected.equalsIgnoreCase("Weekly")) {

            fetched_list = new ArrayList<>();

//            int day = getDay(start_week);
//            int day2 = getDay(weekday_name);
//
//            int days_diff = day2 - day;
//            Date date = null;
//            String pattern = "MMMM d, yyyy";
//            if (!start_shift.equalsIgnoreCase("")) {
//                try {
//                    date = new SimpleDateFormat(pattern).parse(start_shift);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//            }

            Date resetLastweek = DateUtils.resetLastweek(Constants.globaldate);
            Date currentWeek = DateUtils.resetFromDate(Constants.globaldate);

            long reset_week = resetLastweek.getTime();
            long reset_current = currentWeek.getTime();

            fetched_list = new DatabaseOperations(LandingActivity.this).fetchWeeklyData(reset_week, reset_current);
            Log.i("fetched_list", fetched_list.toString());


        } else if (spinner_selected.equalsIgnoreCase("Bi-Weekly")) {

        } else if (spinner_selected.equalsIgnoreCase("Monthly")) {

        } else {

        }
    }

    public int getDay(String start_week) {
        int day = 0;

        switch (start_week) {

            case "Sunday":
                day = 1;
                break;
            case "Monday":
                day = 2;
                break;
            case "Tuesday":
                day = 3;
                break;
            case "Wednesday":
                day = 4;
                break;
            case "Thursday":
                day = 5;
                break;
            case "Friday":
                day = 6;
                break;
            case "Saturday":
                day = 7;
                break;

        }
        return day;
    }

    public static long daysBetween(Calendar startDate, Calendar endDate) {
        Calendar start = Calendar.getInstance();
        start.setTimeZone(startDate.getTimeZone());
        start.setTimeInMillis(startDate.getTimeInMillis());

        Calendar end = Calendar.getInstance();
        end.setTimeZone(endDate.getTimeZone());
        end.setTimeInMillis(endDate.getTimeInMillis());

        // Set the copies to be at midnight, but keep the day information.

        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);

        // At this point, each calendar is set to midnight on
        // their respective days. Now use TimeUnit.MILLISECONDS to
        // compute the number of full days between the two of them.

        return TimeUnit.MILLISECONDS.toDays(
                Math.abs(end.getTimeInMillis() - start.getTimeInMillis()));
    }


    @Override
    protected void onResume() {

        profiles = new DatabaseOperations(LandingActivity.this).fetchAllProfile(LandingActivity.this);
        updateSpinner(profiles);
        super.onResume();
    }
}
